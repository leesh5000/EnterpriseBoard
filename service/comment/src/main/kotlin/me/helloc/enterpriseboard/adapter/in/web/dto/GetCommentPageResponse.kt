package me.helloc.enterpriseboard.adapter.`in`.web.dto

data class GetCommentPageResponse(
    val comments: List<CommentResponse>,
    val visibleRangeCount: Long
) {
    companion object {
        fun of(
            comments: List<CommentResponse>,
            totalCount: Long
        ): GetCommentPageResponse {
            return GetCommentPageResponse(
                comments = comments,
                visibleRangeCount = totalCount
            )
        }
    }
}
