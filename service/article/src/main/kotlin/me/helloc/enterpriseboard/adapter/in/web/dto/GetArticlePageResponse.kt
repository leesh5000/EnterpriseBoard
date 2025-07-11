package me.helloc.enterpriseboard.adapter.`in`.web.dto

data class GetArticlePageResponse(
    val articles: List<ArticleResponse>,
    val totalCount: Long
) {
    companion object {
        fun of(
            articles: List<ArticleResponse>,
            totalCount: Long
        ): GetArticlePageResponse {
            return GetArticlePageResponse(
                articles = articles,
                totalCount = totalCount
            )
        }
    }
}