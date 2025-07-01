package me.helloc.enterpriseboard.article.application.port.input.command

import me.helloc.enterpriseboard.article.domain.model.*

/**
 * 명령 객체들 - 애플리케이션 레이어에서 사용하는 입력 데이터
 */

data class CreateArticleCommand(
    val title: String,
    val content: String,
    val boardId: Long,
    val writerId: Long
)

data class UpdateArticleCommand(
    val articleId: Long,
    val title: String,
    val content: String,
    val requesterId: Long
)

data class DeleteArticleCommand(
    val articleId: Long,
    val requesterId: Long
)

// Result objects
data class CreateArticleResult(
    val articleId: Long,
    val title: String,
    val content: String,
    val boardId: Long,
    val writerId: Long,
    val createdAt: String
)

data class UpdateArticleResult(
    val articleId: Long,
    val title: String,
    val content: String,
    val modifiedAt: String
)

data class DeleteArticleResult(
    val articleId: Long,
    val deletedAt: String
)