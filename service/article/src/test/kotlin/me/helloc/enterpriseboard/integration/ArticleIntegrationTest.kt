package me.helloc.enterpriseboard.integration

import com.fasterxml.jackson.databind.ObjectMapper
import me.helloc.enterpriseboard.adapter.`in`.web.dto.CreateArticleRequest
import me.helloc.enterpriseboard.adapter.`in`.web.dto.UpdateArticleRequest
import me.helloc.enterpriseboard.adapter.`in`.web.dto.ArticleResponse
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
}