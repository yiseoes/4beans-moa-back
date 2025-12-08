package com.moa.dao.payment;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.moa.domain.Payment;
import com.moa.dto.payment.response.PaymentDetailResponse;
import com.moa.dto.payment.response.PaymentResponse;

@Mapper
public interface PaymentDao {

        int insertPayment(Payment payment);

        Optional<Payment> findById(@Param("paymentId") Integer paymentId);

        Optional<PaymentDetailResponse> findDetailById(@Param("paymentId") Integer paymentId);

        List<PaymentResponse> findByUserId(@Param("userId") String userId);

        List<PaymentResponse> findByPartyId(@Param("partyId") Integer partyId);

        Optional<Payment> findByPartyMemberIdAndTargetMonth(
                        @Param("partyMemberId") Integer partyMemberId,
                        @Param("targetMonth") String targetMonth);

        Optional<Payment> findByOrderId(@Param("orderId") String orderId);

        int updatePaymentStatus(
                        @Param("paymentId") Integer paymentId,
                        @Param("status") String status);

        /**
         * 정산 ID 업데이트 (비정규화 - SETTLEMENT_DETAIL 테이블 제거로 인해 추가)
         */
        int updateSettlementId(
                        @Param("paymentId") Integer paymentId,
                        @Param("settlementId") Integer settlementId);

        /**
         * 정산 ID로 결제 내역 조회 (정산 상세 조회용)
         */
        List<PaymentResponse> findBySettlementId(@Param("settlementId") Integer settlementId);
}