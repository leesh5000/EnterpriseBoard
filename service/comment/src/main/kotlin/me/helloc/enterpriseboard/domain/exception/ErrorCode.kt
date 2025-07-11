package me.helloc.enterpriseboard.domain.exception

enum class ErrorCode(
    internal val messageTemplate: String) {
    // 댓글 관련 에러 코드
    COMMENT_NOT_FOUND("ID {commentId}에 해당하는 댓글을 찾을 수 없습니다."),
    ROOT_COMMENT_NOT_FOUND("ID {parentCommentId}에 해당하는 최상위 댓글을 찾을 수 없습니다."),
    DELETED_COMMENT_REPLY("ID {parentCommentId}에 해당하는 댓글은 이미 삭제되었습니다. 삭제된 댓글에는 답글을 달 수 없습니다."),
    NO_ROOT_COMMENT_REPLY("ID {parentCommentId}는 최상위 댓글이 아닙니다. 최상위 댓글에만 댓글을 달 수 있습니다."),
    ;

    fun message(params: Map<String, Any>): String {
        var result = messageTemplate
        params.forEach { (key, value) ->
            result = result.replace("{$key}", value.toString())
        }
        return result
    }

    // 편의 메서드들
    fun message(vararg pairs: Pair<String, Any>): String {
        return message(mapOf(*pairs))
    }

    // BusinessException 생성 편의 메서드들
    fun toException(params: Map<String, Any>): BusinessException {
        return BusinessException(
            errorCode = this,
            message = this.message(params)
        )
    }

    fun toException(vararg pairs: Pair<String, Any>): BusinessException {
        return BusinessException(
            errorCode = this,
            message = this.message(mapOf(*pairs))
        )
    }

    fun toException(): BusinessException {
        return BusinessException(
            errorCode = this,
            message = this.messageTemplate
        )
    }
}
