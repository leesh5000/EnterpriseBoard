package me.helloc.enterpriseboard.integration

import com.fasterxml.jackson.databind.ObjectMapper
import me.helloc.enterpriseboard.adapter.`in`.web.dto.CreateArticleRequest
import me.helloc.enterpriseboard.adapter.`in`.web.dto.UpdateArticleRequest
import me.helloc.enterpriseboard.adapter.`in`.web.dto.ArticleResponse
import me.helloc.enterpriseboard.adapter.`in`.web.dto.ArticlePageResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@ActiveProfiles("test")
@Transactional
class ArticleIntegrationTest {

    companion object {
        @Container
        @JvmStatic
        val mysql: MySQLContainer<*> = MySQLContainer("mysql:8.0.33")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true)
        
        init {
            mysql.start()
            System.setProperty("spring.datasource.url", mysql.jdbcUrl)
            System.setProperty("spring.datasource.username", mysql.username)
            System.setProperty("spring.datasource.password", mysql.password)
        }
    }

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `게시글 생성 테스트`() {
        val request = CreateArticleRequest(
            title = "테스트 제목",
            content = "테스트 내용",
            boardId = 1L,
            writerId = 1L
        )

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(request, headers)

        val response: ResponseEntity<ArticleResponse> = restTemplate.postForEntity(
            "http://localhost:$port/api/v1/articles",
            entity,
            ArticleResponse::class.java
        )

        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.body?.title == "테스트 제목")
        assert(response.body?.content == "테스트 내용")
        assert(response.body?.boardId == 1L)
        assert(response.body?.writerId == 1L)
    }

    @Test
    fun `게시글 조회 테스트`() {
        val createRequest = CreateArticleRequest(
            title = "조회 테스트 제목",
            content = "조회 테스트 내용",
            boardId = 1L,
            writerId = 1L
        )

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(createRequest, headers)

        val createResponse: ResponseEntity<ArticleResponse> = restTemplate.postForEntity(
            "http://localhost:$port/api/v1/articles",
            entity,
            ArticleResponse::class.java
        )

        val articleId = createResponse.body?.articleId!!

        val getResponse: ResponseEntity<ArticleResponse> = restTemplate.getForEntity(
            "http://localhost:$port/api/v1/articles/$articleId",
            ArticleResponse::class.java
        )

        assert(getResponse.statusCode == HttpStatus.OK)
        assert(getResponse.body?.title == "조회 테스트 제목")
        assert(getResponse.body?.content == "조회 테스트 내용")
    }

    @Test
    fun `게시글 수정 테스트`() {
        val createRequest = CreateArticleRequest(
            title = "수정 전 제목",
            content = "수정 전 내용",
            boardId = 1L,
            writerId = 1L
        )

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val createEntity = HttpEntity(createRequest, headers)

        val createResponse: ResponseEntity<ArticleResponse> = restTemplate.postForEntity(
            "http://localhost:$port/api/v1/articles",
            createEntity,
            ArticleResponse::class.java
        )

        val articleId = createResponse.body?.articleId!!

        val updateRequest = UpdateArticleRequest(
            title = "수정 후 제목",
            content = "수정 후 내용"
        )

        val updateEntity = HttpEntity(updateRequest, headers)
        val updateResponse: ResponseEntity<ArticleResponse> = restTemplate.exchange(
            "http://localhost:$port/api/v1/articles/$articleId",
            HttpMethod.PUT,
            updateEntity,
            ArticleResponse::class.java
        )

        assert(updateResponse.statusCode == HttpStatus.OK)
        assert(updateResponse.body?.title == "수정 후 제목")
        assert(updateResponse.body?.content == "수정 후 내용")
    }

    @Test
    fun `게시글 삭제 테스트`() {
        val createRequest = CreateArticleRequest(
            title = "삭제 테스트 제목",
            content = "삭제 테스트 내용",
            boardId = 1L,
            writerId = 1L
        )

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val createEntity = HttpEntity(createRequest, headers)

        val createResponse: ResponseEntity<ArticleResponse> = restTemplate.postForEntity(
            "http://localhost:$port/api/v1/articles",
            createEntity,
            ArticleResponse::class.java
        )

        val articleId = createResponse.body?.articleId!!

        val deleteResponse: ResponseEntity<Void> = restTemplate.exchange(
            "http://localhost:$port/api/v1/articles/$articleId",
            HttpMethod.DELETE,
            null,
            Void::class.java
        )

        assert(deleteResponse.statusCode == HttpStatus.NO_CONTENT)

        val getResponse: ResponseEntity<ArticleResponse> = restTemplate.getForEntity(
            "http://localhost:$port/api/v1/articles/$articleId",
            ArticleResponse::class.java
        )

        assert(getResponse.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `게시글 전체 사이클 테스트 - 생성_조회_수정_삭제`() {
        val createRequest = CreateArticleRequest(
            title = "사이클 테스트 제목",
            content = "사이클 테스트 내용",
            boardId = 1L,
            writerId = 1L
        )

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val createEntity = HttpEntity(createRequest, headers)

        // 생성 테스트
        val createResponse: ResponseEntity<ArticleResponse> = restTemplate.postForEntity(
            "http://localhost:$port/api/v1/articles",
            createEntity,
            ArticleResponse::class.java
        )

        assert(createResponse.statusCode == HttpStatus.CREATED)
        assert(createResponse.body?.title == "사이클 테스트 제목")
        assert(createResponse.body?.content == "사이클 테스트 내용")

        val articleId = createResponse.body?.articleId!!

        // 조회 테스트
        val getResponse: ResponseEntity<ArticleResponse> = restTemplate.getForEntity(
            "http://localhost:$port/api/v1/articles/$articleId",
            ArticleResponse::class.java
        )

        assert(getResponse.statusCode == HttpStatus.OK)
        assert(getResponse.body?.title == "사이클 테스트 제목")
        assert(getResponse.body?.content == "사이클 테스트 내용")

        // 수정 테스트
        val updateRequest = UpdateArticleRequest(
            title = "사이클 테스트 수정된 제목",
            content = "사이클 테스트 수정된 내용"
        )

        val updateEntity = HttpEntity(updateRequest, headers)
        val updateResponse: ResponseEntity<ArticleResponse> = restTemplate.exchange(
            "http://localhost:$port/api/v1/articles/$articleId",
            HttpMethod.PUT,
            updateEntity,
            ArticleResponse::class.java
        )

        assert(updateResponse.statusCode == HttpStatus.OK)
        assert(updateResponse.body?.title == "사이클 테스트 수정된 제목")
        assert(updateResponse.body?.content == "사이클 테스트 수정된 내용")

        // 삭제 테스트
        val deleteResponse: ResponseEntity<Void> = restTemplate.exchange(
            "http://localhost:$port/api/v1/articles/$articleId",
            HttpMethod.DELETE,
            null,
            Void::class.java
        )

        assert(deleteResponse.statusCode == HttpStatus.NO_CONTENT)

        // 삭제 후 조회 테스트
        val getAfterDeleteResponse: ResponseEntity<ArticleResponse> = restTemplate.getForEntity(
            "http://localhost:$port/api/v1/articles/$articleId",
            ArticleResponse::class.java
        )

        assert(getAfterDeleteResponse.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `페이지 조회 테스트 - 기본 페이지네이션 동작`() {
        // Given - 여러 게시글 생성
        val boardId = 100L
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        
        val createdArticles = mutableListOf<Long>()
        repeat(15) { index ->
            val createRequest = CreateArticleRequest(
                title = "페이지 테스트 제목 ${index + 1}",
                content = "페이지 테스트 내용 ${index + 1}",
                boardId = boardId,
                writerId = (index + 1).toLong()
            )
            
            val createEntity = HttpEntity(createRequest, headers)
            val createResponse: ResponseEntity<ArticleResponse> = restTemplate.postForEntity(
                "http://localhost:$port/api/v1/articles",
                createEntity,
                ArticleResponse::class.java
            )
            
            assert(createResponse.statusCode == HttpStatus.CREATED)
            createdArticles.add(createResponse.body?.articleId!!)
        }

        // When - 첫 번째 페이지 조회 (10개씩)
        val pageResponse1: ResponseEntity<ArticlePageResponse> = restTemplate.getForEntity(
            "http://localhost:$port/api/v1/articles?boardId=$boardId&page=1&pageSize=10&movablePageCount=5",
            ArticlePageResponse::class.java
        )

        // Then - 첫 번째 페이지 검증
        assert(pageResponse1.statusCode == HttpStatus.OK)
        assert(pageResponse1.body?.articles?.size == 10)
        assert(pageResponse1.body?.totalCount!! > 0) // PageLimitCalculator에 의해 계산된 값
        
        // ID 내림차순 정렬 확인 (최신 게시글이 먼저)
        val firstPageArticles = pageResponse1.body?.articles!!
        for (i in 0 until firstPageArticles.size - 1) {
            assert(firstPageArticles[i].articleId > firstPageArticles[i + 1].articleId)
        }

        // When - 두 번째 페이지 조회
        val pageResponse2: ResponseEntity<ArticlePageResponse> = restTemplate.getForEntity(
            "http://localhost:$port/api/v1/articles?boardId=$boardId&page=2&pageSize=10&movablePageCount=5",
            ArticlePageResponse::class.java
        )

        // Then - 두 번째 페이지 검증
        assert(pageResponse2.statusCode == HttpStatus.OK)
        assert(pageResponse2.body?.articles?.size == 5) // 남은 5개
        
        // 첫 번째 페이지와 두 번째 페이지의 게시글이 겹치지 않는지 확인
        val firstPageIds = firstPageArticles.map { it.articleId }.toSet()
        val secondPageIds = pageResponse2.body?.articles?.map { it.articleId }?.toSet()!!
        assert(firstPageIds.intersect(secondPageIds).isEmpty())
    }

    @Test
    fun `페이지 조회 테스트 - movablePageCount 기본값 적용`() {
        // Given
        val boardId = 200L
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        
        val createRequest = CreateArticleRequest(
            title = "기본값 테스트 제목",
            content = "기본값 테스트 내용",
            boardId = boardId,
            writerId = 1L
        )
        
        val createEntity = HttpEntity(createRequest, headers)
        val createResponse: ResponseEntity<ArticleResponse> = restTemplate.postForEntity(
            "http://localhost:$port/api/v1/articles",
            createEntity,
            ArticleResponse::class.java
        )
        
        assert(createResponse.statusCode == HttpStatus.CREATED)

        // When - movablePageCount 파라미터 생략 (기본값 10 적용)
        val pageResponse: ResponseEntity<ArticlePageResponse> = restTemplate.getForEntity(
            "http://localhost:$port/api/v1/articles?boardId=$boardId&page=1&pageSize=5",
            ArticlePageResponse::class.java
        )

        // Then
        assert(pageResponse.statusCode == HttpStatus.OK)
        assert(pageResponse.body?.articles?.size == 1)
        assert(pageResponse.body?.totalCount!! > 0)
        assert(pageResponse.body?.articles?.first()?.title == "기본값 테스트 제목")
    }

    @Test
    fun `페이지 조회 테스트 - 존재하지 않는 boardId`() {
        // Given
        val nonExistentBoardId = 99999L

        // When
        val pageResponse: ResponseEntity<ArticlePageResponse> = restTemplate.getForEntity(
            "http://localhost:$port/api/v1/articles?boardId=$nonExistentBoardId&page=1&pageSize=10&movablePageCount=5",
            ArticlePageResponse::class.java
        )

        // Then
        assert(pageResponse.statusCode == HttpStatus.OK)
        assert(pageResponse.body?.articles?.size == 0)
        assert(pageResponse.body?.totalCount == 0L)
    }

    @Test
    fun `페이지 조회 테스트 - 다른 boardId 게시글 격리`() {
        // Given
        val targetBoardId = 300L
        val otherBoardId = 400L
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        
        // 타겟 보드에 게시글 생성
        val targetRequest = CreateArticleRequest(
            title = "타겟 보드 게시글",
            content = "타겟 보드 내용",
            boardId = targetBoardId,
            writerId = 1L
        )
        
        val targetEntity = HttpEntity(targetRequest, headers)
        val targetResponse: ResponseEntity<ArticleResponse> = restTemplate.postForEntity(
            "http://localhost:$port/api/v1/articles",
            targetEntity,
            ArticleResponse::class.java
        )
        
        // 다른 보드에 게시글 생성
        val otherRequest = CreateArticleRequest(
            title = "다른 보드 게시글",
            content = "다른 보드 내용",
            boardId = otherBoardId,
            writerId = 2L
        )
        
        val otherEntity = HttpEntity(otherRequest, headers)
        val otherResponse: ResponseEntity<ArticleResponse> = restTemplate.postForEntity(
            "http://localhost:$port/api/v1/articles",
            otherEntity,
            ArticleResponse::class.java
        )
        
        assert(targetResponse.statusCode == HttpStatus.CREATED)
        assert(otherResponse.statusCode == HttpStatus.CREATED)

        // When - 타겟 보드만 조회
        val pageResponse: ResponseEntity<ArticlePageResponse> = restTemplate.getForEntity(
            "http://localhost:$port/api/v1/articles?boardId=$targetBoardId&page=1&pageSize=10&movablePageCount=5",
            ArticlePageResponse::class.java
        )

        // Then - 타겟 보드 게시글만 조회되는지 확인
        assert(pageResponse.statusCode == HttpStatus.OK)
        assert(pageResponse.body?.articles?.size == 1)
        assert(pageResponse.body?.articles?.first()?.title == "타겟 보드 게시글")
        assert(pageResponse.body?.articles?.first()?.boardId == targetBoardId)
        
        // 다른 보드 게시글이 포함되지 않았는지 확인
        val articleIds = pageResponse.body?.articles?.map { it.articleId }
        assert(!articleIds!!.contains(otherResponse.body?.articleId))
    }

    @Test
    fun `페이지 조회 테스트 - 페이지 크기보다 적은 데이터`() {
        // Given
        val boardId = 500L
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        
        // 3개의 게시글만 생성
        repeat(3) { index ->
            val createRequest = CreateArticleRequest(
                title = "소량 데이터 ${index + 1}",
                content = "소량 내용 ${index + 1}",
                boardId = boardId,
                writerId = (index + 1).toLong()
            )
            
            val createEntity = HttpEntity(createRequest, headers)
            val createResponse: ResponseEntity<ArticleResponse> = restTemplate.postForEntity(
                "http://localhost:$port/api/v1/articles",
                createEntity,
                ArticleResponse::class.java
            )
            
            assert(createResponse.statusCode == HttpStatus.CREATED)
        }

        // When - 페이지 크기를 10으로 설정 (데이터는 3개만 있음)
        val pageResponse: ResponseEntity<ArticlePageResponse> = restTemplate.getForEntity(
            "http://localhost:$port/api/v1/articles?boardId=$boardId&page=1&pageSize=10&movablePageCount=5",
            ArticlePageResponse::class.java
        )

        // Then - 실제 데이터 개수만 반환되는지 확인
        assert(pageResponse.statusCode == HttpStatus.OK)
        assert(pageResponse.body?.articles?.size == 3)
        assert(pageResponse.body?.totalCount == 3L)
    }

    @Test
    fun `페이지 조회 테스트 - 다양한 페이지 파라미터`() {
        // Given
        val boardId = 600L
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        
        // 20개의 게시글 생성
        repeat(20) { index ->
            val createRequest = CreateArticleRequest(
                title = "파라미터 테스트 ${index + 1}",
                content = "파라미터 내용 ${index + 1}",
                boardId = boardId,
                writerId = (index + 1).toLong()
            )
            
            val createEntity = HttpEntity(createRequest, headers)
            val createResponse: ResponseEntity<ArticleResponse> = restTemplate.postForEntity(
                "http://localhost:$port/api/v1/articles",
                createEntity,
                ArticleResponse::class.java
            )
            
            assert(createResponse.statusCode == HttpStatus.CREATED)
        }

        // When & Then - 다양한 페이지 크기로 테스트
        
        // 페이지 크기 5로 첫 번째 페이지
        val page1Response: ResponseEntity<ArticlePageResponse> = restTemplate.getForEntity(
            "http://localhost:$port/api/v1/articles?boardId=$boardId&page=1&pageSize=5&movablePageCount=3",
            ArticlePageResponse::class.java
        )
        assert(page1Response.statusCode == HttpStatus.OK)
        assert(page1Response.body?.articles?.size == 5)
        
        // 페이지 크기 5로 두 번째 페이지
        val page2Response: ResponseEntity<ArticlePageResponse> = restTemplate.getForEntity(
            "http://localhost:$port/api/v1/articles?boardId=$boardId&page=2&pageSize=5&movablePageCount=3",
            ArticlePageResponse::class.java
        )
        assert(page2Response.statusCode == HttpStatus.OK)
        assert(page2Response.body?.articles?.size == 5)
        
        // 페이지 크기 15로 첫 번째 페이지
        val largePage1Response: ResponseEntity<ArticlePageResponse> = restTemplate.getForEntity(
            "http://localhost:$port/api/v1/articles?boardId=$boardId&page=1&pageSize=15&movablePageCount=2",
            ArticlePageResponse::class.java
        )
        assert(largePage1Response.statusCode == HttpStatus.OK)
        assert(largePage1Response.body?.articles?.size == 15)
        
        // 각 페이지의 게시글이 겹치지 않는지 확인
        val page1Ids = page1Response.body?.articles?.map { it.articleId }?.toSet()!!
        val page2Ids = page2Response.body?.articles?.map { it.articleId }?.toSet()!!
        assert(page1Ids.intersect(page2Ids).isEmpty())
    }
}