package me.helloc.enterpriseboard.adapter.`in`.web

import me.helloc.enterpriseboard.domain.exception.ErrorCode
import org.springframework.http.HttpStatus

object ErrorCodeHttpStatusMapper {

    private val errorCodeToHttpStatus = mapOf(
        ErrorCode.COMMENT_NOT_FOUND to HttpStatus.NOT_FOUND,
        ErrorCode.NO_ROOT_COMMENT_REPLY to HttpStatus.BAD_REQUEST
    )

    fun getHttpStatus(errorCode: ErrorCode): HttpStatus {
        return errorCodeToHttpStatus[errorCode] ?: HttpStatus.INTERNAL_SERVER_ERROR
    }
}
