package me.helloc.enterpriseboard.domain.exception

class BusinessException(
    val errorCode: ErrorCode,
    message: String
) : RuntimeException(message)
