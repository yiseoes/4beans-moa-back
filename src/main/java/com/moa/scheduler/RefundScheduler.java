package com.moa.scheduler;

import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.moa.dao.deposit.DepositDao;
import com.moa.dao.party.PartyDao;
import com.moa.dao.product.ProductDao;
import com.moa.domain.Deposit;
import com.moa.domain.Party;
import com.moa.domain.Product;
import com.moa.domain.RefundRetryHistory;
import com.moa.domain.enums.PushCodeType;
import com.moa.dto.push.request.TemplatePushRequest;
import com.moa.service.push.PushService;
import com.moa.service.refund.RefundRetryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Refund Scheduler
 * Processes failed deposit refund retries automatically
 *
 * Schedule:
 * - Every hour: Check for pending refund retries and process them
 *
 * Retry Strategy:
 * - Attempt 1: Immediate (on initial refund failure)
 * - Attempt 2: +1 hour after first failure
 * - Attempt 3: +4 hours after second failure
 * - Attempt 4: +24 hours after third failure (final attempt)
 *
 * @author MOA Team
 * @since 2025-12-06
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RefundScheduler {

    private final RefundRetryService refundRetryService;
    
    // ========== 푸시알림 추가 ==========
    private final PushService pushService;
    private final DepositDao depositDao;
    private final PartyDao partyDao;
    private final ProductDao productDao;
    // ========== 푸시알림 추가 끝 ==========

    /**
     * Process pending refund retries
     * Runs every hour to check for failed refunds that need retry
     *
     * Cron: 0 0 * * * * = Every hour at :00 minutes
     */
    @Scheduled(cron = "0 0 * * * *")
    public void processRefundRetries() {
        log.info("===== Refund Retry Scheduler Started =====");

        try {
            // 1. Find all pending refund retries
            List<RefundRetryHistory> pendingRetries = refundRetryService.findPendingRetries();

            if (pendingRetries.isEmpty()) {
                log.info("No pending refund retries found");
                return;
            }

            log.info("Processing {} pending refund retries", pendingRetries.size());

            // 2. Process each retry
            int successCount = 0;
            int failureCount = 0;

            for (RefundRetryHistory retry : pendingRetries) {
                try {
                    refundRetryService.retryRefund(retry);
                    successCount++;
                    
                    // ========== 푸시알림 추가: 환불 재시도 성공 알림 ==========
                    sendRefundSuccessPush(retry);
                    // ========== 푸시알림 추가 끝 ==========
                    
                } catch (Exception e) {
                    log.error("Failed to process refund retry: retryId={}, depositId={}, error={}",
                            retry.getRetryId(), retry.getDepositId(), e.getMessage(), e);
                    failureCount++;
                    // Continue with next retry (isolation)
                }
            }

            log.info("Refund retry processing completed: success={}, failure={}", successCount, failureCount);

        } catch (Exception e) {
            log.error("Refund retry scheduler failed", e);
        } finally {
            log.info("===== Refund Retry Scheduler Finished =====");
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
     * 푸시알림 추가: 환불 재시도 성공 알림
     */
    private void sendRefundSuccessPush(RefundRetryHistory retry) {
        try {
            // 보증금 정보 조회
            Deposit deposit = depositDao.findById(retry.getDepositId()).orElse(null);
            if (deposit == null) return;

            // 파티 정보 조회
            Party party = partyDao.findById(deposit.getPartyId()).orElse(null);
            if (party == null) return;

            String productName = getProductName(party.getProductId());

            Map<String, String> params = Map.of(
                "productName", productName,
                "amount", String.valueOf(retry.getRefundAmount() != null ? retry.getRefundAmount() : deposit.getDepositAmount())
            );

            TemplatePushRequest pushRequest = TemplatePushRequest.builder()
                .receiverId(deposit.getUserId())
                .pushCode(PushCodeType.REFUND_SUCCESS.getCode())
                .params(params)
                .moduleId(String.valueOf(deposit.getDepositId()))
                .moduleType(PushCodeType.REFUND_SUCCESS.getModuleType())
                .build();

            pushService.addTemplatePush(pushRequest);
            log.info("푸시알림 발송 완료: REFUND_SUCCESS -> userId={}", deposit.getUserId());

        } catch (Exception e) {
            log.error("푸시알림 발송 실패: retryId={}, error={}", retry.getRetryId(), e.getMessage());
        }
    }
    // ========== 푸시알림 추가 끝 ==========
}