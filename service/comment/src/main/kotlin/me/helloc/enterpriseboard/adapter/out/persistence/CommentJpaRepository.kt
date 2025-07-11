package me.helloc.enterpriseboard.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
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
        nativeQuery = true
    )
    fun countBy(articleId: Long, parentCommentId: Long, limit: Long): Long
}
