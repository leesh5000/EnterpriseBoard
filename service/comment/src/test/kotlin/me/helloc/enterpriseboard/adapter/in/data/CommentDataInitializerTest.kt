package me.helloc.enterpriseboard.adapter.`in`.data

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import me.helloc.enterpriseboard.adapter.out.persistence.CommentJpaEntity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CommentDataInitializerTest {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Test
    fun `1200만건의 댓글 데이터가 정확히 생성되어야 한다`() {
        // Given
        val dataInitializer = CommentDataInitializer()
        
        // When
        dataInitializer.initialize()
        
        // Then
        val totalCount = entityManager.createQuery(
            "SELECT COUNT(c) FROM CommentJpaEntity c", 
            Long::class.java
        ).singleResult
        
        assertEquals(12_000_000L, totalCount)
    }

    @Test
    fun `생성된 댓글 중 절반은 루트 댓글이고 절반은 답글이어야 한다`() {
        // Given
        val dataInitializer = CommentDataInitializer()
        
        // When
        dataInitializer.initialize()
        
        // Then
        val rootCommentCount = entityManager.createQuery(
            "SELECT COUNT(c) FROM CommentJpaEntity c WHERE c.commentId = c.parentCommentId", 
            Long::class.java
        ).singleResult
        
        val replyCommentCount = entityManager.createQuery(
            "SELECT COUNT(c) FROM CommentJpaEntity c WHERE c.commentId != c.parentCommentId", 
            Long::class.java
        ).singleResult
        
        assertEquals(6_000_000L, rootCommentCount)
        assertEquals(6_000_000L, replyCommentCount)
    }

    @Test
    fun `모든 답글의 부모 댓글이 실제로 존재해야 한다`() {
        // Given
        val dataInitializer = CommentDataInitializer()
        
        // When
        dataInitializer.initialize()
        
        // Then
        val orphanRepliesCount = entityManager.createQuery(
            """
            SELECT COUNT(reply) 
            FROM CommentJpaEntity reply 
            WHERE reply.commentId != reply.parentCommentId 
            AND reply.parentCommentId NOT IN (
                SELECT root.commentId 
                FROM CommentJpaEntity root 
                WHERE root.commentId = root.parentCommentId
            )
            """, 
            Long::class.java
        ).singleResult
        
        assertEquals(0L, orphanRepliesCount)
    }

    @Test
    fun `모든 댓글이 동일한 게시글에 속해야 한다`() {
        // Given
        val dataInitializer = CommentDataInitializer()
        
        // When
        dataInitializer.initialize()
        
        // Then
        val distinctArticleIds = entityManager.createQuery(
            "SELECT DISTINCT c.articleId FROM CommentJpaEntity c", 
            Long::class.java
        ).resultList
        
        assertEquals(1, distinctArticleIds.size)
        assertEquals(1L, distinctArticleIds[0])
    }
}