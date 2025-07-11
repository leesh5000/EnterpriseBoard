package me.helloc.enterpriseboard.application.facade

import me.helloc.enterpriseboard.application.port.`in`.DeleteCommentUseCase
import me.helloc.enterpriseboard.application.port.out.CommentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DeleteCommentFacade(
    private val commentRepository: CommentRepository
) : DeleteCommentUseCase {

    override fun delete(commentId: Long) {
        val comment = commentRepository.findById(commentId) ?: return
        
        val deletedComment = comment.delete()
        commentRepository.save(deletedComment)
    }
}