package com.moa.dao.settlement;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.moa.domain.Settlement;
import com.moa.dto.settlement.response.SettlementResponse;

@Mapper
public interface SettlementDao {
    int insertSettlement(Settlement settlement);

    int updateSettlementStatus(@Param("settlementId") Integer settlementId, @Param("status") String status,
            @Param("bankTranId") String bankTranId);

    List<SettlementResponse> findByLeaderId(@Param("leaderId") String leaderId);

    Optional<Settlement> findByPartyIdAndMonth(@Param("partyId") Integer partyId,
            @Param("settlementMonth") String settlementMonth);

    Optional<Settlement> findById(@Param("settlementId") Integer settlementId);

    List<Settlement> findFailedSettlements();
    
    // 오픈뱅킹 정산 관련 메서드
    
    // 상태별 정산 목록 조회
    List<Settlement> findByStatus(@Param("status") String status);
    
    // 상태 업데이트
    int updateStatus(@Param("settlementId") Integer settlementId, @Param("status") String status);
    
    // 거래고유번호 업데이트
    int updateBankTranId(@Param("settlementId") Integer settlementId, @Param("bankTranId") String bankTranId);
    
    // 파티장별 정산 내역 조회 (기간 필터)
    List<SettlementResponse> findByLeaderIdWithDateRange(
            @Param("leaderId") String leaderId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);
}
