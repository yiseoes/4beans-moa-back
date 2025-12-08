package com.moa.dao.account;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.moa.domain.Account;

@Mapper
public interface AccountDao {
    int insertAccount(Account account);

    Optional<Account> findByUserId(@Param("userId") String userId);

    int updateVerifyStatus(@Param("accountId") Integer accountId, @Param("isVerified") String isVerified);

    Optional<Account> findById(@Param("accountId") Integer accountId);
    
    // 오픈뱅킹 관련 메서드
    
    // 핀테크이용번호 업데이트
    int updateFintechUseNum(@Param("accountId") Integer accountId, @Param("fintechUseNum") String fintechUseNum);
    
    // 계좌 상태 업데이트
    int updateStatus(@Param("accountId") Integer accountId, @Param("status") String status);
    
    // 핀테크이용번호로 계좌 조회
    Optional<Account> findByFintechUseNum(@Param("fintechUseNum") String fintechUseNum);
    
    // 활성 계좌만 조회
    Optional<Account> findActiveByUserId(@Param("userId") String userId);
}
