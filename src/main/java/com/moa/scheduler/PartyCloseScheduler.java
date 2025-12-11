package com.moa.scheduler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.moa.dao.partymember.PartyMemberDao;
import com.moa.dao.party.PartyDao;
import com.moa.dao.product.ProductDao;
import com.moa.domain.Party;
import com.moa.domain.PartyMember;
import com.moa.domain.Product;
import com.moa.domain.enums.PushCodeType;
import com.moa.dto.push.request.TemplatePushRequest;
import com.moa.service.party.PartyService;
import com.moa.service.push.PushService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 파티 종료 스케줄러
 * 
 * 기능:
 * - 매일 새벽 3시에 종료일이 지난 파티를 자동으로 종료
 * - 파티 종료 시 모든 멤버의 보증금을 Toss Payments 결제 취소를 통해 환불
 *
 * @author MOA Team
 * @since 2025-12-08
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PartyCloseScheduler {

    private final PartyDao partyDao;
    private final PartyService partyService;
    
    // ========== 푸시알림 추가 ==========
    private final PushService pushService;
    private final ProductDao productDao;
    private final PartyMemberDao partyMemberDao;
    // ========== 푸시알림 추가 끝 ==========

    /**
     * 매일 새벽 3시 - 종료일이 지난 파티 자동 종료
     * 
     * Cron: 0 0 3 * * * = 매일 03:00:00
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void closeExpiredParties() {
        log.info("===== Party Close Scheduler Started =====");

        try {
            LocalDateTime now = LocalDateTime.now();
            List<Party> expiredParties = partyDao.findExpiredActiveParties(now);

            if (expiredParties.isEmpty()) {
                log.info("No expired parties found");
                return;
            }

            log.info("Found {} expired parties to close", expiredParties.size());

            int successCount = 0;
            int failureCount = 0;

            for (Party party : expiredParties) {
                try {
                    // closeParty 호출 (보증금 환불 + 상태 변경)
                    partyService.closeParty(party.getPartyId(), party.getPartyLeaderId());
                    successCount++;
                    log.info("Successfully closed party: partyId={}", party.getPartyId());
                    
                    // ========== 푸시알림 추가: 파티 종료 알림 ==========
                    sendPartyClosedPush(party);
                    // ========== 푸시알림 추가 끝 ==========
                    
                } catch (Exception e) {
                    failureCount++;
                    log.error("Failed to close party: partyId={}, error={}",
                            party.getPartyId(), e.getMessage(), e);
                    // 개별 파티 실패가 전체 프로세스를 중단시키지 않도록 계속 진행
                }
            }

            log.info("Party close processing completed: success={}, failure={}", successCount, failureCount);

        } catch (Exception e) {
            log.error("Party close scheduler failed", e);
        } finally {
            log.info("===== Party Close Scheduler Finished =====");
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
     * 푸시알림 추가: 파티 종료 알림 (모든 멤버에게)
     */
    private void sendPartyClosedPush(Party party) {
        try {
            String productName = getProductName(party.getProductId());

            // 파티의 모든 멤버 조회
            List<PartyMember> members = partyMemberDao.findActiveByPartyId(party.getPartyId());

            for (PartyMember member : members) {
                try {
                    Map<String, String> params = Map.of(
                        "productName", productName
                    );

                    TemplatePushRequest pushRequest = TemplatePushRequest.builder()
                        .receiverId(member.getUserId())
                        .pushCode(PushCodeType.PARTY_CLOSED.getCode())
                        .params(params)
                        .moduleId(String.valueOf(party.getPartyId()))
                        .moduleType(PushCodeType.PARTY_CLOSED.getModuleType())
                        .build();

                    pushService.addTemplatePush(pushRequest);
                    log.info("푸시알림 발송 완료: PARTY_CLOSED -> userId={}", member.getUserId());

                } catch (Exception e) {
                    log.error("푸시알림 발송 실패: userId={}, error={}", member.getUserId(), e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("푸시알림 발송 실패: partyId={}, error={}", party.getPartyId(), e.getMessage());
        }
    }
    // ========== 푸시알림 추가 끝 ==========
}