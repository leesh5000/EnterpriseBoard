package me.helloc.enterpriseboard.application.facade

import me.helloc.common.snowflake.Snowflake
import me.helloc.enterpriseboard.application.port.`in`.CreateArticleUseCase
import me.helloc.enterpriseboard.application.port.out.ArticleRepository
import me.helloc.enterpriseboard.domain.model.Article
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class CreateArticleFacade(
    private val articleRepository: ArticleRepository
) : CreateArticleUseCase {

    private val snowflake: Snowflake = Snowflake()

    override fun create(title: String, content: String, boardId: Long, writerId: Long): Article {
        val article = Article.create(
            articleId = snowflake.nextId(),
            title = title,
            content = content,
            boardId = boardId,
            writerId = writerId
        )

        return articleRepository.save(article)
    }
}
