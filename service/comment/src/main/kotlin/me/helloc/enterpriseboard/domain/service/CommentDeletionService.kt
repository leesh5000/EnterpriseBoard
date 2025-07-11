package me.helloc.enterpriseboard.domain.service

import me.helloc.enterpriseboard.application.port.out.CommentRepository
import me.helloc.enterpriseboard.domain.model.Comment
import org.springframework.stereotype.Service

@Service
class CommentDeletionService(
    private val repository: CommentRepository
) {

    fun deleteComment(comment: Comment) {
        // 댓글 삭제 정책 : 댓글 삭제 시, 자식 댓글이 있는 경우 논리 삭제, 없는 경우 물리 삭제
        if (hasChildren(comment)) {
            logicalDelete(comment)
        } else {
            physicalDelete(comment)
        }
    }

    private fun logicalDelete(comment: Comment) {
        val deletedComment = comment.delete()
        repository.save(deletedComment)
    }

    private fun physicalDelete(comment: Comment) {
        recursiveDelete(comment)
    }

    private fun hasChildren(comment: Comment): Boolean {
        return repository.countBy(comment.articleId, comment.commentId, 2L) == 2L
    }

    private fun recursiveDelete(comment: Comment) {
        repository.deleteById(comment.commentId)
        // 댓글 삭제 정책 : 삭제한 댓글의 부모 댓글이 삭제된 상태이고, 자식 댓글이 없는 경우 부모 댓글도 삭제
        if (!comment.isRoot()) {
            repository.findById(comment.parentCommentId)
                .filter { it.deleted }
                .filter { hasChildren(it).not() }
                .ifPresent { parent -> recursiveDelete(parent) }
        }
    }
}
