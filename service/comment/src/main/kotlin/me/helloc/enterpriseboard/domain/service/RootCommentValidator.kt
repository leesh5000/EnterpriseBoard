package me.helloc.enterpriseboard.domain.service

import me.helloc.enterpriseboard.domain.exception.ErrorCode
import me.helloc.enterpriseboard.domain.model.Comment
import org.springframework.stereotype.Service

@Service
class RootCommentValidator {

    fun validate(root: Comment) {
        if (root.isNull()) {
            throw ErrorCode.ROOT_COMMENT_NOT_FOUND.toException("parentCommentId" to root.commentId)
        }
        if (!root.isRoot()) {
            throw ErrorCode.NOT_ROOT_COMMENT.toException("parentCommentId" to root.commentId)
        }
        if (root.deleted) {
            throw ErrorCode.COMMENT_DELETED.toException("parentCommentId" to root.commentId)
        }
    }
}
