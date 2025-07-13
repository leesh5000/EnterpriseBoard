package me.helloc.enterpriseboard.adapter.out.persistence

import me.helloc.enterpriseboard.application.port.out.CommentRepositoryV2
import me.helloc.enterpriseboard.domain.exception.ErrorCode
import me.helloc.enterpriseboard.domain.model.CommentV2
import org.springframework.stereotype.Component
import java.util.*

@Component
class CommentJpaAdapterV2(
    private val commentJpaRepositoryV2: CommentJpaRepositoryV2
) : CommentRepositoryV2 {

    override fun findByPath(path: String): Optional<CommentV2> {
        return commentJpaRepositoryV2.findByPath(path)
            .map { it.toDomainModel() }
    }

    override fun getDescendantsLastPath(
        articleId: Long,
        pathPrefix: String,
    ): String {
        return commentJpaRepositoryV2.findDescendantsLastPath(articleId, pathPrefix)
            .orElse("")
    }

    override fun getByPath(path: String): CommentV2 {
        return commentJpaRepositoryV2.findByPath(path)
            .map { it.toDomainModel() }
            .orElseThrow {
                ErrorCode.COMMENT_NOT_FOUND_BY_PATH.toException("path" to path)
            }
    }

    override fun save(rootComment: CommentV2): CommentV2 {
        val entity = CommentJpaEntityV2.from(rootComment)
        val savedEntity = commentJpaRepositoryV2.save(entity)
        return savedEntity.toDomainModel()
    }

    override fun findById(commentId: Long): Optional<CommentV2> {
        return commentJpaRepositoryV2.findById(commentId)
            .map { it.toDomainModel() }
    }

    override fun deleteById(commentId: Long) {
        commentJpaRepositoryV2.deleteById(commentId)
    }
}
