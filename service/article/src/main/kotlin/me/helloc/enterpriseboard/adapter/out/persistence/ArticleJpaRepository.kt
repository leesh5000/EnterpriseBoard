package me.helloc.enterpriseboard.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleJpaRepository : JpaRepository<ArticleJpaEntity, Long> {
    fun findByBoardId(boardId: Long): List<ArticleJpaEntity>
    fun findByWriterId(writerId: Long): List<ArticleJpaEntity>
}