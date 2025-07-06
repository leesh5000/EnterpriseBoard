# 헥사고날 아키텍처 다이어그램

## 전체 아키텍처 구조

```mermaid
graph TB
    subgraph "외부 시스템"
        Client[클라이언트<br/>웹/모바일]
        DB[(데이터베이스)]
        Redis[(Redis)]
        ExtAPI[외부 API]
    end
    
    subgraph "어댑터 레이어 (Adapter Layer)"
        subgraph "인바운드 어댑터"
            REST[REST Controller<br/>ArticleController]
            GQL[GraphQL<br/>미구현]
            gRPC[gRPC<br/>미구현]
        end
        
        subgraph "아웃바운드 어댑터"
            JPA[JPA Adapter<br/>ArticleRepositoryAdapter]
            RedisAdapter[Redis Adapter<br/>미구현]
            ExtAdapter[External API Adapter<br/>미구현]
        end
    end
    
    subgraph "애플리케이션 레이어 (Application Layer)"
        subgraph "인바운드 포트"
            UC1[CreateArticleUseCase]
            UC2[UpdateArticleUseCase]
            UC3[GetArticleUseCase]
            UC4[DeleteArticleUseCase]
        end
        
        subgraph "아웃바운드 포트"
            RepoPort[ArticleRepository<br/>인터페이스]
            CachePort[CachePort<br/>미구현]
            NotifPort[NotificationPort<br/>미구현]
        end
        
        subgraph "유스케이스 구현"
            SVC1[CreateArticleService]
            SVC2[UpdateArticleService]
            SVC3[GetArticleService]
            SVC4[DeleteArticleService]
        end
    end
    
    subgraph "도메인 레이어 (Domain Layer)"
        Article[Article<br/>도메인 모델]
        DomainService[도메인 서비스<br/>미구현]
    end
    
    %% 연결선
    Client --> REST
    REST --> UC1
    REST --> UC2
    REST --> UC3
    REST --> UC4
    
    UC1 --> SVC1
    UC2 --> SVC2
    UC3 --> SVC3
    UC4 --> SVC4
    
    SVC1 --> Article
    SVC2 --> Article
    SVC3 --> Article
    SVC4 --> Article
    
    SVC1 --> RepoPort
    SVC2 --> RepoPort
    SVC3 --> RepoPort
    SVC4 --> RepoPort
    
    RepoPort --> JPA
    CachePort --> RedisAdapter
    NotifPort --> ExtAdapter
    
    JPA --> DB
    RedisAdapter --> Redis
    ExtAdapter --> ExtAPI
    
    classDef domain fill:#f9f,stroke:#333,stroke-width:4px,color:#000
    classDef application fill:#bbf,stroke:#333,stroke-width:2px,color:#000
    classDef adapter fill:#bfb,stroke:#333,stroke-width:2px,color:#000
    classDef external fill:#fbb,stroke:#333,stroke-width:2px,color:#000
    
    class Article,DomainService domain
    class UC1,UC2,UC3,UC4,RepoPort,CachePort,NotifPort,SVC1,SVC2,SVC3,SVC4 application
    class REST,GQL,gRPC,JPA,RedisAdapter,ExtAdapter adapter
    class Client,DB,Redis,ExtAPI external
```

## 의존성 방향

```mermaid
graph LR
    subgraph "의존성 규칙"
        A[외부 시스템] --> B[어댑터]
        B --> C[애플리케이션]
        C --> D[도메인]
    end
    
    subgraph "금지된 의존성"
        D -.-> C
        D -.-> B
        D -.-> A
        C -.-> B
        C -.-> A
        B -.-> A
    end
    
    style A fill:#fbb,color:#000
    style B fill:#bfb,color:#000
    style C fill:#bbf,color:#000
    style D fill:#f9f,color:#000
```

## 데이터 흐름 예시 (Article 생성)

```mermaid
sequenceDiagram
    participant Client
    participant Controller as ArticleController<br/>(인바운드 어댑터)
    participant UseCase as CreateArticleUseCase<br/>(인바운드 포트)
    participant Service as CreateArticleService<br/>(유스케이스 구현)
    participant Domain as Article<br/>(도메인 모델)
    participant Port as ArticleRepository<br/>(아웃바운드 포트)
    participant Adapter as ArticleRepositoryAdapter<br/>(아웃바운드 어댑터)
    participant DB as Database
    
    Client->>Controller: POST /api/articles<br/>{title, content, ...}
    Controller->>Controller: CreateArticleRequest → CreateArticleCommand
    Controller->>UseCase: create(command)
    UseCase->>Service: create(command)
    Service->>Domain: Article.create(...)
    Domain->>Service: Article 인스턴스
    Service->>Port: save(article)
    Port->>Adapter: save(article)
    Adapter->>Adapter: Article → ArticleJpaEntity
    Adapter->>DB: INSERT INTO article ...
    DB->>Adapter: 저장 완료
    Adapter->>Adapter: ArticleJpaEntity → Article
    Adapter->>Service: 저장된 Article
    Service->>Controller: Article
    Controller->>Controller: Article → ArticleResponse
    Controller->>Client: 201 Created<br/>{articleId, title, ...}
```

## 레이어별 책임

### 🎯 도메인 레이어 (Domain Layer)
- **책임**: 핵심 비즈니스 로직
- **특징**: 프레임워크 독립적, 순수 Kotlin/Java
- **예시**: Article 엔티티의 update() 메서드

### 📋 애플리케이션 레이어 (Application Layer)
- **책임**: 유스케이스 조율, 트랜잭션 관리
- **특징**: 비즈니스 프로세스 정의
- **예시**: "게시글 작성" 유스케이스

### 🔌 어댑터 레이어 (Adapter Layer)
- **책임**: 외부 시스템과의 통신
- **특징**: 프레임워크 의존적 (Spring, JPA 등)
- **예시**: REST API, 데이터베이스 연동

## 장점

1. **테스트 용이성**: 각 레이어를 독립적으로 테스트 가능
2. **유연성**: 어댑터만 교체하여 다른 기술 스택으로 전환 가능
3. **비즈니스 로직 보호**: 도메인이 외부 변화에 영향받지 않음
4. **명확한 경계**: 각 레이어의 책임이 명확히 분리됨