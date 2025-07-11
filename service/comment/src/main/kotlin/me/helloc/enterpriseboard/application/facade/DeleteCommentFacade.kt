package me.helloc.enterpriseboard.application.facade

import me.helloc.enterpriseboard.application.port.`in`.DeleteCommentUseCase
import me.helloc.enterpriseboard.application.port.out.CommentRepository
import me.helloc.enterpriseboard.domain.service.CommentDeletionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DeleteCommentFacade(
    private val repository: CommentRepository,
    private val commentDeletionService: CommentDeletionService
) : DeleteCommentUseCase {

    override fun delete(commentId: Long) {
        repository.findById(commentId)
            .filter { it.deleted.not() }
            .ifPresent { comment -> commentDeletionService.deleteComment(comment) }
    }
}
