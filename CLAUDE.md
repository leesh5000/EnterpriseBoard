# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

이 파일은 이 저장소에서 코드를 작업할 때 Claude Code(claude.ai/code)에게 지침을 제공합니다.

## 기본 지침

- 모든 응답은 한글로 설명합니다.

## 프로젝트 설명

이 프로젝트는 게시판 서비스를 예시로 대용량 시스템 및 분산 환경에서도 내결함성, 내구성, 고가용성 등을 갖춘 서버를 설계하고 구현해보는 프로젝트입니다.

## 개발 지침

**ROLE AND EXPERTISE (역할 및 전문성)**
당신은 켄트 벡(Kent Beck)의 테스트 주도 개발(TDD) 및 ‘Tidy First’ 원칙을 따르는 시니어 소프트웨어 엔지니어입니다. 귀하의 목적은 이 방법론을 정확히 따라 개발을 안내하는 것입니다.

**CORE DEVELOPMENT PRINCIPLES (핵심 개발 원칙)**
- 항상 TDD 사이클(레드 → 그린 → 리팩터)을 따릅니다.
- 가장 단순한 실패하는 테스트를 먼저 작성합니다.
- 테스트를 통과시키기 위해 최소한의 코드를 구현합니다.
- 테스트가 통과된 후에만 리팩터링을 수행합니다.
- 벡의 ‘Tidy First’ 접근법을 따라 구조적 변경과 동작적 변경을 분리합니다.
- 개발 전반에 걸쳐 높은 코드 품질을 유지합니다.

**TDD METHODOLOGY GUIDANCE (TDD 방법론 가이드)**
1. 작은 기능 단위의 동작을 정의하는 실패하는 테스트를 작성합니다.
2. 테스트 실패 메시지가 명확하고 유용하도록 합니다.
3. 테스트를 통과시킬 수 있는 최소한의 코드를 작성합니다.
4. 테스트가 통과되면(그린 단계) 필요한 리팩터링을 고려합니다.
5. 리팩터링 후에도 테스트를 실행해 올바름을 검증합니다.
6. 이 과정을 반복하며 점진적으로 기능을 확장합니다.

**TIDY FIRST APPROACH (‘Tidy First’ 접근법)**
- **구조적 변경**: 동작을 변경하지 않는 코드 재배치(이름 변경, 메서드 추출, 코드 이동 등)
- **동작적 변경**: 실제 기능 추가나 수정
- 반드시 구조적 변경을 먼저 하고, 그 후 동작적 변경을 수행합니다.
- 각 변경 후 테스트를 실행해 동작이 동일함을 확인합니다.

**COMMIT DISCIPLINE (커밋 규율)**
- 모든 테스트가 통과하고 경고가 없을 때만 커밋합니다.
- 커밋은 단일 논리 단위의 작업만 포함해야 합니다.
- 커밋 메시지에는 ‘구조적 변경’ 또는 ‘동작적 변경’임을 명시합니다.
- 작은 단위로 자주 커밋합니다.

**CODE QUALITY STANDARDS (코드 품질 기준)**
- 중복을 철저히 제거합니다.
- 의도를 명확히 드러내는 이름과 구조를 사용합니다.
- 의존성을 명시적으로 처리합니다.
- 메서드는 작고 단일 책임을 가집니다.
- 상태(state)와 부작용(side effect)은 최소화합니다.
- 가능한 가장 단순한 해법을 사용합니다.

**REFACTORING GUIDELINES (리팩터링 지침)**
- 테스트가 통과된 후에만 리팩터링합니다.
- 하나의 리팩터링 패턴을 한 번에 하나씩 적용합니다.
- 각 리팩터링 후 테스트를 실행합니다.
- 중복 제거나 가독성 개선에 우선순위를 둡니다.

**EXAMPLE WORKFLOW (예시 워크플로우)**
1. 기능의 작은 부분부터 실패하는 테스트 작성
2. 테스트 통과를 위한 최소 구현
3. 전체 테스트 실행(그린)
4. 구조적 변경(‘Tidy First’) → 테스트 실행 → 커밋
5. 다음 작은 기능에 대한 테스트 작성
6. 반복

**Kotlin-specific (Kotlin 특이사항)**
- 코드 스타일은 항상 Kotlin 코딩 컨벤션을 따릅니다. (https://kotlinlang.org/docs/coding-conventions.html)
- Java -> Kotlin 변환 시, 아래의 링크를 참조하여 항상 Best Practices를 따릅니다.
  - https://www.krasamo.com/migrating-apps-from-java-to-kotlin/
  - https://www.intelivita.com/blog/convert-java-to-kotlin/
  - https://resources.jetbrains.com/storage/products/kotlinconf-2023/Practical%20Tips%20For%20Legacy%20Java%20Codebases%20Conversion%20Into%20Kotlin.pdf

## 아키텍처 지침

**HEXAGONAL ARCHITECTURE (헥사고날 아키텍처)**
이 프로젝트는 헥사고날 아키텍처(포트와 어댑터 패턴)를 따릅니다. 새로운 기능을 추가하거나 기존 코드를 수정할 때 반드시 이 구조를 유지해야 합니다.

**프로젝트 구조 (Article 서비스 기준)**
```
service/article/src/main/kotlin/me/helloc/enterpriseboard/
├── ArticleApplication.kt          # 🚀 Spring Boot 메인 애플리케이션
│
├── domain/                        # 🎯 순수 도메인 레이어
│   ├── model/
│   │   └── Article.kt            # 도메인 엔티티 (비즈니스 로직 포함)
│   └── service/
│       └── PageLimitCalculator.kt # 도메인 서비스 (페이지 제한값 계산)
│
├── application/                   # 📋 애플리케이션 레이어
│   ├── port/
│   │   ├── in/                   # 인바운드 포트 (유스케이스 인터페이스)
│   │   │   ├── CreateArticleUseCase.kt + CreateArticleCommand
│   │   │   ├── DeleteArticleUseCase.kt
│   │   │   ├── GetArticleUseCase.kt + GetArticlePageQuery/Result
│   │   │   └── UpdateArticleUseCase.kt + UpdateArticleCommand
│   │   └── out/                  # 아웃바운드 포트 (외부 시스템 인터페이스)
│   │       └── ArticleRepository.kt # 영속성 포트 정의
│   └── facade/                  # 유스케이스 구현 (Facade 패턴)
│       ├── CreateArticleFacade.kt
│       ├── DeleteArticleFacade.kt
│       ├── GetArticleFacade.kt  # 페이지네이션 로직 포함
│       └── UpdateArticleFacade.kt
│
└── adapter/                      # 🔌 어댑터 레이어
    ├── in/                       # 인바운드 어댑터
    │   ├── loader/
    │   │   └── DataInitializer.kt # 대용량 데이터 초기화 도구
    │   └── web/                  # REST 컨트롤러
    │       ├── ArticleController.kt # REST API 엔드포인트
    │       ├── GlobalExceptionHandler.kt # 전역 예외 처리
    │       └── dto/              # Request/Response DTO
    │           ├── ArticlePageResponse.kt # 페이지 조회 응답
    │           ├── ArticleResponse.kt     # 개별 조회 응답
    │           ├── CreateArticleRequest.kt
    │           └── UpdateArticleRequest.kt
    └── out/                      # 아웃바운드 어댑터
        └── persistence/          # JPA 영속성 어댑터
            ├── ArticleJpaAdapter.kt   # Repository 구현체
            ├── ArticleJpaEntity.kt    # JPA 엔티티
            └── ArticleJpaRepository.kt # Spring Data JPA 인터페이스

# 테스트 구조 (src/test/)
test/kotlin/me/helloc/enterpriseboard/
├── ArticleRepositoryTest.kt      # 영속성 계층 테스트
├── adapter/in/web/
│   ├── ArticleControllerTest.kt  # 컨트롤러 단위 테스트
│   └── FakeUseCases.kt          # 테스트 더블 (Fake 구현체)
├── application/facade/
│   ├── *ArticleFacadeTest.kt   # Facade 계층 단위 테스트
│   └── FakeArticleRepository.kt # Repository 테스트 더블
├── domain/
│   ├── model/ArticleTest.kt     # 도메인 모델 테스트
│   └── service/PageLimitCalculatorTest.kt # 도메인 서비스 테스트
└── integration/
    └── ArticleIntegrationTest.kt # TestContainers 기반 통합 테스트
```

**핵심 원칙**
1. **의존성 방향**: 외부에서 내부로만 의존 (Adapter → Application → Domain)
2. **도메인 순수성**: Domain 레이어는 프레임워크나 외부 라이브러리에 의존하지 않음
3. **포트 정의**: Application 레이어에서 인터페이스로 포트 정의
4. **어댑터 구현**: Adapter 레이어에서 포트 구현체 제공

**구현 가이드라인 (실제 Article 서비스 패턴)**

**Domain Layer**
- **Model**: 순수 Kotlin 클래스, JPA 의존성 없음 (Article.kt)
- **Service**: 복잡한 비즈니스 계산 로직 (PageLimitCalculator.kt)
- **원칙**: 프레임워크 독립적, 비즈니스 로직만 포함

**Application Layer**
- **UseCase Interface**: 단일 책임 원칙, Command/Query 패턴 적용
  - CreateArticleUseCase + CreateArticleCommand
  - GetArticleUseCase + GetArticlePageQuery/Result
- **Facade Implementation**: UseCase 구현체, 도메인 서비스와 포트 조합
  - Facade 패턴으로 복잡한 비즈니스 로직을 단순한 인터페이스로 제공
- **포트 정의**: 인터페이스로 외부 의존성 추상화

**Adapter Layer**
- **Web Layer**: REST 컨트롤러, DTO 변환, 전역 예외 처리
- **Persistence Layer**: JPA 어댑터, 도메인 모델 ↔ JPA 엔티티 변환
- **Loader**: 특수 목적 어댑터 (대용량 데이터 초기화)

**DTO 변환 패턴**
- Request DTO → Command/Query 객체 → Domain Model
- Domain Model → Result 객체 → Response DTO
- 각 계층 간 모델 노출 방지

**테스트 패턴**
- **단위 테스트**: Fake 구현체 활용 (Mock 프레임워크 지양)
- **통합 테스트**: TestContainers + 실제 DB
- **테스트 더블**: 각 포트별 Fake 구현체 제공

**아키텍처 다이어그램**
자세한 아키텍처 다이어그램은 [docs/hexagonal-architecture-diagram.md](docs/hexagonal-architecture-diagram.md)를 참조하세요.

## 커밋 메세지 작성 지침

### 개선된 커밋 메시지 구조

**완전한 예시:**
```
feat: 로그인 페이지에 비밀번호 찾기 기능 추가

**문제상황:**
고객센터로 비밀번호 분실 문의가 주 20건 이상 접수되어
고객 불편과 운영팀 업무 부담이 가중됨

**해결방법:**
이메일 인증을 통한 셀프 비밀번호 재설정 기능으로
즉시 해결 가능하도록 개선

**변경사항:**
- 비밀번호 찾기 버튼 UI 추가
- 이메일 인증을 통한 비밀번호 재설정 로직 구현
- 인증 메일 템플릿 작성
- 보안을 위한 6자리 인증번호 시스템 도입
```

### 업데이트된 커밋 메시지 작성 프롬프트

```
다음 정보를 바탕으로 명확하고 이해하기 쉬운 커밋 메시지를 작성해주세요:

**변경 사항:** [여기에 실제로 변경한 내용을 입력]

**커밋 메시지 작성 규칙:**
1. 첫 줄: [타입]: [50자 이내로 요약]

2. 빈 줄 하나 추가

3. 본문 구조:
   **문제상황:** (왜 이 변경이 필요했는지)
   **해결방법:** (어떤 접근으로 문제를 해결했는지)
   **변경사항:** (구체적인 구현 내용)

**실제 사용 예시들:**

**예시 1 - 버그 수정:**
```
fix: 장바구니 총 금액 계산 오류 해결

**문제상황:**
상품을 장바구니에서 삭제해도 총 금액이 업데이트되지 않아
결제 시 실제 금액과 표시 금액이 달라 고객 컴플레인 발생

**해결방법:**
상품 삭제 이벤트 리스너에 총 금액 재계산 로직 추가로
실시간 금액 동기화 구현

**변경사항:**
- removeItem 함수에 calculateTotal() 호출 추가
- 총 금액 업데이트 애니메이션 효과 추가
- 장바구니 비어있을 때 안내 메시지 표시
```

**예시 2 - 성능 개선:**
```
perf: 상품 목록 페이지 로딩 속도 개선

**문제상황:**
상품이 1000개 이상일 때 페이지 로딩이 5초 이상 걸려
사용자 이탈률이 30% 증가함

**해결방법:**
무한 스크롤과 이미지 지연 로딩을 도입하여
초기 로딩 시간을 1초 이내로 단축

**변경사항:**
- 20개씩 페이지네이션 구현
- Intersection Observer API로 무한 스크롤 적용
- 이미지 lazy loading 라이브러리 도입
- 로딩 스켈레톤 UI 추가
```

**예시 3 - 새 기능 추가:**
```
feat: 상품 리뷰 평점 시스템 도입

**문제상황:**
구매 후기는 있지만 별점이 없어 상품 품질을
한눈에 파악하기 어렵다는 고객 피드백 지속 접수

**해결방법:**
5점 만점 별점 시스템과 평균 평점 표시로
상품 선택 시 직관적인 품질 판단 기준 제공

**변경사항:**
- 별점 입력 컴포넌트 개발 (1-5점)
- 리뷰 목록에 별점 표시 기능 추가
- 상품 상세페이지에 평균 별점 표시
- 별점별 리뷰 필터링 기능 구현

## 코드 품질 개선 및 리팩토링에 관한 지침

- 해당 링크를 참고하세요. (https://techblog.lycorp.co.jp/ko/techniques-for-improving-code-quality-list)

## 개발 환경 및 빌드 명령어

### 필수 요구사항
- **Java**: OpenJDK 21 (프로젝트 루트에 jdk-21.0.7/ 디렉토리 포함)
- **Kotlin**: 1.9.25
- **Spring Boot**: 3.5.3
- **테스트 프레임워크**: Kotest 5.7.2

### 빌드 및 테스트 명령어
```bash
# 전체 프로젝트 빌드
./gradlew build

# 특정 서비스 빌드
./gradlew :service:article:build

# 전체 테스트 실행
./gradlew test

# 특정 서비스 테스트 실행
./gradlew :service:article:test

# 특정 테스트 클래스 실행
./gradlew :service:article:test --tests "ArticleControllerTest"
./gradlew :service:article:test --tests "GetArticleFacadeTest"

# 특정 테스트 메서드 실행 (Kotest 기반)
./gradlew :service:article:test --tests "*페이지 조회 테스트*"

# 통합 테스트 실행
./gradlew :service:article:test --tests "ArticleIntegrationTest"

# 프로젝트 정리
./gradlew clean

# 대용량 데이터 초기화 실행 (12M 레코드)
./gradlew :service:article:bootRun --args='--spring.profiles.active=data-init'
```

### 멀티모듈 프로젝트 구조
이 프로젝트는 마이크로서비스 아키텍처로 구성된 멀티모듈 Gradle 프로젝트입니다:

**공통 모듈 (common/)**
- `common:snowflake`: Twitter Snowflake 기반 분산 ID 생성기

**서비스 모듈 (service/)**
- `service:article`: 게시글 CRUD 및 페이지네이션 (메인 구현체)
- `service:comment`: 댓글 서비스 (미래 구현 예정)
- `service:view`: 조회수 서비스 (미래 구현 예정) 
- `service:like`: 좋아요 서비스 (미래 구현 예정)
- `service:hot-article`: 인기 게시글 서비스 (미래 구현 예정)
- `service:article-read`: 게시글 읽기 전용 서비스 (미래 구현 예정)

### 데이터베이스 및 테스트 환경
- **운영 DB**: MySQL 8.0
- **테스트 DB**: TestContainers + MySQL 8.0.33 (통합 테스트)
- **테스트 격리**: @Transactional 및 격리된 데이터 생성
- **성능 테스트**: 대용량 데이터 초기화 지원 (12M 레코드)

### 주요 구현 기능
**게시글 서비스 (service:article)**
- CRUD 연산 (생성, 조회, 수정, 삭제)
- 페이지네이션 (PageLimitCalculator 기반)
- REST API 엔드포인트: `/api/v1/articles`
- 대용량 데이터 처리 (배치 처리, 멀티스레딩)

**테스트 커버리지**
- **단위 테스트**: 각 계층별 격리된 테스트 (Fake 객체 사용)
- **통합 테스트**: 실제 DB 환경 TestContainers 기반
- **성능 테스트**: 12M 레코드 처리 테스트

### 의존성 관리 패턴
- 모든 하위 모듈은 루트 `build.gradle.kts`에서 공통 의존성 관리
- 각 서비스는 필요한 의존성만 추가로 선언
- TestContainers는 통합 테스트가 필요한 모듈에서만 사용

### 개발 팁
**테스트 작성 시:**
- 단위 테스트: Kotest StringSpec 스타일 사용
- 통합 테스트: JUnit 5 + TestContainers 사용  
- 테스트 더블: Fake 구현체 활용 (Mock 프레임워크 지양)
- Facade 테스트: UseCase 인터페이스 기반 단위 테스트

**데이터 처리 시:**
- 대용량 처리: 배치 크기 5000, 스레드 풀 20개 권장
- JPA 설정: batch_size=5000, HikariCP max-pool-size=30 설정됨

**모듈 간 의존성:**
- 공통 모듈(snowflake)은 다른 서비스에서 참조 가능
- 서비스 간 직접 참조 금지 (향후 이벤트 기반 통신 예정)
