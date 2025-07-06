package me.helloc.enterpriseboard.application.facade

import me.helloc.common.snowflake.Snowflake
import me.helloc.enterpriseboard.application.port.`in`.CreateArticleCommand
import me.helloc.enterpriseboard.application.port.`in`.CreateArticleUseCase
import me.helloc.enterpriseboard.application.port.out.ArticleRepository
import me.helloc.enterpriseboard.domain.model.Article
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CreateArticleFacade(
    private val articleRepository: ArticleRepository
) : CreateArticleUseCase {

    private val snowflake: Snowflake = Snowflake()

    override fun create(command: CreateArticleCommand): Article {
        val article = Article.create(
            articleId = snowflake.nextId(),
            title = command.title,
            content = command.content,
            boardId = command.boardId,
            writerId = command.writerId
        )
        
        return articleRepository.save(article)
    }
}