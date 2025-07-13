package me.helloc.enterpriseboard.application.facade

import me.helloc.enterpriseboard.application.port.`in`.DeleteCommentUseCaseV2
import me.helloc.enterpriseboard.application.port.out.CommentRepositoryV2
import me.helloc.enterpriseboard.domain.service.CommentDeletionServiceV2
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DeleteCommentFacadeV2(
    private val repository: CommentRepositoryV2,
    private val commentDeletionService: CommentDeletionServiceV2
) : DeleteCommentUseCaseV2 {

    override fun delete(commentId: Long) {
        repository.findById(commentId)
            .filter { it.deleted.not() }
            .ifPresent { comment -> commentDeletionService.deleteComment(comment) }
    }
}
