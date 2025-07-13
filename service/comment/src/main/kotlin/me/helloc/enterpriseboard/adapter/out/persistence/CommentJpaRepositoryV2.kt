package me.helloc.enterpriseboard.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface CommentJpaRepositoryV2: JpaRepository<CommentJpaEntityV2, Long> {

    @Query("SELECT c FROM CommentJpaEntityV2 c WHERE c.path = :path")
    fun findByPath(@Param("path") path: String): Optional<CommentJpaEntityV2>

    @Query(
        """
        SELECT path
        FROM comment_v2
        WHERE article_id = :articleId
            AND path > :pathPrefix
            AND path LIKE CONCAT(:pathPrefix, '%')
        ORDER BY path DESC
        LIMIT 1
        """,
        nativeQuery = true
    )
    fun findDescendantsLastPath(
        @Param("articleId") articleId: Long,
        @Param("pathPrefix") pathPrefix: String,
    ): Optional<String>

}
