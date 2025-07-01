package me.helloc.enterpriseboard.article.application.service

import me.helloc.enterpriseboard.article.application.port.input.ArticleUseCase
import me.helloc.enterpriseboard.article.application.port.input.command.*
import me.helloc.enterpriseboard.article.application.port.input.query.*
import me.helloc.enterpriseboard.article.application.port.output.EventPublisher
import me.helloc.enterpriseboard.article.application.port.output.IdGenerator
import me.helloc.enterpriseboard.article.domain.event.ArticleDeletedEvent
import me.helloc.enterpriseboard.article.domain.model.*
import me.helloc.enterpriseboard.article.domain.repository.ArticleRepository
import me.helloc.enterpriseboard.article.domain.service.ArticleDomainService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 애플리케이션 서비스 - 유스케이스 구현체
 */
@Service
@Transactional
class ArticleApplicationService(
    private val articleRepository: ArticleRepository,
    private val articleDomainService: ArticleDomainService,
    private val eventPublisher: EventPublisher,
    private val idGenerator: IdGenerator
) : ArticleUseCase {

    override fun createArticle(command: CreateArticleCommand): CreateArticleResult {
        val articleId = ArticleId(idGenerator.generateId())
        val title = Title(command.title)
        val content = Content(command.content)
        val boardId = BoardId(command.boardId)
        val writerId = WriterId(command.writerId)
        
        // 도메인 규칙 검증
        if (!articleDomainService.canCreateArticleInBoard(boardId, writerId)) {
            throw IllegalStateException("Cannot create more articles in this board today")
        }
        
        if (articleDomainService.isDuplicateTitle(boardId, title)) {
            throw IllegalArgumentException("Article with same title already exists in this board")
        }
        
        // 도메인 객체 생성
        val article = Article.create(articleId, title, content, boardId, writerId)
        
        // 저장
        val savedArticle = articleRepository.save(article)
        
        // 이벤트 발행
        eventPublisher.publishAll(savedArticle.getEvents())
        savedArticle.clearEvents()
        
        return CreateArticleResult(
            articleId = savedArticle.articleId.value,
            title = savedArticle.title.value,
            content = savedArticle.content.value,
            boardId = savedArticle.boardId.value,
            writerId = savedArticle.writerId.value,
            createdAt = savedArticle.createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
    }

    override fun updateArticle(command: UpdateArticleCommand): UpdateArticleResult {
        val articleId = ArticleId(command.articleId)
        val requesterId = WriterId(command.requesterId)
        val newTitle = Title(command.title)
        val newContent = Content(command.content)
        
        val article = articleRepository.findById(articleId)
            .orElseThrow { IllegalArgumentException("Article not found with id: ${command.articleId}") }
        
        // 권한 검증
        if (!articleDomainService.canUpdateArticle(article, requesterId)) {
            throw IllegalStateException("No permission to update this article")
        }
        
        // 제목 중복 확인 (자신 제외)
        if (articleDomainService.isDuplicateTitle(article.boardId, newTitle, articleId)) {
            throw IllegalArgumentException("Article with same title already exists in this board")
        }
        
        // 업데이트
        val updatedArticle = article.update(newTitle, newContent)
        val savedArticle = articleRepository.save(updatedArticle)
        
        // 이벤트 발행
        eventPublisher.publishAll(savedArticle.getEvents())
        savedArticle.clearEvents()
        
        return UpdateArticleResult(
            articleId = savedArticle.articleId.value,
            title = savedArticle.title.value,
            content = savedArticle.content.value,
            modifiedAt = savedArticle.modifiedAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
    }

    override fun deleteArticle(command: DeleteArticleCommand): DeleteArticleResult {
        val articleId = ArticleId(command.articleId)
        val requesterId = WriterId(command.requesterId)
        
        val article = articleRepository.findById(articleId)
            .orElseThrow { IllegalArgumentException("Article not found with id: ${command.articleId}") }
        
        // 권한 검증
        if (!articleDomainService.canDeleteArticle(article, requesterId)) {
            throw IllegalStateException("No permission to delete this article")
        }
        
        // 삭제
        articleRepository.deleteById(articleId)
        
        // 삭제 이벤트 발행
        val deleteEvent = ArticleDeletedEvent(articleId, article.boardId)
        eventPublisher.publish(deleteEvent)
        
        return DeleteArticleResult(
            articleId = articleId.value,
            deletedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
    }

    @Transactional(readOnly = true)
    override fun getArticle(query: GetArticleQuery): GetArticleResult {
        val article = articleRepository.findById(ArticleId(query.articleId))
            .orElseThrow { IllegalArgumentException("Article not found with id: ${query.articleId}") }
        
        return GetArticleResult(
            article = ArticleView(
                articleId = article.articleId.value,
                title = article.title.value,
                content = article.content.value,
                boardId = article.boardId.value,
                writerId = article.writerId.value,
                createdAt = article.createdAt,
                modifiedAt = article.modifiedAt
            )
        )
    }

    @Transactional(readOnly = true)
    override fun getArticlesByBoard(query: GetArticlesByBoardQuery): GetArticlesByBoardResult {
        val articles = articleRepository.findByBoardId(BoardId(query.boardId))
        
        return GetArticlesByBoardResult(
            articles = articles.map { article ->
                ArticleView(
                    articleId = article.articleId.value,
                    title = article.title.value,
                    content = article.content.value,
                    boardId = article.boardId.value,
                    writerId = article.writerId.value,
                    createdAt = article.createdAt,
                    modifiedAt = article.modifiedAt
                )
            }
        )
    }

    @Transactional(readOnly = true)
    override fun getArticlesByWriter(query: GetArticlesByWriterQuery): GetArticlesByWriterResult {
        val articles = articleRepository.findByWriterId(WriterId(query.writerId))
        
        return GetArticlesByWriterResult(
            articles = articles.map { article ->
                ArticleView(
                    articleId = article.articleId.value,
                    title = article.title.value,
                    content = article.content.value,
                    boardId = article.boardId.value,
                    writerId = article.writerId.value,
                    createdAt = article.createdAt,
                    modifiedAt = article.modifiedAt
                )
            }
        )
    }

    @Transactional(readOnly = true)
    override fun getAllArticles(query: GetAllArticlesQuery): GetAllArticlesResult {
        val articles = articleRepository.findAll()
        
        return GetAllArticlesResult(
            articles = articles.map { article ->
                ArticleView(
                    articleId = article.articleId.value,
                    title = article.title.value,
                    content = article.content.value,
                    boardId = article.boardId.value,
                    writerId = article.writerId.value,
                    createdAt = article.createdAt,
                    modifiedAt = article.modifiedAt
                )
            },
            totalCount = articles.size.toLong(),
            page = query.page,
            size = query.size
        )
    }
}