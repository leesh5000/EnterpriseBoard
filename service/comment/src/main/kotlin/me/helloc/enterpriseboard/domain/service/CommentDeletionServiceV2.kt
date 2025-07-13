package me.helloc.enterpriseboard.domain.service

import me.helloc.enterpriseboard.application.port.out.CommentRepositoryV2
import me.helloc.enterpriseboard.domain.model.CommentV2
import org.springframework.stereotype.Service

@Service
class CommentDeletionServiceV2(
    private val repository: CommentRepositoryV2
) {

    fun deleteComment(comment: CommentV2) {
        // 댓글 삭제 정책 : 댓글 삭제 시, 자식 댓글이 있는 경우 논리 삭제, 없는 경우 물리 삭제
        if (hasChildren(comment)) {
            logicalDelete(comment)
        } else {
            physicalDelete(comment)
        }
    }

    private fun logicalDelete(comment: CommentV2) {
        val deletedComment = comment.delete()
        repository.save(deletedComment)
    }

    private fun physicalDelete(comment: CommentV2) {
        recursiveDelete(comment)
    }

    private fun hasChildren(comment: CommentV2): Boolean {
        return repository.getDescendantsLastPath(comment.articleId, comment.commentPath.path)
            .isNotEmpty()
    }

    private fun recursiveDelete(comment: CommentV2) {
        repository.deleteById(comment.commentId)
        // 댓글 삭제 정책 : 삭제한 댓글의 부모 댓글이 삭제된 상태이고, 자식 댓글이 없는 경우 부모 댓글도 삭제
        if (!comment.isRoot()) {
            repository.findByPath(comment.commentPath.getParentPath())
                .filter { it.deleted }
                .filter { hasChildren(it).not() }
                .ifPresent { parent -> recursiveDelete(parent) }
        }
    }
}
