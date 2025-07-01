package me.helloc.enterpriseboard.like.domain.repository

import me.helloc.enterpriseboard.like.domain.model.*
import java.util.Optional

/**
 * 좋아요 도메인 리포지토리 인터페이스
 */
interface LikeRepository {
    fun save(like: Like): Like
    fun findById(likeId: LikeId): Optional<Like>
    fun findByArticleIdAndUserId(articleId: ArticleId, userId: UserId): Optional<Like>
    fun findByArticleId(articleId: ArticleId): List<Like>
    fun findByUserId(userId: UserId): List<Like>
    fun deleteById(likeId: LikeId)
    fun deleteByArticleIdAndUserId(articleId: ArticleId, userId: UserId)
    fun existsByArticleIdAndUserId(articleId: ArticleId, userId: UserId): Boolean
    fun countByArticleId(articleId: ArticleId): Long
}