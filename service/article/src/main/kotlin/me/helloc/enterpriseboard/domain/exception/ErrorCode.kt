package me.helloc.enterpriseboard.domain.exception

enum class ErrorCode(
    internal val messageTemplate: String) {
    // 게시글 관련 에러 코드
    NOT_FOUND_ARTICLE("ID {articleId}에 해당하는 게시글이 존재하지 않습니다."),
    INVALID_ARTICLE_TITLE("게시글 제목은 {minLength}자 이상 {maxLength}자 이하여야 합니다."),
    PERMISSION_DENIED("사용자 {userId}는 게시글 {articleId}에 대한 권한이 없습니다.")
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
