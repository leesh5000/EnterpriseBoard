package me.helloc.enterpriseboard.integration

import com.fasterxml.jackson.databind.ObjectMapper
import me.helloc.enterpriseboard.adapter.`in`.web.dto.CommentResponse
import me.helloc.enterpriseboard.adapter.`in`.web.dto.CreateCommentRequest
import me.helloc.enterpriseboard.adapter.`in`.web.dto.GetCommentPageResponse
import me.helloc.enterpriseboard.domain.model.Comment
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
class CommentIntegrationTest {

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
    fun `댓글 생성 테스트`() {
        val request = CreateCommentRequest(
            content = "테스트 댓글 내용",
            parentCommentId = Comment.EMPTY_ID,
            articleId = 1L,
            writerId = 1L
        )

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(request, headers)

        val response: ResponseEntity<String> = restTemplate.postForEntity(
            "http://localhost:$port/api/v1/comments",
            entity,
            String::class.java
        )

        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.body != null)
        assert(response.body!!.contains("테스트 댓글 내용"))
    }

    @Test
    fun `대댓글 생성 테스트`() {
        // Given: 부모 댓글 생성
        val parentRequest = CreateCommentRequest(
            content = "부모 댓글 내용",
            parentCommentId = Comment.EMPTY_ID,
            articleId = 1L,
            writerId = 1L
        )

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val parentEntity = HttpEntity(parentRequest, headers)

        val parentResponse: ResponseEntity<CommentResponse> = restTemplate.postForEntity(
            "http://localhost:$port/api/v1/comments",
            parentEntity,
            CommentResponse::class.java
        )

        val parentCommentId = parentResponse.body?.commentId!!

        // When: 대댓글 생성
        val childRequest = CreateCommentRequest(
            content = "대댓글 내용",
            parentCommentId = parentCommentId,
            articleId = 1L,
            writerId = 2L
        )

        val childEntity = HttpEntity(childRequest, headers)
        val childResponse: ResponseEntity<CommentResponse> = restTemplate.postForEntity(
            "http://localhost:$port/api/v1/comments",
            childEntity,
            CommentResponse::class.java
        )

        // Then
        assert(childResponse.statusCode == HttpStatus.CREATED)
        assert(childResponse.body?.content == "대댓글 내용")
        assert(childResponse.body?.parentCommentId == parentCommentId)
        assert(childResponse.body?.articleId == 1L)
        assert(childResponse.body?.writerId == 2L)
    }

    @Test
    fun `댓글 페이지 조회 테스트`() {
        val articleId = 100L
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        // Given: 여러 댓글 생성
        val createdComments = mutableListOf<Long>()
        repeat(15) { index ->
            val request = CreateCommentRequest(
                content = "페이지 테스트 댓글 ${index + 1}",
                parentCommentId = Comment.EMPTY_ID,
                articleId = articleId,
                writerId = (index + 1).toLong()
            )

            val entity = HttpEntity(request, headers)
            val response: ResponseEntity<CommentResponse> = restTemplate.postForEntity(
                "http://localhost:$port/api/v1/comments",
                entity,
                CommentResponse::class.java
            )

            assert(response.statusCode == HttpStatus.CREATED)
            createdComments.add(response.body?.commentId!!)
        }

        // When: 첫 번째 페이지 조회 (10개씩)
        val pageResponse: ResponseEntity<GetCommentPageResponse> = restTemplate.getForEntity(
            "http://localhost:$port/api/v1/comments?articleId=$articleId&page=1&pageSize=10&movablePageCount=5",
            GetCommentPageResponse::class.java
        )

        // Then
        assert(pageResponse.statusCode == HttpStatus.OK)
        assert(pageResponse.body?.comments?.size == 10)
        assert(pageResponse.body?.visibleRangeCount!! > 0)

        // ID 순서 확인 (오름차순 정렬)
        val comments = pageResponse.body?.comments!!
        for (i in 0 until comments.size - 1) {
            assert(comments[i].commentId <= comments[i + 1].commentId)
        }
    }

    @Test
    fun `댓글 무한 스크롤 조회 테스트`() {
        val articleId = 200L
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        // Given: 댓글 10개 생성
        val createdComments = mutableListOf<Long>()
        repeat(10) { index ->
            val request = CreateCommentRequest(
                content = "스크롤 테스트 댓글 ${index + 1}",
                parentCommentId = Comment.EMPTY_ID,
                articleId = articleId,
                writerId = 1L
            )

            val entity = HttpEntity(request, headers)
            val response: ResponseEntity<CommentResponse> = restTemplate.postForEntity(
                "http://localhost:$port/api/v1/comments",
                entity,
                CommentResponse::class.java
            )

            createdComments.add(response.body?.commentId!!)
        }

        // When: 첫 페이지 무한 스크롤 조회
        val scrollResponse: ResponseEntity<Array<CommentResponse>> = restTemplate.getForEntity(
            "http://localhost:$port/api/v1/comments/scroll?articleId=$articleId&pageSize=5&lastParentCommentId=${Comment.EMPTY_ID}&lastCommentId=${Comment.EMPTY_ID}",
            Array<CommentResponse>::class.java
        )

        // Then
        assert(scrollResponse.statusCode == HttpStatus.OK)
        assert(scrollResponse.body?.size == 5)

        // ID 오름차순으로 정렬되어 있는지 확인
        val comments = scrollResponse.body!!
        for (i in 0 until comments.size - 1) {
            assert(comments[i].commentId <= comments[i + 1].commentId)
        }
    }

    @Test
    fun `댓글 삭제 테스트`() {
        // Given: 댓글 생성
        val request = CreateCommentRequest(
            content = "삭제될 댓글",
            parentCommentId = Comment.EMPTY_ID,
            articleId = 1L,
            writerId = 1L
        )

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(request, headers)

        val createResponse: ResponseEntity<CommentResponse> = restTemplate.postForEntity(
            "http://localhost:$port/api/v1/comments",
            entity,
            CommentResponse::class.java
        )

        val commentId = createResponse.body?.commentId!!

        // When: 댓글 삭제
        val deleteResponse: ResponseEntity<Void> = restTemplate.exchange(
            "http://localhost:$port/api/v1/comments/$commentId",
            HttpMethod.DELETE,
            null,
            Void::class.java
        )

        // Then
        assert(deleteResponse.statusCode == HttpStatus.NO_CONTENT)
    }

    @Test
    fun `댓글 전체 사이클 테스트 - 생성_조회_삭제`() {
        val articleId = 300L
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        // 생성 테스트
        val createRequest = CreateCommentRequest(
            content = "사이클 테스트 댓글",
            parentCommentId = Comment.EMPTY_ID,
            articleId = articleId,
            writerId = 1L
        )

        val createEntity = HttpEntity(createRequest, headers)
        val createResponse: ResponseEntity<CommentResponse> = restTemplate.postForEntity(
            "http://localhost:$port/api/v1/comments",
            createEntity,
            CommentResponse::class.java
        )

        assert(createResponse.statusCode == HttpStatus.CREATED)
        assert(createResponse.body?.content == "사이클 테스트 댓글")

        val commentId = createResponse.body?.commentId!!

        // 조회 테스트 (페이지 조회로 확인)
        val pageResponse: ResponseEntity<GetCommentPageResponse> = restTemplate.getForEntity(
            "http://localhost:$port/api/v1/comments?articleId=$articleId&page=1&pageSize=10&movablePageCount=5",
            GetCommentPageResponse::class.java
        )

        assert(pageResponse.statusCode == HttpStatus.OK)
        assert(pageResponse.body?.comments?.size == 1)
        assert(pageResponse.body?.comments?.first()?.content == "사이클 테스트 댓글")

        // 삭제 테스트
        val deleteResponse: ResponseEntity<Void> = restTemplate.exchange(
            "http://localhost:$port/api/v1/comments/$commentId",
            HttpMethod.DELETE,
            null,
            Void::class.java
        )

        assert(deleteResponse.statusCode == HttpStatus.NO_CONTENT)

        // 삭제 후 조회 테스트 (빈 결과 확인)
        val afterDeleteResponse: ResponseEntity<GetCommentPageResponse> = restTemplate.getForEntity(
            "http://localhost:$port/api/v1/comments?articleId=$articleId&page=1&pageSize=10&movablePageCount=5",
            GetCommentPageResponse::class.java
        )

        assert(afterDeleteResponse.statusCode == HttpStatus.OK)
        assert(afterDeleteResponse.body?.comments?.isEmpty() == true)
    }
}