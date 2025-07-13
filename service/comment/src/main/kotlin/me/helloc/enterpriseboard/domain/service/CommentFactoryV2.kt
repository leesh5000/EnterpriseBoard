package me.helloc.enterpriseboard.domain.service

import me.helloc.common.snowflake.Snowflake
import me.helloc.enterpriseboard.domain.exception.ErrorCode
import me.helloc.enterpriseboard.domain.model.CommentPath
import me.helloc.enterpriseboard.domain.model.CommentV2
import org.springframework.stereotype.Service

@Service
class CommentFactoryV2 {

    private val snowflake: Snowflake = Snowflake()

    fun createRootComment(
        content: String,
        articleId: Long,
        writerId: Long,
        lastPath: String = ""
    ): CommentV2 {
        return CommentV2(
            commentId = snowflake.nextId(),
            content = content,
            commentPath = CommentPath(CommentPath.next(lastPath)),
            articleId = articleId,
            writerId = writerId
        )
    }

    fun createChildComment(
        content: String,
        articleId: Long,
        writerId: Long,
        parent: CommentV2,
        descendantsLastPath: String,
    ): CommentV2 {
        validateRootComment(parent)
        val commentPath: CommentPath = parent.commentPath.createChildPath(descendantsLastPath)
        return CommentV2(
            commentId = snowflake.nextId(),
            content = content,
            commentPath = commentPath,
            articleId = articleId,
            writerId = writerId
        )
    }

    private fun validateRootComment(parent: CommentV2) {
        // 댓글 생성 정책 : 최상위 댓글에만 답글을 달 수 있음 (댓글은 최대 2 depth)
        if (!parent.isRoot()) {
            throw ErrorCode.NO_ROOT_COMMENT_REPLY.toException("parentCommentId" to parent.commentId)
        }
        // 댓글 생성 정책 : 삭제된 댓글에 답글을 달 수 없음
        if (parent.deleted) {
            throw ErrorCode.DELETED_COMMENT_REPLY.toException("parentCommentId" to parent.commentId)
        }
    }
}
