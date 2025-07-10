package me.helloc.enterpriseboard.application.facade

import me.helloc.enterpriseboard.application.port.`in`.UpdateCommentCommand
import me.helloc.enterpriseboard.application.port.`in`.UpdateCommentUseCase
import me.helloc.enterpriseboard.application.port.out.CommentRepository
import me.helloc.enterpriseboard.domain.model.Comment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateCommentFacade(
    private val commentRepository: CommentRepository
) : UpdateCommentUseCase {

    override fun update(command: UpdateCommentCommand): Comment {
        val comment = commentRepository.findById(command.commentId)
        
        if (comment.isNull()) {
            return Comment.empty()
        }
        
        val updatedComment = comment.update(command.content)
        return commentRepository.save(updatedComment)
    }
}