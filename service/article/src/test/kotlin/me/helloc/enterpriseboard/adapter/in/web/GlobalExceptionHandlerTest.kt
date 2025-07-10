package me.helloc.enterpriseboard.adapter.`in`.web

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.helloc.enterpriseboard.domain.exception.BusinessException
import me.helloc.enterpriseboard.domain.exception.ErrorCode
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

class GlobalExceptionHandlerTest : StringSpec({
    
    lateinit var globalExceptionHandler: GlobalExceptionHandler
    
    beforeEach {
        globalExceptionHandler = GlobalExceptionHandler()
    }
    
    "BusinessException이 올바른 HTTP 상태 코드로 변환되어야 한다" {
        // Given
        val exception = ErrorCode.NOT_FOUND_ARTICLE.toException("articleId" to 123)
        
        // When
        val response = globalExceptionHandler.handleBusinessException(exception)
        
        // Then
        response.statusCode shouldBe HttpStatus.NOT_FOUND
        response.body shouldNotBe null
        response.body?.code shouldBe "NOT_FOUND_ARTICLE"
        response.body?.message shouldBe "ID 123에 해당하는 게시글이 존재하지 않습니다."
        response.body?.status shouldBe 404
        response.body?.timestamp shouldNotBe null
    }
    
    "NoSuchElementException이 404 Not Found로 처리되어야 한다" {
        // Given
        val exception = NoSuchElementException("Resource not found")
        
        // When
        val response = globalExceptionHandler.handleNoSuchElementException(exception)
        
        // Then
        response.statusCode shouldBe HttpStatus.NOT_FOUND
        response.body shouldNotBe null
        response.body?.code shouldBe "RESOURCE_NOT_FOUND"
        response.body?.message shouldBe "Resource not found"
        response.body?.status shouldBe 404
        response.body?.timestamp shouldNotBe null
    }
    
    "일반 Exception이 500 Internal Server Error로 처리되어야 한다" {
        // Given
        val exception = RuntimeException("Unexpected error")
        
        // When
        val response = globalExceptionHandler.handleGenericException(exception)
        
        // Then
        response.statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR
        response.body shouldNotBe null
        response.body?.code shouldBe "INTERNAL_SERVER_ERROR"
        response.body?.message shouldBe "An unexpected error occurred"
        response.body?.status shouldBe 500
        response.body?.timestamp shouldNotBe null
    }
    
    "INVALID_ARTICLE_TITLE 에러가 400 Bad Request로 처리되어야 한다" {
        // Given
        val exception = ErrorCode.INVALID_ARTICLE_TITLE.toException(
            "minLength" to 1,
            "maxLength" to 100
        )
        
        // When
        val response = globalExceptionHandler.handleBusinessException(exception)
        
        // Then
        response.statusCode shouldBe HttpStatus.BAD_REQUEST
        response.body?.code shouldBe "INVALID_ARTICLE_TITLE"
        response.body?.status shouldBe 400
    }
    
    "PERMISSION_DENIED 에러가 403 Forbidden으로 처리되어야 한다" {
        // Given
        val exception = ErrorCode.PERMISSION_DENIED.toException(
            "userId" to 1,
            "articleId" to 2
        )
        
        // When
        val response = globalExceptionHandler.handleBusinessException(exception)
        
        // Then
        response.statusCode shouldBe HttpStatus.FORBIDDEN
        response.body?.code shouldBe "PERMISSION_DENIED"
        response.body?.status shouldBe 403
    }
})