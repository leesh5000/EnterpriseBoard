package me.helloc.enterpriseboard.application.facade

import me.helloc.common.snowflake.Snowflake
import me.helloc.enterpriseboard.application.port.`in`.CreateCommentUseCase
import me.helloc.enterpriseboard.application.port.out.CommentRepository
import me.helloc.enterpriseboard.domain.model.Comment
import me.helloc.enterpriseboard.domain.service.RootCommentValidator
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class CreateCommentFacade(
    private val repository: CommentRepository,
    private val rootCommentValidator: RootCommentValidator,
) : CreateCommentUseCase {

    private val snowflake: Snowflake = Snowflake()

    override fun create(content: String, parentCommentId: Long, articleId: Long, writerId: Long): Comment {
        return if (parentCommentId == Comment.NO_PARENT_ID) {
            createRootComment(content, articleId, writerId)
        } else {
            createChildComment(parentCommentId, content, articleId, writerId)
        }
    }

    private fun createChildComment(
        parentCommentId: Long,
        content: String,
        articleId: Long,
        writerId: Long,
    ): Comment {
        val parent: Comment? = repository.findById(parentCommentId)
        rootCommentValidator.validate(parent)
        val comment = Comment.create(
            commentId = snowflake.nextId(),
            content = content,
            parentCommentId = parent!!.commentId,
            articleId = articleId,
            writerId = writerId
        )
        return repository.save(comment)
    }

    private fun createRootComment(
        content: String,
        articleId: Long,
        writerId: Long,
    ): Comment {
        val comment = Comment.create(
            commentId = snowflake.nextId(),
            content = content,
            parentCommentId = Comment.NO_PARENT_ID,
            articleId = articleId,
            writerId = writerId
        )
        return repository.save(comment)
    }
}
