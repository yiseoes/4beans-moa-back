package com.moa.dto.payment.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 결제 상세 응답 DTO
 * 결제 상세 내역 조회 시 사용
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetailResponse {

    // === 결제 기본 정보 ===
    private Integer paymentId;
    private Integer partyId;
    private Integer partyMemberId;
    private String userId;
    private String paymentType;
    private Integer paymentAmount;
    private String paymentStatus;
    private String paymentMethod;
    private LocalDateTime paymentDate;
    private String targetMonth;

    // === Toss Payments 정보 ===
    private String tossPaymentKey;
    private String orderId;
    private String cardNumber;
    private String cardCompany;

    // === 파티 정보 (JOIN) ===
    private String productName;
    private String productImage;
    private String partyLeaderNickname;

    // === 사용자 정보 (JOIN) ===
    private String userNickname;

    // === 재시도 정보 ===
    private String retryStatus;          // 재시도 상태 (SUCCESS, FAILED)
    private Integer attemptNumber;       // 현재 시도 횟수
    private LocalDateTime nextRetryDate; // 다음 재시도 예정일
    private String retryReason;          // 실패 사유
    private String errorMessage;         // 에러 메시지
    private boolean canRetry;            // 재시도 가능 여부
    private int maxRetryAttempts = 4;    // 최대 재시도 횟수
}