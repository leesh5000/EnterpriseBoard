package me.helloc.enterpriseboard.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CommentJpaRepository : JpaRepository<CommentJpaEntity, Long> {
    @Query(
        value = """
        SELECT COUNT(*)
        FROM (
            SELECT comment_id
            FROM comment
            WHERE article_id = :articleId AND parent_comment_id = :parentCommentId
            LIMIT :limit
        ) t
        """,
        nativeQuery = true)
    fun countBy(articleId: Long, parentCommentId: Long, limit: Long): Long

    @Query(
        value = """
        SELECT comment.comment_id, comment.content, comment.article_id, comment.writer_id, comment.parent_comment_id,
               comment.deleted, comment.created_at
       FROM (
            SELECT comment_id
            FROM comment
            WHERE article_id = :articleId
            ORDER BY parent_comment_id ASC, comment_id ASC
            LIMIT :limit OFFSET :offset
        ) t
        LEFT JOIN comment ON t.comment_id = comment.comment_id
        """,
        nativeQuery = true)
    fun findAll(
        @Param("articleId") articleId: Long,
        @Param("offset") offset: Long,
        @Param("limit") limit: Long,
    ): List<CommentJpaEntity>

    @Query(
        value = """
        SELECT count(*)
        FROM (
            SELECT comment_id
            FROM comment
            WHERE article_id = :articleId
            LIMIT :limit
        ) t
        LEFT JOIN comment ON t.comment_id = comment.comment_id
        """,
        nativeQuery = true)
    fun count(
        @Param("articleId") articleId: Long,
        @Param("limit") limit: Long,
    ): Long

    @Query(
        value = """
        SELECT comment.comment_id, comment.content, comment.article_id, comment.writer_id, comment.parent_comment_id,
               comment.deleted, comment.created_at
        FROM comment
        WHERE article_id = :articleId
        ORDER BY parent_comment_id ASC, comment_id ASC
        LIMIT :limit
        """,
        nativeQuery = true)
    fun findAllInfiniteScroll(
        @Param("articleId") articleId: Long,
        @Param("limit") limit: Long,
    ): List<CommentJpaEntity>

    @Query(
        value = """
        SELECT comment.comment_id, comment.content, comment.article_id, comment.writer_id, comment.parent_comment_id,
               comment.deleted, comment.created_at
        FROM comment
        WHERE article_id = :articleId AND
            (parent_comment_id < :lastCommentParentId OR
            (parent_comment_id = :lastCommentParentId AND comment_id < :lastCommentId))
        ORDER BY parent_comment_id ASC, comment_id ASC
        LIMIT :limit
        """,
        nativeQuery = true)
    fun findAllInfiniteScroll(
        @Param("articleId") articleId: Long,
        @Param("lastCommentParentId") lastParentCommentId: Long,
        @Param("lastCommentId") lastCommentId: Long,
        @Param("limit") limit: Long,
    ): List<CommentJpaEntity>
}
