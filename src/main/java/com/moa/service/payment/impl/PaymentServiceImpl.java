package com.moa.service.payment.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.moa.common.exception.BusinessException;
import com.moa.common.exception.ErrorCode;
import com.moa.dao.payment.PaymentDao;
import com.moa.dao.user.UserCardDao;
import com.moa.domain.Payment;
import com.moa.domain.UserCard;
import com.moa.domain.enums.PaymentStatus;
import com.moa.dto.payment.request.PaymentRequest;
import com.moa.dto.payment.response.PaymentDetailResponse;
import com.moa.dto.payment.response.PaymentResponse;
import com.moa.common.event.MonthlyPaymentCompletedEvent;
import com.moa.common.event.MonthlyPaymentFailedEvent;
import com.moa.service.payment.PaymentRetryService;
import com.moa.service.payment.PaymentService;
import com.moa.service.payment.TossPaymentService;

import lombok.RequiredArgsConstructor;

/**
 * 결제 서비스 구현체
 *
 * v1.0 가정:
 * - 모든 결제는 즉시 성공 (Happy Path)
 * - Toss Payments API는 항상 성공
 * - 결제 실패 케이스 없음
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

        private final PaymentDao paymentDao;
        private final TossPaymentService tossPaymentService;
        private final UserCardDao userCardDao;
        private final PaymentRetryService retryService;
        private final ApplicationEventPublisher eventPublisher;

        private static final int MAX_RETRY_ATTEMPTS = 4;

        @Override
        public Payment createInitialPayment(
                        Integer partyId,
                        Integer partyMemberId,
                        String userId,
                        Integer amount,
                        String targetMonth,
                        PaymentRequest request) {

                // 1. 중복 결제 확인
                if (isDuplicatePayment(partyMemberId, targetMonth)) {
                        throw new BusinessException(ErrorCode.DUPLICATE_PAYMENT);
                }

                // 2. 결제 금액 검증
                if (amount <= 0) {
                        throw new BusinessException(ErrorCode.INVALID_PAYMENT_AMOUNT);
                }

                // 3. orderId 중복 확인 (토스 API 중복 호출 방지)
                if (paymentDao.findByOrderId(request.getOrderId()).isPresent()) {
                        // 이미 처리된 orderId면 기존 결제 정보 반환
                        return paymentDao.findByOrderId(request.getOrderId()).get();
                }

                // 4. Toss Payments 결제 승인
                tossPaymentService.confirmPayment(
                                request.getTossPaymentKey(),
                                request.getOrderId(),
                                amount);

                // 5. Payment 엔티티 생성
                Payment payment = Payment.builder()
                                .partyId(partyId)
                                .partyMemberId(partyMemberId)
                                .userId(userId)
                                .paymentType("INITIAL") // 첫 달 결제
                                .paymentAmount(amount)
                                .paymentStatus(PaymentStatus.COMPLETED) // v1.0: 즉시 완료
                                .paymentMethod(request.getPaymentMethod())
                                .paymentDate(LocalDateTime.now())
                                .tossPaymentKey(request.getTossPaymentKey())
                                .orderId(request.getOrderId())
                                .targetMonth(targetMonth)
                                .build();

                // 6. DB 저장
                paymentDao.insertPayment(payment);

                return payment;
        }

        @Override
        public Payment createMonthlyPayment(
                        Integer partyId,
                        Integer partyMemberId,
                        String userId,
                        Integer amount,
                        String targetMonth) {

                // 1. 중복 결제 확인
                if (isDuplicatePayment(partyMemberId, targetMonth)) {
                        throw new BusinessException(ErrorCode.DUPLICATE_PAYMENT);
                }

                // 2. 빌링키 조회
                UserCard userCard = userCardDao.findByUserId(userId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.BILLING_KEY_NOT_FOUND));

                // 3. 주문 ID 생성
                String orderId = "MONTHLY_" + partyId + "_" + partyMemberId + "_" + System.currentTimeMillis();

                // 4. Toss Payments 빌링키 결제 요청
                String paymentKey = tossPaymentService.payWithBillingKey(
                                userCard.getBillingKey(),
                                orderId,
                                amount,
                                "MOA 월 구독료 (" + targetMonth + ")");

                // 5. Payment 엔티티 생성
                Payment payment = Payment.builder()
                                .partyId(partyId)
                                .partyMemberId(partyMemberId)
                                .userId(userId)
                                .paymentType("MONTHLY") // 월별 자동 결제
                                .paymentAmount(amount)
                                .paymentStatus(PaymentStatus.COMPLETED)
                                .paymentMethod("CARD") // 빌링키 결제도 카드로 간주
                                .paymentDate(LocalDateTime.now())
                                .targetMonth(targetMonth)
                                .tossPaymentKey(paymentKey) // 결제 키 저장
                                .orderId(orderId) // 주문 ID 저장
                                .cardNumber(userCard.getCardNumber()) // 카드 번호 저장
                                .cardCompany(userCard.getCardCompany()) // 카드사 저장
                                .build();

                // 6. DB 저장
                paymentDao.insertPayment(payment);

                return payment;
        }

        @Override
        public Payment createDepositPayment(
                        Integer partyId,
                        Integer partyMemberId,
                        String userId,
                        Integer amount,
                        String targetMonth,
                        PaymentRequest request) {

                // 1. Payment 엔티티 생성
                Payment payment = Payment.builder()
                                .partyId(partyId)
                                .partyMemberId(partyMemberId)
                                .userId(userId)
                                .paymentType("DEPOSIT") // 보증금
                                .paymentAmount(amount)
                                .paymentStatus(PaymentStatus.COMPLETED) // v1.0: 즉시 완료
                                .paymentMethod(request.getPaymentMethod())
                                .paymentDate(LocalDateTime.now())
                                .tossPaymentKey(request.getTossPaymentKey())
                                .orderId(request.getOrderId())
                                .targetMonth(targetMonth)
                                .build();

                // 2. DB 저장
                paymentDao.insertPayment(payment);

                return payment;
        }

        @Override
        public Payment createInitialPaymentWithoutConfirm(
                        Integer partyId,
                        Integer partyMemberId,
                        String userId,
                        Integer amount,
                        String targetMonth,
                        PaymentRequest request) {

                // 1. 중복 결제 확인
                if (isDuplicatePayment(partyMemberId, targetMonth)) {
                        throw new BusinessException(ErrorCode.DUPLICATE_PAYMENT);
                }

                // 2. 결제 금액 검증
                if (amount <= 0) {
                        throw new BusinessException(ErrorCode.INVALID_PAYMENT_AMOUNT);
                }

                // 3. Toss 승인은 이미 createDeposit에서 완료됨 - 생략

                // 4. Payment 엔티티 생성
                Payment payment = Payment.builder()
                                .partyId(partyId)
                                .partyMemberId(partyMemberId)
                                .userId(userId)
                                .paymentType("INITIAL") // 첫 달 결제
                                .paymentAmount(amount)
                                .paymentStatus(PaymentStatus.COMPLETED) // v1.0: 즉시 완료
                                .paymentMethod(request.getPaymentMethod())
                                .paymentDate(LocalDateTime.now())
                                .tossPaymentKey(request.getTossPaymentKey())
                                .orderId(request.getOrderId())
                                .targetMonth(targetMonth)
                                .build();

                // 5. DB 저장
                paymentDao.insertPayment(payment);

                return payment;
        }

        @Override
        @Transactional(readOnly = true)
        public PaymentDetailResponse getPaymentDetail(Integer paymentId) {
                return paymentDao.findDetailById(paymentId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
        }

        @Override
        @Transactional(readOnly = true)
        public List<PaymentResponse> getMyPayments(String userId) {
                return paymentDao.findByUserId(userId);
        }

        @Override
        @Transactional(readOnly = true)
        public List<PaymentResponse> getPartyPayments(Integer partyId) {
                return paymentDao.findByPartyId(partyId);
        }

        @Override
        @Transactional(readOnly = true)
        public boolean isDuplicatePayment(Integer partyMemberId, String targetMonth) {
                return paymentDao.findByPartyMemberIdAndTargetMonth(partyMemberId, targetMonth)
                                .isPresent();
        }

        // ============================================
        // New Methods for Monthly Auto-Payment with Retry Logic
        // ============================================

        @Override
        @Transactional
        public void processMonthlyPayment(
                        Integer partyId,
                        Integer partyMemberId,
                        String userId,
                        Integer amount,
                        String targetMonth) {

                // 1. Check for duplicate payment
                if (isDuplicatePayment(partyMemberId, targetMonth)) {
                        return;
                }

                // 2. Create PENDING payment record
                Payment payment = Payment.builder()
                                .partyId(partyId)
                                .partyMemberId(partyMemberId)
                                .userId(userId)
                                .paymentType("MONTHLY")
                                .paymentAmount(amount)
                                .paymentStatus(PaymentStatus.PENDING) // Start as PENDING
                                .paymentMethod("CARD")
                                .paymentDate(LocalDateTime.now())
                                .targetMonth(targetMonth)
                                .orderId("MONTHLY_" + partyId + "_" + partyMemberId + "_" + System.currentTimeMillis())
                                .build();

                paymentDao.insertPayment(payment);

                // 3. Attempt execution (separate transaction)
                attemptPaymentExecution(payment, 1);
        }

        @Override
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void attemptPaymentExecution(Payment payment, int attemptNumber) {
                try {
                        // 1. Get billing key
                        UserCard userCard = userCardDao.findByUserId(payment.getUserId())
                                        .orElseThrow(() -> new BusinessException(ErrorCode.BILLING_KEY_NOT_FOUND));

                        // 2. Call Toss API with billing key
                        String paymentKey = tossPaymentService.payWithBillingKey(
                                        userCard.getBillingKey(),
                                        payment.getOrderId(),
                                        payment.getPaymentAmount(),
                                        "MOA 월 구독료 (" + payment.getTargetMonth() + ")");

                        // 3. Update payment to COMPLETED
                        payment.setPaymentStatus(PaymentStatus.COMPLETED);
                        payment.setTossPaymentKey(paymentKey);
                        payment.setCardNumber(userCard.getCardNumber());
                        payment.setCardCompany(userCard.getCardCompany());
                        paymentDao.updatePaymentStatus(payment.getPaymentId(), "COMPLETED");

                        // 4. Record success in retry history
                        retryService.recordSuccess(payment, attemptNumber);

                        // 5. Publish success event
                        eventPublisher.publishEvent(new MonthlyPaymentCompletedEvent(
                                        payment.getPartyId(),
                                        payment.getPartyMemberId(),
                                        payment.getUserId(),
                                        payment.getPaymentAmount(),
                                        payment.getTargetMonth()));

                } catch (BusinessException e) {
                        // Payment failed - handle failure and schedule retry
                        handlePaymentFailure(payment, attemptNumber, e);
                }
        }

        private void handlePaymentFailure(Payment payment, int attemptNumber, BusinessException e) {
                // 1. Update payment status to FAILED
                paymentDao.updatePaymentStatus(payment.getPaymentId(), "FAILED");

                // 2. Determine if retry should be scheduled
                boolean shouldRetry = attemptNumber < MAX_RETRY_ATTEMPTS;

                if (shouldRetry) {
                        // Schedule retry with exponential backoff
                        LocalDateTime nextRetry = calculateNextRetryTime(attemptNumber);
                        retryService.recordFailureWithRetry(
                                        payment,
                                        attemptNumber,
                                        e.getErrorCode().getCode(),
                                        e.getMessage(),
                                        nextRetry);
                } else {
                        // Max retries exceeded - permanent failure
                        retryService.recordPermanentFailure(payment, attemptNumber, e);

                        // Publish failure event for notification
                        eventPublisher.publishEvent(new MonthlyPaymentFailedEvent(
                                        payment.getPartyId(),
                                        payment.getPartyMemberId(),
                                        payment.getUserId(),
                                        payment.getTargetMonth(),
                                        e.getMessage()));
                }
        }

        private LocalDateTime calculateNextRetryTime(int attemptNumber) {
                // Exponential backoff: 24h, 48h, 72h
                int hoursToAdd = 24 * attemptNumber;
                return LocalDateTime.now().plusHours(hoursToAdd);
        }

        // ============================================
        // Manual Retry Methods (User-initiated)
        // ============================================

        @Override
        @Transactional
        public PaymentDetailResponse retryFailedPayment(Integer paymentId, String userId) {
                // 1. 결제 정보 조회
                Payment payment = paymentDao.findById(paymentId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

                // 2. 결제 소유자 확인
                if (!payment.getUserId().equals(userId)) {
                        throw new BusinessException(ErrorCode.UNAUTHORIZED, "본인의 결제만 재시도할 수 있습니다.");
                }

                // 3. 결제 상태 확인 (FAILED만 재시도 가능)
                if (payment.getPaymentStatus() != PaymentStatus.FAILED) {
                        throw new BusinessException(ErrorCode.INVALID_PAYMENT_STATUS, "실패한 결제만 재시도할 수 있습니다.");
                }

                // 4. 재시도 횟수 확인
                int attemptCount = getRetryAttemptCount(paymentId);
                if (attemptCount >= MAX_RETRY_ATTEMPTS) {
                        throw new BusinessException(ErrorCode.MAX_RETRY_EXCEEDED, "최대 재시도 횟수를 초과했습니다. 고객센터에 문의해주세요.");
                }

                // 5. 빌링키 조회
                UserCard userCard = userCardDao.findByUserId(userId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.BILLING_KEY_NOT_FOUND));

                // 6. 새 주문 ID 생성
                String newOrderId = "RETRY_" + paymentId + "_" + System.currentTimeMillis();

                try {
                        // 7. Toss Payments 빌링키 결제 요청
                        String paymentKey = tossPaymentService.payWithBillingKey(
                                        userCard.getBillingKey(),
                                        newOrderId,
                                        payment.getPaymentAmount(),
                                        "MOA 결제 재시도 (" + payment.getTargetMonth() + ")");

                        // 8. 결제 성공 - 상태 업데이트
                        payment.setPaymentStatus(PaymentStatus.COMPLETED);
                        payment.setTossPaymentKey(paymentKey);
                        payment.setOrderId(newOrderId);
                        payment.setCardNumber(userCard.getCardNumber());
                        payment.setCardCompany(userCard.getCardCompany());
                        payment.setPaymentDate(LocalDateTime.now());
                        paymentDao.updatePaymentStatus(paymentId, "COMPLETED");

                        // 9. 재시도 성공 기록
                        retryService.recordSuccess(payment, attemptCount + 1);

                        // 10. 성공 이벤트 발행
                        eventPublisher.publishEvent(new MonthlyPaymentCompletedEvent(
                                        payment.getPartyId(),
                                        payment.getPartyMemberId(),
                                        payment.getUserId(),
                                        payment.getPaymentAmount(),
                                        payment.getTargetMonth()));

                } catch (BusinessException e) {
                        // 11. 결제 실패 - 재시도 기록
                        LocalDateTime nextRetry = (attemptCount + 1 < MAX_RETRY_ATTEMPTS)
                                        ? calculateNextRetryTime(attemptCount + 1)
                                        : null;

                        if (nextRetry != null) {
                                retryService.recordFailureWithRetry(
                                                payment,
                                                attemptCount + 1,
                                                e.getErrorCode().getCode(),
                                                e.getMessage(),
                                                nextRetry);
                        } else {
                                retryService.recordPermanentFailure(payment, attemptCount + 1, e);
                        }

                        throw new BusinessException(ErrorCode.PAYMENT_RETRY_FAILED,
                                        "결제 재시도에 실패했습니다: " + e.getMessage());
                }

                // 12. 업데이트된 결제 상세 정보 반환
                return getPaymentDetail(paymentId);
        }

        @Override
        @Transactional(readOnly = true)
        public boolean canRetryPayment(Integer paymentId) {
                Payment payment = paymentDao.findById(paymentId).orElse(null);
                if (payment == null) {
                        return false;
                }

                // FAILED 상태이고 재시도 횟수가 4회 미만인 경우에만 재시도 가능
                return payment.getPaymentStatus() == PaymentStatus.FAILED
                                && getRetryAttemptCount(paymentId) < MAX_RETRY_ATTEMPTS;
        }

        @Override
        @Transactional(readOnly = true)
        public int getRetryAttemptCount(Integer paymentId) {
                return retryService.findPendingRetries(java.time.LocalDate.now())
                                .stream()
                                .filter(r -> r.getPaymentId().equals(paymentId))
                                .mapToInt(r -> r.getAttemptNumber())
                                .max()
                                .orElse(0);
        }
}