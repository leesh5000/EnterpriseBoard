package me.helloc.enterpriseboard.articleread.domain.model

import java.time.LocalDateTime

/**
 * 게시글 읽기 전용 모델 - CQRS의 Read Model
 */
data class ArticleReadModel(
    val articleId: ArticleId,
    val title: Title,
    val content: Content,
    val summary: Summary,
    val boardId: BoardId,
    val boardName: BoardName,
    val writerId: WriterId,
    val writerNickname: WriterNickname,
    val viewCount: ViewCount,
    val likeCount: LikeCount,
    val commentCount: CommentCount,
    val tags: List<Tag>,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
    val isHot: Boolean,
    val hotRank: HotRank?
) {
    
    fun updateViewCount(newViewCount: ViewCount): ArticleReadModel {
        return copy(viewCount = newViewCount)
    }
    
    fun updateLikeCount(newLikeCount: LikeCount): ArticleReadModel {
        return copy(likeCount = newLikeCount)
    }
    
    fun updateCommentCount(newCommentCount: CommentCount): ArticleReadModel {
        return copy(commentCount = newCommentCount)
    }
    
    fun updateHotStatus(isHot: Boolean, hotRank: HotRank?): ArticleReadModel {
        return copy(isHot = isHot, hotRank = hotRank)
    }
    
    fun generateSummary(): Summary {
        val cleanContent = content.value.replace(Regex("<[^>]*>"), "") // HTML 태그 제거
        val summaryText = if (cleanContent.length > 100) {
            cleanContent.substring(0, 100) + "..."
        } else {
            cleanContent
        }
        return Summary(summaryText)
    }
    
    companion object {
        fun create(
            articleId: ArticleId,
            title: Title,
            content: Content,
            boardId: BoardId,
            boardName: BoardName,
            writerId: WriterId,
            writerNickname: WriterNickname,
            createdAt: LocalDateTime,
            modifiedAt: LocalDateTime,
            tags: List<Tag> = emptyList()
        ): ArticleReadModel {
            val readModel = ArticleReadModel(
                articleId = articleId,
                title = title,
                content = content,
                summary = Summary(""),
                boardId = boardId,
                boardName = boardName,
                writerId = writerId,
                writerNickname = writerNickname,
                viewCount = ViewCount(0),
                likeCount = LikeCount(0),
                commentCount = CommentCount(0),
                tags = tags,
                createdAt = createdAt,
                modifiedAt = modifiedAt,
                isHot = false,
                hotRank = null
            )
            
            return readModel.copy(summary = readModel.generateSummary())
        }
    }
}