package me.helloc.enterpriseboard.view.domain.model

import me.helloc.enterpriseboard.view.domain.event.DomainEvent
import me.helloc.enterpriseboard.view.domain.event.ViewCountIncreasedEvent
import java.time.LocalDateTime

/**
 * 조회수 도메인 모델
 */
data class View(
    val viewId: ViewId,
    val articleId: ArticleId,
    val userId: UserId?,
    val ipAddress: IpAddress,
    val userAgent: UserAgent?,
    val createdAt: LocalDateTime,
    private val events: MutableList<DomainEvent> = mutableListOf()
) {

    fun getEvents(): List<DomainEvent> = events.toList()

    fun clearEvents() = events.clear()

    companion object {
        fun create(
            viewId: ViewId,
            articleId: ArticleId,
            userId: UserId?,
            ipAddress: IpAddress,
            userAgent: UserAgent?
        ): View {
            val now = LocalDateTime.now()
            val view = View(
                viewId = viewId,
                articleId = articleId,
                userId = userId,
                ipAddress = ipAddress,
                userAgent = userAgent,
                createdAt = now
            )

            view.events.add(
                ViewCountIncreasedEvent(
                    viewId = viewId,
                    articleId = articleId,
                    userId = userId,
                    ipAddress = ipAddress,
                    createdAt = now
                )
            )

            return view
        }
    }
}
