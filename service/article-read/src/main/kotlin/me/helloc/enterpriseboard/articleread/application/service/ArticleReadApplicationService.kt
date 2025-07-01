package me.helloc.enterpriseboard.articleread.application.service

import me.helloc.enterpriseboard.articleread.application.port.input.ArticleReadUseCase
import me.helloc.enterpriseboard.articleread.application.port.input.command.*
import me.helloc.enterpriseboard.articleread.application.port.input.query.*
import me.helloc.enterpriseboard.articleread.domain.model.*
import me.helloc.enterpriseboard.articleread.domain.repository.ArticleReadModelRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 게시글 읽기 애플리케이션 서비스 - CQRS Query Side
 */
@Service
@Transactional
class ArticleReadApplicationService(
    private val articleReadModelRepository: ArticleReadModelRepository
) : ArticleReadUseCase {

    override fun createArticleReadModel(command: CreateArticleReadModelCommand): CreateArticleReadModelResult {
        val articleId = ArticleId(command.articleId)
        
        // 이미 존재하는지 확인
        if (articleReadModelRepository.existsById(articleId)) {
            return CreateArticleReadModelResult(articleId = command.articleId, created = false)
        }
        
        val readModel = ArticleReadModel.create(
            articleId = articleId,
            title = Title(command.title),
            content = Content(command.content),
            boardId = BoardId(command.boardId),
            boardName = BoardName(command.boardName),
            writerId = WriterId(command.writerId),
            writerNickname = WriterNickname(command.writerNickname),
            createdAt = command.createdAt,
            modifiedAt = command.createdAt,
            tags = command.tags.map { Tag(it) }
        )
        
        articleReadModelRepository.save(readModel)
        
        return CreateArticleReadModelResult(articleId = command.articleId, created = true)
    }

    override fun updateArticleReadModel(command: UpdateArticleReadModelCommand): UpdateArticleReadModelResult {
        val articleId = ArticleId(command.articleId)
        val existingReadModel = articleReadModelRepository.findById(articleId)
            .orElseThrow { IllegalArgumentException("Article read model not found: ${command.articleId}") }
        
        val updatedReadModel = existingReadModel.copy(
            title = Title(command.title),
            content = Content(command.content),
            modifiedAt = command.modifiedAt,
            tags = command.tags.map { Tag(it) }
        ).let { it.copy(summary = it.generateSummary()) }
        
        articleReadModelRepository.save(updatedReadModel)
        
        return UpdateArticleReadModelResult(articleId = command.articleId, updated = true)
    }

    override fun updateViewCount(command: UpdateViewCountCommand): UpdateViewCountResult {
        val articleId = ArticleId(command.articleId)
        val existingReadModel = articleReadModelRepository.findById(articleId)
            .orElseThrow { IllegalArgumentException("Article read model not found: ${command.articleId}") }
        
        val updatedReadModel = existingReadModel.updateViewCount(ViewCount(command.viewCount))
        articleReadModelRepository.save(updatedReadModel)
        
        return UpdateViewCountResult(articleId = command.articleId, viewCount = command.viewCount)
    }

    override fun updateLikeCount(command: UpdateLikeCountCommand): UpdateLikeCountResult {
        val articleId = ArticleId(command.articleId)
        val existingReadModel = articleReadModelRepository.findById(articleId)
            .orElseThrow { IllegalArgumentException("Article read model not found: ${command.articleId}") }
        
        val updatedReadModel = existingReadModel.updateLikeCount(LikeCount(command.likeCount))
        articleReadModelRepository.save(updatedReadModel)
        
        return UpdateLikeCountResult(articleId = command.articleId, likeCount = command.likeCount)
    }

    override fun updateCommentCount(command: UpdateCommentCountCommand): UpdateCommentCountResult {
        val articleId = ArticleId(command.articleId)
        val existingReadModel = articleReadModelRepository.findById(articleId)
            .orElseThrow { IllegalArgumentException("Article read model not found: ${command.articleId}") }
        
        val updatedReadModel = existingReadModel.updateCommentCount(CommentCount(command.commentCount))
        articleReadModelRepository.save(updatedReadModel)
        
        return UpdateCommentCountResult(articleId = command.articleId, commentCount = command.commentCount)
    }

    override fun updateHotStatus(command: UpdateHotStatusCommand): UpdateHotStatusResult {
        val articleId = ArticleId(command.articleId)
        val existingReadModel = articleReadModelRepository.findById(articleId)
            .orElseThrow { IllegalArgumentException("Article read model not found: ${command.articleId}") }
        
        val hotRank = command.hotRank?.let { HotRank(it) }
        val updatedReadModel = existingReadModel.updateHotStatus(command.isHot, hotRank)
        articleReadModelRepository.save(updatedReadModel)
        
        return UpdateHotStatusResult(
            articleId = command.articleId,
            isHot = command.isHot,
            hotRank = command.hotRank
        )
    }

    override fun deleteArticleReadModel(command: DeleteArticleReadModelCommand): DeleteArticleReadModelResult {
        val articleId = ArticleId(command.articleId)
        
        if (!articleReadModelRepository.existsById(articleId)) {
            return DeleteArticleReadModelResult(articleId = command.articleId, deleted = false)
        }
        
        articleReadModelRepository.deleteById(articleId)
        
        return DeleteArticleReadModelResult(articleId = command.articleId, deleted = true)
    }

    @Transactional(readOnly = true)
    override fun getArticle(query: GetArticleQuery): GetArticleResult {
        val articleId = ArticleId(query.articleId)
        val readModel = articleReadModelRepository.findById(articleId)
            .orElseThrow { IllegalArgumentException("Article not found: ${query.articleId}") }
        
        return GetArticleResult(article = readModel.toView())
    }

    @Transactional(readOnly = true)
    override fun getArticlesByBoard(query: GetArticlesByBoardQuery): GetArticlesByBoardResult {
        val boardId = BoardId(query.boardId)
        val articles = articleReadModelRepository.findByBoardIdWithPaging(boardId, query.page, query.size)
        val totalCount = articleReadModelRepository.countByBoardId(boardId)
        
        return GetArticlesByBoardResult(
            articles = articles.map { it.toView() },
            totalCount = totalCount,
            page = query.page,
            size = query.size,
            hasNext = (query.page + 1) * query.size < totalCount
        )
    }

    @Transactional(readOnly = true)
    override fun getArticlesByWriter(query: GetArticlesByWriterQuery): GetArticlesByWriterResult {
        val writerId = WriterId(query.writerId)
        val articles = articleReadModelRepository.findByWriterIdWithPaging(writerId, query.page, query.size)
        val totalCount = articleReadModelRepository.countByWriterId(writerId)
        
        return GetArticlesByWriterResult(
            articles = articles.map { it.toView() },
            totalCount = totalCount,
            page = query.page,
            size = query.size,
            hasNext = (query.page + 1) * query.size < totalCount
        )
    }

    @Transactional(readOnly = true)
    override fun getHotArticles(query: GetHotArticlesQuery): GetHotArticlesResult {
        val articles = if (query.boardId != null) {
            val boardId = BoardId(query.boardId)
            articleReadModelRepository.findHotArticlesByBoard(boardId, query.limit)
        } else {
            articleReadModelRepository.findHotArticles(query.limit)
        }
        
        return GetHotArticlesResult(
            articles = articles.map { it.toView() },
            boardId = query.boardId
        )
    }

    @Transactional(readOnly = true)
    override fun searchArticles(query: SearchArticlesQuery): SearchArticlesResult {
        val articles = when (query.searchType) {
            SearchType.TITLE_ONLY -> articleReadModelRepository.findByTitleContaining(query.keyword, query.page, query.size)
            SearchType.CONTENT_ONLY -> articleReadModelRepository.findByContentContaining(query.keyword, query.page, query.size)
            SearchType.TITLE_AND_CONTENT -> articleReadModelRepository.findByTitleOrContentContaining(query.keyword, query.page, query.size)
            SearchType.TAG -> articleReadModelRepository.findByTag(Tag(query.keyword), query.page, query.size)
        }
        
        // 간단한 총 개수 추정 (실제로는 별도 카운트 쿼리 필요)
        val totalCount = articles.size.toLong()
        
        return SearchArticlesResult(
            articles = articles.map { it.toView() },
            keyword = query.keyword,
            searchType = query.searchType,
            totalCount = totalCount,
            page = query.page,
            size = query.size,
            hasNext = articles.size == query.size
        )
    }

    @Transactional(readOnly = true)
    override fun getRecentArticles(query: GetRecentArticlesQuery): GetRecentArticlesResult {
        val articles = if (query.boardId != null) {
            val boardId = BoardId(query.boardId)
            articleReadModelRepository.findRecentArticlesByBoard(boardId, query.limit)
        } else {
            articleReadModelRepository.findRecentArticles(query.limit)
        }
        
        return GetRecentArticlesResult(
            articles = articles.map { it.toView() },
            boardId = query.boardId
        )
    }

    private fun ArticleReadModel.toView(): ArticleReadView {
        return ArticleReadView(
            articleId = articleId.value,
            title = title.value,
            content = content.value,
            summary = summary.value,
            boardId = boardId.value,
            boardName = boardName.value,
            writerId = writerId.value,
            writerNickname = writerNickname.value,
            viewCount = viewCount.value,
            likeCount = likeCount.value,
            commentCount = commentCount.value,
            tags = tags.map { it.value },
            createdAt = createdAt,
            modifiedAt = modifiedAt,
            isHot = isHot,
            hotRank = hotRank?.value
        )
    }
}