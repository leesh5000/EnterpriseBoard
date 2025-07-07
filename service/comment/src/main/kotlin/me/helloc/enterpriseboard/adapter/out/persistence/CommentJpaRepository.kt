package me.helloc.enterpriseboard.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CommentJpaRepository : JpaRepository<CommentJpaEntity, Long> {
    
    fun findByArticleIdOrderByCommentIdDesc(articleId: Long): List<CommentJpaEntity>
    
    fun findByWriterIdOrderByCommentIdDesc(writerId: Long): List<CommentJpaEntity>
    
    @Query("""
        SELECT c FROM CommentJpaEntity c 
        WHERE c.articleId = :articleId 
        ORDER BY c.commentId DESC 
        LIMIT :limit OFFSET :offset
    """)
    fun findByArticleIdWithPagination(
        @Param("articleId") articleId: Long,
        @Param("offset") offset: Long,
        @Param("limit") limit: Long
    ): List<CommentJpaEntity>
    
    @Query("""
        SELECT COUNT(c) FROM CommentJpaEntity c 
        WHERE c.articleId = :articleId 
        LIMIT :limit
    """)
    fun countByArticleIdWithLimit(
        @Param("articleId") articleId: Long,
        @Param("limit") limit: Long
    ): Long
    
    @Query("""
        SELECT c FROM CommentJpaEntity c 
        WHERE c.articleId = :articleId 
        ORDER BY c.commentId DESC 
        LIMIT :limit
    """)
    fun findByArticleIdForInfiniteScroll(
        @Param("articleId") articleId: Long,
        @Param("limit") limit: Long
    ): List<CommentJpaEntity>
    
    @Query("""
        SELECT c FROM CommentJpaEntity c 
        WHERE c.articleId = :articleId 
        AND c.commentId < :lastCommentId 
        ORDER BY c.commentId DESC 
        LIMIT :limit
    """)
    fun findByArticleIdForInfiniteScrollWithCursor(
        @Param("articleId") articleId: Long,
        @Param("lastCommentId") lastCommentId: Long,
        @Param("limit") limit: Long
    ): List<CommentJpaEntity>
}