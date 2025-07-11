package me.helloc.enterpriseboard.adapter.`in`.web

import me.helloc.enterpriseboard.domain.exception.ErrorCode
import org.springframework.http.HttpStatus

object ErrorCodeHttpStatusMapper {
    
    private val errorCodeToHttpStatus = mapOf(
        ErrorCode.NOT_FOUND_ARTICLE to HttpStatus.NOT_FOUND,
        ErrorCode.INVALID_ARTICLE_TITLE to HttpStatus.BAD_REQUEST,
        ErrorCode.PERMISSION_DENIED to HttpStatus.FORBIDDEN
    )
    
    fun getHttpStatus(errorCode: ErrorCode): HttpStatus {
        return errorCodeToHttpStatus[errorCode] ?: HttpStatus.INTERNAL_SERVER_ERROR
    }
}