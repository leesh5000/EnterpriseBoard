package me.helloc.enterpriseboard.domain.service

import me.helloc.common.snowflake.Snowflake
import me.helloc.enterpriseboard.domain.exception.ErrorCode
import me.helloc.enterpriseboard.domain.model.Comment
import org.springframework.stereotype.Service

@Service
class CommentFactory {

    private val snowflake: Snowflake = Snowflake()

    fun createChildComment(
        content: String,
        articleId: Long,
        writerId: Long,
        parent: Comment,
    ): Comment {
        validateRootComment(parent)
        return Comment.create(
            commentId = snowflake.nextId(),
            content = content,
            parentCommentId = parent.commentId,
            articleId = articleId,
            writerId = writerId
        )
    }

    private fun validateRootComment(parent: Comment) {
        // 댓글 생성 정책 : 최상위 댓글에만 답글을 달 수 있음 (댓글은 최대 2 depth)
        if (!parent.isRoot()) {
            throw ErrorCode.NO_ROOT_COMMENT_REPLY.toException("parentCommentId" to parent.commentId)
        }
        // 댓글 생성 정책 : 삭제된 댓글에 답글을 달 수 없음
        if (parent.deleted) {
            throw ErrorCode.DELETED_COMMENT_REPLY.toException("parentCommentId" to parent.commentId)
        }
    }

    fun createRootComment(
        content: String,
        articleId: Long,
        writerId: Long,
    ): Comment {
        return Comment.create(
            commentId = snowflake.nextId(),
            content = content,
            parentCommentId = Comment.NO_PARENT_ID,
            articleId = articleId,
            writerId = writerId
        )
    }
}
