package me.helloc.enterpriseboard.adapter.`in`.web

import me.helloc.enterpriseboard.domain.exception.BusinessException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {
    
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(ex: BusinessException): ResponseEntity<ErrorResponse> {
        val httpStatus = ErrorCodeHttpStatusMapper.getHttpStatus(ex.errorCode)
        logger.warn("Business exception occurred: ${ex.errorCode}, message: ${ex.message}")
        
        return ResponseEntity.status(httpStatus)
            .body(ErrorResponse(
                code = ex.errorCode.name,
                message = ex.message ?: "Business error occurred",
                status = httpStatus.value(),
                timestamp = LocalDateTime.now()
            ))
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(ex: NoSuchElementException): ResponseEntity<ErrorResponse> {
        logger.warn("Resource not found: ${ex.message}")
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(
                code = "RESOURCE_NOT_FOUND",
                message = ex.message ?: "Resource not found",
                status = HttpStatus.NOT_FOUND.value(),
                timestamp = LocalDateTime.now()
            ))
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected error occurred", ex)
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(
                code = "INTERNAL_SERVER_ERROR",
                message = "An unexpected error occurred",
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                timestamp = LocalDateTime.now()
            ))
    }
}

data class ErrorResponse(
    val code: String,
    val message: String,
    val status: Int,
    val timestamp: LocalDateTime
)
