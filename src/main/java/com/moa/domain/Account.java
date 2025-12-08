package com.moa.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private Integer accountId;
    private String userId;
    private String bankCode;
    private String bankName;
    private String accountNumber;
    private String accountHolder;
    private String fintechUseNum;  // 핀테크이용번호 (오픈뱅킹)
    private String status;         // ACTIVE, INACTIVE
    private String isVerified;     // 'Y' or 'N'
    private LocalDateTime regDate;
    private LocalDateTime verifyDate;
    
    // 계좌번호 마스킹 (앞 4자리 + **** + 뒤 4자리)
    public String getMaskedAccountNumber() {
        if (accountNumber == null || accountNumber.length() < 8) {
            return "****";
        }
        return accountNumber.substring(0, 4) + "****" + 
               accountNumber.substring(accountNumber.length() - 4);
    }
}
