package me.helloc.enterpriseboard.domain.service

import me.helloc.enterpriseboard.domain.exception.ErrorCode
import me.helloc.enterpriseboard.domain.model.Comment
import org.springframework.stereotype.Service

@Service
class CommentCreatePolicyChecker {

    fun check(root: Comment) {
        if (!root.isRoot()) {
            throw ErrorCode.NO_ROOT_COMMENT_REPLY.toException("parentCommentId" to root.commentId)
        }
        if (root.deleted) {
            throw ErrorCode.DELETED_COMMENT_REPLY.toException("parentCommentId" to root.commentId)
        }
    }
}
