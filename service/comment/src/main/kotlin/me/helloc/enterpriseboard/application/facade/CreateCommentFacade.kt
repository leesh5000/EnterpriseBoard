package me.helloc.enterpriseboard.application.facade

import me.helloc.common.snowflake.Snowflake
import me.helloc.enterpriseboard.application.port.`in`.CreateCommentCommand
import me.helloc.enterpriseboard.application.port.`in`.CreateCommentUseCase
import me.helloc.enterpriseboard.application.port.out.CommentRepository
import me.helloc.enterpriseboard.domain.model.Comment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CreateCommentFacade(
    private val commentRepository: CommentRepository,
    private val snowflake: Snowflake
) : CreateCommentUseCase {

    override fun create(command: CreateCommentCommand): Comment {
        val comment = Comment.create(
            commentId = snowflake.nextId(),
            content = command.content,
            parentCommentId = command.parentCommentId,
            articleId = command.articleId,
            writerId = command.writerId
        )
        
        return commentRepository.save(comment)
    }
}