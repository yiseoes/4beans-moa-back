# MOA (모아) - OTT 구독 공유 플랫폼 백엔드

<div align="center">

**4beans 팀이 만든 OTT 구독을 함께 모아 쓰는 플랫폼**

[![Deploy](https://img.shields.io/badge/Deploy-AWS-FF9900?logo=amazon-aws)](https://www.moamoa.cloud/)
[![Java](https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.8-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-3.x-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)

**🚀 Live Demo**: [https://www.moamoa.cloud/](https://www.moamoa.cloud/)

</div>

---

## 📌 프로젝트 소개

**MOA(모아)**는 Netflix, Disney+, Wavve 등 OTT 구독 서비스를 함께 공유하여 비용을 절감할 수 있는 중개 플랫폼의 백엔드 시스템입니다.

### 핵심 비즈니스 모델

- **파티장**이 OTT 계정을 생성하고 **파티원**을 모집
- **보증금 시스템**을 통한 신뢰성 있는 공유 환경 제공
- **자동 결제** 및 **월간 정산**으로 편리한 운영
- 파티원은 저렴한 월회비로 OTT 서비스 이용

### 해결하는 문제

1. 높은 OTT 구독 비용 → 여러 명이 나눠서 부담
2. 공유 시 신뢰 문제 → 보증금 시스템으로 해결
3. 수동 정산의 번거로움 → 자동 결제 및 정산

---

## 📸 스크린샷

### 메인 & 테마 시스템
<p align="center">
  <img src="./screenshots/main(pop).JPG" alt="메인 화면 (Pop 테마)" width="700"/>
</p>

| Classic | Dark | Christmas |
|:-------:|:----:|:---------:|
| <img src="./screenshots/theme(classic).JPG" alt="Classic" width="260"/> | <img src="./screenshots/theme(dark).JPG" alt="Dark" width="260"/> | <img src="./screenshots/theme(christmas).JPG" alt="Christmas" width="260"/> |

### 핵심 기능
| 파티 상세 | 결제/보증금 | 관리자 대시보드 |
|:--------:|:----------:|:-------------:|
| <img src="./screenshots/party_detail.JPG" alt="파티 상세" width="260"/> | <img src="./screenshots/MyWallet.JPG" alt="금융 내역" width="260"/> | <img src="./screenshots/admin_Dashboard.JPG" alt="대시보드" width="260"/> |

### Push 알림 & 커뮤니티 관리
| 알림 발송 내역 | 문의 관리 |
|:------------:|:--------:|
| <img src="./screenshots/admin_pushHistory.JPG" alt="알림 발송" width="380"/> | <img src="./screenshots/admin_Inquiry.JPG" alt="문의 관리" width="380"/> |

---

## ✨ 주요 기능

### 🎭 파티 관리
- 파티 생성 및 멤버 모집
- 파티 상태 관리 (모집중, 활성, 종료)
- 파티장/파티원 역할 구분
- OTT 계정 정보 공유

### 💰 보증금 시스템
- **파티장 보증금**: 상품 가격 전액 (예: 13,000원)
- **파티원 보증금**: 인당 분담금 (예: 3,250원)
- 파티 종료 시 자동 환불
- 위약 시 보증금 차감

### 💳 자동 결제 및 정산
- **토스 페이먼츠** 빌링키 기반 자동 결제
- 매월 자동 월회비 청구
- 결제 실패 시 최대 4회 자동 재시도
- 파티장에게 매월 자동 정산 (수수료 15%)

### 🏦 오픈뱅킹 연동
- **1원 인증**을 통한 계좌 소유권 검증
- 정산금 자동 이체 준비 (추후 오픈뱅킹 API 연동 시 확장 가능)
- 확장 가능한 계좌 인증 아키텍처

### 🔐 보안 및 인증
- **JWT** 기반 무상태 인증 (Access + Refresh Token)
- **Google OTP** 2단계 인증 지원
- **OAuth 2.0** 소셜 로그인 (Kakao, Google)
- 계정 잠금 정책 (5회 실패 시 자동 잠금)
- 본인인증 연동 (PASS)

### 🔔 Push 알림 시스템 (24종)
- **SSE(Server-Sent Events)** 기반 실시간 알림
- **알림 24종 카테고리별 분류**:
  - **PARTY (9종)**: 가입/탈퇴/시작/종료/해산/일시정지/멤버가입/멤버탈퇴
  - **PAYMENT (10종)**: 결제예정/성공/실패재시도/잔액부족/한도초과/카드오류/최종실패/재시도성공/타임아웃
  - **DEPOSIT (3종)**: 보증금환불/차감/환불성공
  - **SETTLEMENT (3종)**: 정산완료/실패/계좌등록요청
  - **OPENBANKING (4종)**: 인증요청/계좌인증완료/인증만료/횟수초과
  - **COMMUNITY (1종)**: 문의답변
- 트랜잭션 커밋 후 알림 발송 (데이터 정합성 보장)
- `ConcurrentHashMap` 기반 멀티스레드 안전한 연결 관리
- 알림 읽음/안읽음 처리, 알림 설정 (종류별 ON/OFF)

### 👥 커뮤니티
- **공지사항**: CRUD, 검색/필터, 페이징, 관리자 등록/수정
- **FAQ**: 카테고리별 조회, 검색, 관리자 CRUD
- **1:1 문의**: 등록/수정/삭제, 파일 첨부, 관리자 답변 시스템
- JWT 기반 사용자/관리자 권한 분기 처리

---

## 🛠 기술 스택

### Backend
- **Language**: Java 17
- **Framework**: Spring Boot 3.5.8
- **ORM**: MyBatis 3.0.3
- **Build Tool**: Maven

### Database
- **RDBMS**: MySQL 8.0+
- **Test DB**: H2 (in-memory)

### Authentication & Security
- **JWT**: jjwt 0.11.5
- **2FA**: TOTP (Google OTP) 1.7.1
- **Spring Security**: 인가 및 권한 관리

### External Services
- **Payment**: Toss Payments (빌링키 기반 자동결제)
- **Banking**: Open Banking API (1원 인증)
- **OAuth**: Kakao, Google
- **Email**: Resend 3.1.0
- **Identity Verification**: PASS 본인인증

### Testing
- JUnit 5, Mockito
- Spring Boot Test
- jqwik (Property-Based Testing)

---

## 🏗 시스템 아키텍처

### 계층형 아키텍처 (Layered Architecture)

```
┌─────────────────────────────────────────┐
│         Client (Web/Mobile)             │
└─────────────────────────────────────────┘
                   ↓ HTTPS/REST API
┌─────────────────────────────────────────┐
│      Controller Layer (REST API)        │
│   - AuthRestController                  │
│   - PartyRestController                 │
│   - PaymentRestController               │
│   - SettlementRestController            │
└─────────────────────────────────────────┘
                   ↓
┌─────────────────────────────────────────┐
│      Service Layer (Business Logic)     │
│   - PartyService                        │
│   - PaymentService                      │
│   - SettlementService                   │
│   - OpenBankingService                  │
└─────────────────────────────────────────┘
                   ↓
┌─────────────────────────────────────────┐
│       DAO Layer (Data Access)           │
│   - MyBatis Mappers                     │
│   - 24 XML Mapper Files                 │
└─────────────────────────────────────────┘
                   ↓
┌─────────────────────────────────────────┐
│         Database (MySQL)                │
│   - 27 Tables                           │
└─────────────────────────────────────────┘
```

### 핵심 비즈니스 플로우

```
파티 생성 → 방장 보증금 결제 → 파티원 모집 → 파티원 가입 (보증금+첫달)
                                      ↓
                              파티 활성화 (정원 충족)
                                      ↓
                    매월 자동 월회비 결제 (Scheduler)
                                      ↓
                      매월 파티장 정산 (Scheduler)
                                      ↓
                    파티 종료 → 보증금 환불 (Scheduler)
```

---

## 🚀 시작하기 (로컬 개발)

### 1. 사전 요구사항

다음 프로그램들이 설치되어 있어야 합니다:

- **Java 17** 이상
- **Maven 3.x** (또는 내장된 Maven Wrapper 사용)
- **MySQL 8.0** 이상
- **Git**

### 2. 저장소 클론

```bash
git clone https://github.com/yourusername/4beans-moa-back.git
cd 4beans-moa-back
```

### 3. 데이터베이스 설정

#### 3.1 데이터베이스 생성

```bash
mysql -u root -p
```

```sql
CREATE DATABASE moa CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
exit;
```

#### 3.2 스키마 적용

```bash
mysql -u root -p moa < src/main/resources/moa_schema_20251211_1.sql
```

#### 3.3 샘플 데이터 적용 (선택사항)

```bash
mysql -u root -p moa < src/main/resources/sample-data2_20251211_1.sql
```

### 4. 환경 변수 설정

#### 4.1 application-secret.properties 파일 생성

프로젝트 루트에 제공된 템플릿을 복사합니다:

```bash
cp src/main/resources/application-secret.properties.example src/main/resources/application-secret.properties
```

#### 4.2 필요한 환경 변수

`src/main/resources/application-secret.properties` 파일을 열고 다음 값들을 설정하세요:

```properties
# 데이터베이스
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password

# JWT (랜덤 문자열 생성 권장)
jwt.secret=your-secret-key-at-least-256-bits
jwt.access-token-expire-time=3600000
jwt.refresh-token-expire-time=604800000

# 토스 페이먼츠
toss.payments.secret-key=test_sk_xxxxxxxxxxxxxxxx
toss.payments.client-key=test_ck_xxxxxxxxxxxxxxxx

# 오픈뱅킹
openbanking.client-id=your-client-id
openbanking.client-secret=your-client-secret
openbanking.platform.access-token=your-platform-token

# OAuth 2.0
oauth.kakao.client-id=your-kakao-client-id
oauth.kakao.client-secret=your-kakao-client-secret
oauth.google.client-id=your-google-client-id
oauth.google.client-secret=your-google-client-secret

# Resend (이메일)
resend.api-key=re_xxxxxxxxxxxxxxxx

# PASS 본인인증
pass.api-key=your-pass-api-key
pass.api-secret=your-pass-api-secret
```

> **참고**: 실제 API 키는 각 서비스의 개발자 콘솔에서 발급받아야 합니다.

### 5. 빌드

#### Maven Wrapper 사용 (권장)

```bash
# Unix/Linux/macOS
./mvnw clean package

# Windows
mvnw.cmd clean package
```

#### 시스템 Maven 사용

```bash
mvn clean package
```

#### 테스트 제외 빌드

```bash
./mvnw clean package -DskipTests
```

### 6. 실행

#### 로컬 환경 실행

```bash
./mvnw spring-boot:run
```

#### WAR 파일로 실행

```bash
java -jar target/moa-0.0.1-SNAPSHOT.war
```

#### 특정 프로필로 실행

```bash
# 로컬 환경
java -jar target/moa-0.0.1-SNAPSHOT.war --spring.profiles.active=local

# 운영 환경
java -jar target/moa-0.0.1-SNAPSHOT.war --spring.profiles.active=prod
```

### 7. 접속 확인

서버가 정상적으로 실행되면 다음 주소로 접속할 수 있습니다:

- **로컬**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health (설정된 경우)

---

## 📡 주요 API 엔드포인트

### 인증 (Authentication)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | 로그인 (이메일/비밀번호) |
| POST | `/api/auth/logout` | 로그아웃 |
| POST | `/api/auth/refresh` | 토큰 갱신 |
| POST | `/api/auth/otp/setup` | Google OTP 설정 |
| POST | `/api/auth/otp/verify` | OTP 검증 |

### 사용자 (User)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users/me` | 내 정보 조회 |
| PUT | `/api/users/me` | 내 정보 수정 |
| POST | `/api/users/updatePwd` | 비밀번호 변경 |
| POST | `/api/users/me/card` | 카드 등록 (자동결제) |
| GET | `/api/users/me/account` | 정산 계좌 조회 |

### 파티 (Party)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/parties` | 파티 생성 |
| GET | `/api/parties` | 파티 목록 조회 (필터링/페이징) |
| GET | `/api/parties/{partyId}` | 파티 상세 조회 |
| POST | `/api/parties/{partyId}/join` | 파티 가입 |
| DELETE | `/api/parties/{partyId}/leave` | 파티 탈퇴 |
| GET | `/api/parties/my` | 내가 가입한 파티 목록 |

### 결제 (Payment)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/payments/my` | 내 결제 내역 |
| GET | `/api/v1/payments/{paymentId}` | 결제 상세 조회 |
| GET | `/api/v1/payments/party/{partyId}` | 파티별 결제 내역 |

### 보증금 (Deposit)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/deposits/my` | 내 보증금 내역 |
| GET | `/api/deposits/party/{partyId}` | 파티별 보증금 내역 |

### 정산 (Settlement)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/settlements/my` | 내 정산 내역 (방장용) |
| GET | `/api/settlements/{settlementId}/details` | 정산 상세 내역 |

### 오픈뱅킹 (Open Banking)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/openbanking/auth-url` | 오픈뱅킹 인증 URL 생성 |
| GET | `/api/openbanking/callback` | OAuth 콜백 |
| POST | `/api/openbanking/send-verification` | 1원 인증 시작 |
| POST | `/api/openbanking/verify` | 1원 인증 검증 |

### 상품 (Product)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/product` | 상품 목록 조회 (OTT 서비스) |
| GET | `/api/product/{productId}` | 상품 상세 조회 |

### 🔔 Push 알림 (Notification)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/push/subscribe` | SSE 실시간 알림 구독 |
| GET | `/api/push/my` | 내 알림 목록 조회 |
| GET | `/api/push/unread-count` | 읽지 않은 알림 수 |
| PATCH | `/api/push/{pushId}/read` | 알림 읽음 처리 |
| PATCH | `/api/push/read-all` | 전체 알림 읽음 처리 |
| DELETE | `/api/push/{pushId}` | 알림 삭제 |

### 커뮤니티 (Community)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/community/notice` | 공지사항 목록 조회 |
| GET | `/api/community/notice/{id}` | 공지사항 상세 조회 |
| GET | `/api/community/faq` | FAQ 목록 조회 |
| POST | `/api/community/inquiry` | 1:1 문의 등록 |
| GET | `/api/community/inquiry/my` | 내 문의 목록 조회 |

---

## 📂 프로젝트 구조

```
4beans-moa-back/
├── src/
│   ├── main/
│   │   ├── java/com/moa/
│   │   │   ├── MoaApplication.java        # 메인 애플리케이션
│   │   │   ├── auth/                      # 인증/인가
│   │   │   ├── common/                    # 공통 모듈
│   │   │   │   ├── exception/             # 예외 처리
│   │   │   │   ├── aspect/                # AOP
│   │   │   │   ├── filter/                # 필터
│   │   │   │   └── util/                  # 유틸리티
│   │   │   ├── config/                    # 설정 파일
│   │   │   │   ├── SecurityConfig.java    # Spring Security
│   │   │   │   ├── WebConfig.java         # CORS 등
│   │   │   │   └── ...
│   │   │   ├── dao/                       # 데이터 접근 계층 (MyBatis)
│   │   │   │   ├── party/
│   │   │   │   ├── payment/
│   │   │   │   ├── settlement/
│   │   │   │   └── ...
│   │   │   ├── domain/                    # 엔티티 모델
│   │   │   ├── dto/                       # 데이터 전송 객체
│   │   │   ├── scheduler/                 # 스케줄러
│   │   │   │   ├── PaymentScheduler.java
│   │   │   │   ├── SettlementScheduler.java
│   │   │   │   └── ...
│   │   │   ├── service/                   # 비즈니스 로직
│   │   │   │   ├── party/
│   │   │   │   ├── payment/
│   │   │   │   ├── settlement/
│   │   │   │   └── ...
│   │   │   └── web/                       # 컨트롤러 계층
│   │   │       ├── auth/
│   │   │       ├── party/
│   │   │       ├── payment/
│   │   │       └── ...
│   │   └── resources/
│   │       ├── sql/                       # MyBatis Mapper XML (24개)
│   │       ├── templates/email/           # 이메일 템플릿
│   │       ├── static/                    # 정적 파일
│   │       ├── data/                      # 샘플 데이터
│   │       ├── application.properties
│   │       ├── application-local.properties
│   │       ├── application-prod.properties
│   │       └── moa_schema_20251211_1.sql  # DB 스키마
│   └── test/                              # 테스트 코드
├── uploads/                               # 파일 업로드
│   ├── user/profile/
│   └── product-image/
├── pom.xml                                # Maven 설정
├── mvnw, mvnw.cmd                         # Maven Wrapper
└── README.md
```

### 주요 패키지 설명

- **auth**: JWT 필터, 토큰 프로바이더, 인증 핸들러
- **common**: 공통 예외, AOP, 유틸리티 클래스
- **config**: Spring 설정 (Security, Web, MyBatis 등)
- **dao**: MyBatis 인터페이스 (Mapper)
- **domain**: 엔티티 클래스 (Party, Payment, User 등)
- **dto**: Request/Response DTO
- **scheduler**: 자동화 작업 (정산, 결제, 환불 등)
- **service**: 비즈니스 로직
- **web**: REST API 컨트롤러

---

## ⏰ 자동화 시스템 (Scheduler)

MOA 플랫폼은 8개의 스케줄러를 통해 핵심 비즈니스 로직을 자동화합니다.

| 스케줄러 | 실행 주기 | 역할 |
|---------|----------|------|
| **SettlementScheduler** | 매월 1일 04:00 | 월간 정산 생성 및 이체 |
| **SettlementTransferScheduler** | 매시간 | 정산금 오픈뱅킹 이체 자동화 |
| **PaymentScheduler** | 매월 특정일 | 월회비 자동 결제 |
| **PaymentTimeoutScheduler** | 매 30분 | 결제 실패 재시도 (최대 4회) |
| **RefundScheduler** | 매 30분 | 보증금 자동 환불 처리 |
| **PartyCloseScheduler** | 매일 자정 | 종료 예정 파티 자동 종료 |
| **ExpiredPartyCleanupScheduler** | 매일 03:00 | 만료된 파티 정리 |
| **PendingDepositCleanupScheduler** | 매일 02:00 | 대기 중인 보증금 정리 |

### 재시도 메커니즘

결제, 환불, 정산 실패 시 자동으로 재시도하며, 모든 재시도 이력은 DB에 기록됩니다:

- **PAYMENT_RETRY_HISTORY**: 결제 재시도 이력
- **REFUND_RETRY_HISTORY**: 환불 재시도 이력
- **SETTLEMENT_RETRY_HISTORY**: 정산 재시도 이력

---

## 🔒 보안

### 인증 방식

1. **JWT 기반 무상태 인증**
   - Access Token (1시간)
   - Refresh Token (7일)
   - HttpOnly Cookie로 안전하게 전달

2. **Google OTP 2단계 인증**
   - TOTP 기반 OTP 생성
   - 백업 코드 제공 (최대 10개)

3. **OAuth 2.0 소셜 로그인**
   - Kakao, Google 지원
   - 기존 계정 연동 가능

### 권한 체계

| 권한 | 설명 |
|------|------|
| `USER` | 일반 사용자 (기본) |
| `ADMIN` | 관리자 (상품 관리, 대시보드 접근) |

### 보안 기능

- 비밀번호 암호화 (BCrypt)
- 계정 잠금 정책 (5회 실패 시 자동 잠금)
- 로그인 이력 추적
- CORS 화이트리스트 설정
- SQL Injection 방지 (MyBatis PreparedStatement)
- XSS 방지 (입력값 검증)

---

## 🚢 배포

### 프로덕션 체크리스트

배포 전 다음 항목들을 확인하세요:

- [ ] MySQL 데이터베이스 준비 (UTF-8 설정)
- [ ] `application-secret.properties` 설정 완료
  - [ ] 데이터베이스 접속 정보
  - [ ] JWT Secret Key (256비트 이상)
  - [ ] 토스 페이먼츠 API 키
  - [ ] 오픈뱅킹 API 키
  - [ ] OAuth 클라이언트 ID/Secret
  - [ ] Resend API 키
  - [ ] PASS 본인인증 API 키
- [ ] `application-prod.properties`에서 프론트엔드 URL 설정
- [ ] WAR 파일 빌드 완료
- [ ] 서버 방화벽 설정 (8080 포트 또는 설정한 포트)
- [ ] SSL 인증서 설정 (HTTPS)
- [ ] 환경 변수 `spring.profiles.active=prod` 설정

### 빌드 및 배포

```bash
# 1. 프로덕션 빌드
./mvnw clean package -DskipTests

# 2. WAR 파일 서버로 전송
scp target/moa-0.0.1-SNAPSHOT.war user@your-server:/path/to/deploy/

# 3. 서버에서 실행
java -jar moa-0.0.1-SNAPSHOT.war --spring.profiles.active=prod
```

### 추천 배포 방식

- **Tomcat/WAS**: WAR 파일을 Tomcat의 webapps에 배포
- **Standalone**: `java -jar` 명령어로 직접 실행
- **Docker**: Docker 이미지 빌드 후 컨테이너 실행 (Dockerfile 작성 필요)
- **CI/CD**: GitHub Actions, Jenkins 등으로 자동 배포

> 상세한 배포 가이드는 별도 문서 `DEPLOY.md`를 참조하세요. (작성 예정)

---

## 🗄️ 데이터베이스

### ERD (주요 테이블 관계)

```
USERS (사용자)
  ├── 1:N → OAUTH_ACCOUNT (소셜 로그인)
  ├── 1:N → PARTY (파티장)
  ├── 1:1 → ACCOUNT (정산 계좌)
  └── 1:1 → USER_CARD (자동결제 카드)

PARTY (파티)
  ├── 1:N → PARTY_MEMBER (파티원)
  └── N:1 → PRODUCT (상품)

PARTY_MEMBER (파티원)
  ├── 1:N → DEPOSIT (보증금)
  └── 1:N → PAYMENT (월회비)

SETTLEMENT (정산)
  └── 1:N → TRANSFER_TRANSACTION (이체 거래)
```

### 총 27개 테이블

- 회원 관련: 8개 (USERS, OAUTH_ACCOUNT, BLACKLIST 등)
- 상품/구독: 3개 (CATEGORY, PRODUCT, SUBSCRIPTION)
- 파티: 2개 (PARTY, PARTY_MEMBER)
- 결제/보증금: 4개 (DEPOSIT, PAYMENT, PAYMENT_RETRY_HISTORY 등)
- 정산: 3개 (SETTLEMENT, SETTLEMENT_RETRY_HISTORY, TRANSFER_TRANSACTION)
- 오픈뱅킹: 2개 (BANK_CODE, ACCOUNT_VERIFICATION)
- 커뮤니티/알림: 4개 (COMMUNITY, PUSH 등)
- 기타: 1개 (CHATBOT_KNOWLEDGE - 추후 개발 예정)

스키마 파일: `src/main/resources/moa_schema_20251211_1.sql`

---

## 🧪 테스트

### 테스트 실행

```bash
# 전체 테스트 실행
./mvnw test

# 특정 테스트 클래스 실행
./mvnw test -Dtest=PartyServiceTest

# 테스트 커버리지 리포트 생성 (Jacoco 설정 필요)
./mvnw test jacoco:report
```

### 테스트 도구

- **JUnit 5**: 단위 테스트 프레임워크
- **Mockito**: 모킹 라이브러리
- **jqwik**: Property-Based Testing
- **H2 Database**: 테스트용 in-memory DB
- **Spring Boot Test**: 통합 테스트

---

## 🏗️ 설계 철학

### 확장 가능한 아키텍처

**오픈뱅킹 사례**: 현재는 1원 인증만 구현되어 있지만, 추후 오픈뱅킹 API를 사용할 수 있을 때 구현체만 교체하면 자동 이체 기능을 쉽게 추가할 수 있도록 인터페이스 기반으로 설계되었습니다.

```java
// 인터페이스 설계로 확장 가능
interface BankingService {
    void verifyAccount();      // 현재: 1원 인증
    void transfer();           // 추후: 자동 이체 구현
}
```

### 재사용 가능한 컴포넌트

- 템플릿 기반 이메일 발송
- 템플릿 기반 알림 시스템
- 공통 예외 처리
- 재시도 메커니즘 추상화

---

## 📝 TODO / 개발 예정 기능

- [ ] AI 챗봇 (GPT-4o-mini + RAG) - 리팩토링 예정
- [ ] 오픈뱅킹 자동 이체 (API 사용 가능 시 구현)
- [ ] API 문서화 (Swagger/OpenAPI)
- [ ] Docker 지원 (Dockerfile, docker-compose.yml)
- [ ] CI/CD 파이프라인 구축
- [ ] 모니터링 (Actuator, Prometheus)

---

## 👥 팀 정보

**4beans** 팀이 개발한 프로젝트입니다.

| 팀원 | 역할 |
|------|------|
| 육주영 | 팀장 / 유저 / 인증보안 / AI챗봇 |
| 박한솔 | 파티 / 결제 / 보증금 / 정산 |
| 김진원 | 구독상품 / AWS 인프라 |
| 김이서 | **커뮤니티 / Push 알림 시스템 / DB 설계** |

---

### 김이서 - 커뮤니티 / Push 알림 시스템 / DB 설계

#### 1. DB 설계 및 데이터 관리
| 항목 | 상세 내용 |
|------|-----------|
| **ERD 설계** | 도메인 기반 ERD 설계 논의 참여 및 테이블 구조 정의 |
| **샘플 데이터** | 테스트용 시드 스크립트 작성 (사용자 50명, 구독/결제/문의 등 500+건) |
| **인덱스 설계** | 쿼리 성능 최적화를 위한 인덱스 설계 기준 수립 |
| **버전 관리** | DB 스키마 변경 이력 Git 기반 버전 관리 |

#### 2. 커뮤니티 도메인 (공지/FAQ/문의) - Full Stack
| 기능 | 상세 구현 |
|------|-----------|
| **공지사항** | CRUD, 검색/필터, 페이징, 관리자 등록/수정 |
| **FAQ** | 카테고리별 조회, 검색, 관리자 CRUD |
| **1:1 문의** | 등록/수정/삭제, 파일 첨부, 관리자 답변 시스템 |
| **권한 분기** | JWT 기반 사용자/관리자 권한에 따른 화면 및 기능 분기 처리 |

#### 3. Push 알림 시스템 - **전담 설계/구현** (Full Stack)

> **Push 알림 24종** 전체 로직 설계, 백엔드 구현, 프론트엔드 UI까지 전담 개발

**Backend 구현 - SSE 기반 실시간 알림**
```java
// SSE 기반 실시간 알림 시스템
@Service
public class NotificationService {
    // ConcurrentHashMap으로 사용자별 SSE 연결 관리
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    // 실시간 알림 전송
    public void sendNotification(Long userId, NotificationDto notification) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            emitter.send(notification);
        }
    }
}

// 트랜잭션 커밋 후 알림 발송 (데이터 정합성 보장)
TransactionSynchronizationManager.registerSynchronization(
    new TransactionSynchronization() {
        @Override
        public void afterCommit() {
            notificationService.sendNotification(userId, notification);
        }
    }
);
```

**Push 알림 24종 이벤트 (`PushCodeType.java`)**
| 카테고리 | 알림 종류 |
|----------|-----------|
| **PARTY (9종)** | 가입, 탈퇴, 시작, 종료, 멤버가입, 멤버탈퇴, 해산, 멤버탈퇴됨, 일시정지 |
| **PAYMENT (10종)** | 결제예정, 성공, 실패재시도, 잔액부족, 한도초과, 카드오류, 최종실패, 멤버실패알림, 재시도성공, 타임아웃 |
| **DEPOSIT (3종)** | 보증금환불, 보증금차감, 환불성공 |
| **SETTLEMENT (3종)** | 정산완료, 정산실패, 계좌등록요청 |
| **OPENBANKING (4종)** | 인증요청, 계좌인증완료, 인증만료, 인증횟수초과 |
| **COMMUNITY (1종)** | 문의답변 |

**Frontend 구현**
- SSE 연결 관리 및 자동 재연결 로직
- 실시간 알림 토스트 UI
- 알림 목록 페이지 (읽음/안읽음 처리)
- 알림 설정 (알림 종류별 ON/OFF)

#### 4. API 테스트
| 도구 | 활용 |
|------|------|
| **JUnit** | 단위 테스트 작성 (Service, DAO 계층) |
| **Postman** | REST API 기능 테스트 수행 |

#### 기술적 챌린지
- **SSE 연결 관리**: 서버 재시작/네트워크 끊김 시 자동 재연결 구현
- **트랜잭션 동기화**: `TransactionSynchronization` 활용하여 DB 커밋 후 알림 발송
- **동시성 처리**: `ConcurrentHashMap`으로 멀티스레드 환경에서 안전한 연결 관리

---

### 기술 스택 선택 이유

- **Spring Boot**: 빠른 개발과 엔터프라이즈급 안정성
- **MyBatis**: 복잡한 쿼리 최적화 및 세밀한 제어
- **JWT**: 확장 가능한 무상태 인증
- **토스 페이먼츠**: 국내 최고의 개발자 경험
- **오픈뱅킹**: 금융 서비스의 표준 플랫폼

---

## 📄 라이선스

이 프로젝트는 비공개 프로젝트입니다.

---

## 📞 문의

프로젝트 관련 문의사항이 있으시면 이슈를 등록해주세요.

- **GitHub Issues**: [이슈 등록하기](https://github.com/yourusername/4beans-moa-back/issues)

---

<div align="center">

**Made with ❤️ by 4beans Team**

</div>
