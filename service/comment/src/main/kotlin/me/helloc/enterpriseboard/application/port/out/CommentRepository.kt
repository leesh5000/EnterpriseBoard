package me.helloc.enterpriseboard.application.port.out

import me.helloc.enterpriseboard.domain.model.Comment

interface CommentRepository {
    fun save(comment: Comment): Comment
    fun findById(commentId: Long): Comment?
    fun findByArticleId(articleId: Long): List<Comment>
    fun findByWriterId(writerId: Long): List<Comment>
    fun deleteById(commentId: Long)
    fun existsById(commentId: Long): Boolean
    
    // 페이지네이션을 위한 메서드들
    fun findAll(
        articleId: Long,
        offset: Long,
        limit: Long
    ): List<Comment>
    
    fun countByArticleId(articleId: Long, limit: Long): Long
    
    // 무한 스크롤을 위한 메서드들
    fun findAllInfiniteScroll(
        articleId: Long,
        limit: Long
    ): List<Comment>
    
    fun findAllInfiniteScroll(
        articleId: Long,
        limit: Long,
        lastCommentId: Long
    ): List<Comment>
}