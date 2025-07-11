package me.helloc.enterpriseboard.adapter.`in`.web.dto

data class GetCommentPageResponse(
    val comments: List<CommentResponse>,
    val totalCount: Long
) {
    companion object {
        fun of(
            comments: List<CommentResponse>,
            totalCount: Long
        ): GetCommentPageResponse {
            return GetCommentPageResponse(
                comments = comments,
                totalCount = totalCount
            )
        }
    }
}