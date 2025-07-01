package me.helloc.enterpriseboard.view.domain.repository

import me.helloc.enterpriseboard.view.domain.model.*
import java.time.LocalDateTime
import java.util.Optional

/**
 * 조회수 도메인 리포지토리 인터페이스
 */
interface ViewRepository {
    fun save(view: View): View
    fun findById(viewId: ViewId): Optional<View>
    fun findByArticleId(articleId: ArticleId): List<View>
    fun findByUserId(userId: UserId): List<View>
    fun countByArticleId(articleId: ArticleId): Long
    fun countByArticleIdAndDateRange(articleId: ArticleId, startDate: LocalDateTime, endDate: LocalDateTime): Long
    fun existsByArticleIdAndIpAddressWithinHours(articleId: ArticleId, ipAddress: IpAddress, hours: Int): Boolean
    fun existsByArticleIdAndUserIdWithinHours(articleId: ArticleId, userId: UserId, hours: Int): Boolean
}