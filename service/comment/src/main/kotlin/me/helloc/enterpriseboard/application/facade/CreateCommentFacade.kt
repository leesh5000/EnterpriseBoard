package me.helloc.enterpriseboard.application.facade

import me.helloc.enterpriseboard.application.port.`in`.CreateCommentUseCase
import me.helloc.enterpriseboard.application.port.out.CommentRepository
import me.helloc.enterpriseboard.domain.model.Comment
import me.helloc.enterpriseboard.domain.service.CommentFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class CreateCommentFacade(
    private val repository: CommentRepository,
    private val commentFactory: CommentFactory,
) : CreateCommentUseCase {

    override fun create(content: String, parentCommentId: Long, articleId: Long, writerId: Long): Comment {
        return if (parentCommentId == Comment.EMPTY_ID) {
            val rootComment = commentFactory.createRootComment(content, articleId, writerId)
            repository.save(rootComment)
        } else {
            val parent = repository.getById(parentCommentId)
            val childComment = commentFactory.createChildComment(content, articleId, writerId, parent)
            repository.save(childComment)
        }
    }
}
