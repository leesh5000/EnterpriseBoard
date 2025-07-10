package me.helloc.enterpriseboard.domain.exception

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class ErrorCodeTest : StringSpec({
    
    "ErrorCode는 파라미터를 사용해 메시지를 생성할 수 있다" {
        // Given
        val errorCode = ErrorCode.NOT_FOUND_ARTICLE
        val articleId = 123L
        
        // When
        val message = errorCode.message("articleId" to articleId)
        
        // Then
        message shouldBe "ID 123에 해당하는 게시글이 존재하지 않습니다."
    }
    
    "ErrorCode는 여러 파라미터를 사용해 메시지를 생성할 수 있다" {
        // Given
        val errorCode = ErrorCode.INVALID_ARTICLE_TITLE
        val minLength = 1
        val maxLength = 100
        
        // When
        val message = errorCode.message(
            "minLength" to minLength,
            "maxLength" to maxLength
        )
        
        // Then
        message shouldBe "게시글 제목은 1자 이상 100자 이하여야 합니다."
    }
    
    "ErrorCode는 Map을 사용해 메시지를 생성할 수 있다" {
        // Given
        val errorCode = ErrorCode.PERMISSION_DENIED
        val params = mapOf(
            "userId" to 456,
            "articleId" to 789
        )
        
        // When
        val message = errorCode.message(params)
        
        // Then
        message shouldBe "사용자 456는 게시글 789에 대한 권한이 없습니다."
    }
    
    "ErrorCode는 BusinessException을 생성할 수 있다" {
        // Given
        val errorCode = ErrorCode.NOT_FOUND_ARTICLE
        val articleId = 999L
        
        // When
        val exception = errorCode.toException("articleId" to articleId)
        
        // Then
        exception shouldNotBe null
        exception.errorCode shouldBe errorCode
        exception.message shouldBe "ID 999에 해당하는 게시글이 존재하지 않습니다."
    }
    
    "ErrorCode는 파라미터 없이 BusinessException을 생성할 수 있다" {
        // Given
        val errorCode = ErrorCode.NOT_FOUND_ARTICLE
        
        // When
        val exception = errorCode.toException()
        
        // Then
        exception shouldNotBe null
        exception.errorCode shouldBe errorCode
        exception.message shouldBe "ID {articleId}에 해당하는 게시글이 존재하지 않습니다."
    }
})