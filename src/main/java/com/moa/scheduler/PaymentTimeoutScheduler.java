package com.moa.scheduler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.moa.dao.party.PartyDao;
import com.moa.dao.product.ProductDao;
import com.moa.domain.Party;
import com.moa.domain.Product;
import com.moa.domain.enums.PartyStatus;
import com.moa.domain.enums.PushCodeType;
import com.moa.dto.push.request.TemplatePushRequest;
import com.moa.service.party.PartyService;
import com.moa.service.push.PushService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 결제 타임아웃 스케줄러
 * 
 * PENDING_PAYMENT 상태로 30분 이상 경과한 파티를 자동으로 취소합니다.
 * 5분마다 실행됩니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTimeoutScheduler {

    private final PartyDao partyDao;
    private final PartyService partyService;
    
    // ========== 푸시알림 추가 ==========
    private final PushService pushService;
    private final ProductDao productDao;
    // ========== 푸시알림 추가 끝 ==========

    // 타임아웃 시간 (분)
    private static final int TIMEOUT_MINUTES = 30;

    /**
     * 5분마다 실행되는 결제 타임아웃 체크
     * PENDING_PAYMENT 상태로 30분 이상 경과한 파티를 취소합니다.
     */
    @Scheduled(fixedRate = 5 * 60 * 1000) // 5분마다 실행
    public void checkPaymentTimeout() {
        log.info("결제 타임아웃 체크 시작");

        try {
            // 30분 전 시간 계산
            LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(TIMEOUT_MINUTES);

            // PENDING_PAYMENT 상태이고 생성 시간이 30분 이상 경과한 파티 조회
            List<Party> expiredParties = partyDao.findExpiredPendingPaymentParties(
                    PartyStatus.PENDING_PAYMENT, 
                    timeoutThreshold
            );

            if (expiredParties.isEmpty()) {
                log.info("타임아웃된 파티가 없습니다.");
                return;
            }

            log.info("타임아웃된 파티 {}개 발견", expiredParties.size());

            // 각 파티 취소 처리
            for (Party party : expiredParties) {
                try {
                    partyService.cancelExpiredParty(
                            party.getPartyId(),
                            "결제 타임아웃 (30분 초과)"
                    );
                    log.info("파티 취소 완료: partyId={}", party.getPartyId());
                    
                    // ========== 푸시알림 추가: 결제 타임아웃 알림 ==========
                    sendPaymentTimeoutPush(party);
                    // ========== 푸시알림 추가 끝 ==========
                    
                } catch (Exception e) {
                    log.error("파티 취소 실패: partyId={}, error={}", 
                            party.getPartyId(), e.getMessage());
                }
            }

            log.info("결제 타임아웃 체크 완료: 처리된 파티 {}개", expiredParties.size());

        } catch (Exception e) {
            log.error("결제 타임아웃 체크 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    // ============================================
    // 푸시알림 추가: Private 메서드들
    // ============================================

    /**
     * 푸시알림 추가: 상품명 조회 헬퍼 메서드
     */
    private String getProductName(Integer productId) {
        if (productId == null) return "OTT 서비스";
        
        try {
            Product product = productDao.getProduct(productId);
            return (product != null && product.getProductName() != null) 
                ? product.getProductName() : "OTT 서비스";
        } catch (Exception e) {
            log.warn("상품 조회 실패: productId={}", productId);
            return "OTT 서비스";
        }
    }

    /**
     * 푸시알림 추가: 결제 타임아웃 알림 (방장에게)
     */
    private void sendPaymentTimeoutPush(Party party) {
        try {
            String productName = getProductName(party.getProductId());

            Map<String, String> params = Map.of(
                "productName", productName,
                "timeoutMinutes", String.valueOf(TIMEOUT_MINUTES)
            );

            TemplatePushRequest pushRequest = TemplatePushRequest.builder()
                .receiverId(party.getPartyLeaderId())
                .pushCode(PushCodeType.PAY_TIMEOUT.getCode())
                .params(params)
                .moduleId(String.valueOf(party.getPartyId()))
                .moduleType(PushCodeType.PAY_TIMEOUT.getModuleType())
                .build();

            pushService.addTemplatePush(pushRequest);
            log.info("푸시알림 발송 완료: PAY_TIMEOUT -> userId={}", party.getPartyLeaderId());

        } catch (Exception e) {
            log.error("푸시알림 발송 실패: partyId={}, error={}", party.getPartyId(), e.getMessage());
        }
    }
    // ========== 푸시알림 추가 끝 ==========
}