package me.helloc.enterpriseboard.adapter.`in`.web.dto

data class ArticlePageResponse(
    val articles: List<ArticleResponse>,
    val totalCount: Long,
) {
    companion object {
        fun of(
            articles: List<ArticleResponse>,
            totalCount: Long,
        ): ArticlePageResponse {
            return ArticlePageResponse(
                articles = articles,
                totalCount = totalCount,
            )
        }
    }
}
