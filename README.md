# 대규모 시스템 게시판

대용량 데이터, 트래픽 환경을 고려한 게시판 프로젝트입니다.

## 멀티 모듈

이 프로젝트는 마이크로서비스 아키텍처를 위한 멀티 모듈 구조로 설계되었습니다.

### 공통 모듈 (common)
- **snowflake**: 분산 시스템에서 고유 ID 생성을 위한 Snowflake 알고리즘 구현

### 서비스 모듈 (service)
각 서비스는 독립적으로 배포 가능한 Spring Boot 애플리케이션입니다.

- **article**: 게시글 작성, 수정, 조회 등 게시글 관련 핵심 기능
- **article-read**: 게시글 읽기 전용 서비스 (읽기 성능 최적화)
- **comment**: 댓글 작성, 수정, 삭제 기능
- **view**: 게시글 조회수 추적 서비스
- **like**: 게시글 좋아요 기능
- **hot-article**: 인기 게시글 집계 서비스

## 패키지 구조

각 서비스는 헥사고날 아키텍처(포트와 어댑터 패턴)를 따릅니다.

```
service/
├── article/
│   └── src/main/kotlin/me/helloc/enterpriseboard/article/
│       ├── domain/              # 도메인 로직
│       │   ├── Article.kt       # 게시글 엔티티
│       │   └── repository/      # 도메인 리포지토리 인터페이스
│       ├── infrastructure/      # 인프라스트럭처 계층
│       │   └── persistence/     # 데이터 영속성 구현
│       ├── interfaces/          # 인터페이스 계층
│       │   └── web/            # REST API 컨트롤러
│       └── service/            # 애플리케이션 서비스
│           └── dto/            # 요청/응답 DTO
```

### 주요 패키지 역할
- **domain**: 비즈니스 로직과 엔티티
- **infrastructure**: 외부 시스템과의 연동 (DB, 메시징 등)
- **interfaces**: 외부로부터의 요청 처리 (REST API, 이벤트 핸들러 등)
- **service**: 애플리케이션 로직과 도메인 오케스트레이션

## 도메인 다이어그램

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Article       │    │   Comment       │    │   View          │
│   Service       │    │   Service       │    │   Service       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │              ┌─────────────────┐              │
         └─────────────→│   Hot Article   │←─────────────┘
                        │   Service       │
                        └─────────────────┘
                                 │
                        ┌─────────────────┐
                        │   Like          │
                        │   Service       │
                        └─────────────────┘
```

### 서비스 간 관계
- **Article Service**: 게시글 생성/수정 시 이벤트 발행
- **View Service**: 조회수 증가 이벤트를 Hot Article Service로 전달
- **Like Service**: 좋아요 이벤트를 Hot Article Service로 전달
- **Hot Article Service**: 조회수와 좋아요 데이터를 집계하여 인기 게시글 선정
- **Comment Service**: 게시글에 대한 댓글 관리

## ERD 다이어그램

```sql
┌─────────────────────┐
│      article        │
├─────────────────────┤
│ PK article_id       │
│    title            │
│    content          │
│ FK board_id         │ (shard key)
│    writer_id        │
│    created_at       │
│    modified_at      │
└─────────────────────┘
          │
          │ 1:N
          ▼
┌─────────────────────┐
│board_article_count  │
├─────────────────────┤
│ PK board_id         │
│    article_count    │
└─────────────────────┘

┌─────────────────────┐
│       outbox        │
├─────────────────────┤
│ PK outbox_id        │
│    shard_key        │
│    event_type       │
│    payload          │
│    created_at       │
└─────────────────────┘
```

### 주요 테이블 설명
- **article**: 게시글 정보, board_id를 샤드 키로 사용
- **board_article_count**: 게시판별 게시글 수 집계
- **outbox**: 이벤트 발행을 위한 아웃박스 패턴 구현
