package me.helloc.enterpriseboard.comment.application.service

import me.helloc.enterpriseboard.comment.application.port.input.CommentUseCase
import me.helloc.enterpriseboard.comment.application.port.input.command.*
import me.helloc.enterpriseboard.comment.application.port.input.query.*
import me.helloc.enterpriseboard.comment.application.port.output.EventPublisher
import me.helloc.enterpriseboard.comment.application.port.output.IdGenerator
import me.helloc.enterpriseboard.comment.domain.event.CommentDeletedEvent
import me.helloc.enterpriseboard.comment.domain.model.*
import me.helloc.enterpriseboard.comment.domain.repository.CommentRepository
import me.helloc.enterpriseboard.comment.domain.service.CommentDomainService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 댓글 애플리케이션 서비스
 */
@Service
@Transactional
class CommentApplicationService(
    private val commentRepository: CommentRepository,
    private val commentDomainService: CommentDomainService,
    private val eventPublisher: EventPublisher,
    private val idGenerator: IdGenerator
) : CommentUseCase {

    override fun createComment(command: CreateCommentCommand): CreateCommentResult {
        val commentId = CommentId(idGenerator.generateId())
        val articleId = ArticleId(command.articleId)
        val content = CommentContent(command.content)
        val writerId = WriterId(command.writerId)
        val parentCommentId = command.parentCommentId?.let { CommentId(it) }
        
        // 도메인 규칙 검증
        if (!commentDomainService.canCreateComment(articleId, writerId)) {
            throw IllegalStateException("Cannot create comment for this article")
        }
        
        // 대댓글인 경우 추가 검증
        if (parentCommentId != null && !commentDomainService.canReplyToComment(parentCommentId)) {
            throw IllegalArgumentException("Cannot reply to this comment")
        }
        
        // 댓글 생성
        val comment = Comment.create(commentId, articleId, content, writerId, parentCommentId)
        
        // 저장
        val savedComment = commentRepository.save(comment)
        
        // 이벤트 발행
        eventPublisher.publishAll(savedComment.getEvents())
        savedComment.clearEvents()
        
        return CreateCommentResult(
            commentId = savedComment.commentId.value,
            articleId = savedComment.articleId.value,
            content = savedComment.content.value,
            writerId = savedComment.writerId.value,
            parentCommentId = savedComment.parentCommentId?.value,
            createdAt = savedComment.createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
    }

    override fun updateComment(command: UpdateCommentCommand): UpdateCommentResult {
        val commentId = CommentId(command.commentId)
        val requesterId = WriterId(command.requesterId)
        val newContent = CommentContent(command.content)
        
        val comment = commentRepository.findById(commentId)
            .orElseThrow { IllegalArgumentException("Comment not found with id: ${command.commentId}") }
        
        // 권한 검증
        if (!commentDomainService.canUpdateComment(comment, requesterId)) {
            throw IllegalStateException("No permission to update this comment")
        }
        
        // 업데이트
        val updatedComment = comment.update(newContent)
        val savedComment = commentRepository.save(updatedComment)
        
        // 이벤트 발행
        eventPublisher.publishAll(savedComment.getEvents())
        savedComment.clearEvents()
        
        return UpdateCommentResult(
            commentId = savedComment.commentId.value,
            content = savedComment.content.value,
            modifiedAt = savedComment.modifiedAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
    }

    override fun deleteComment(command: DeleteCommentCommand): DeleteCommentResult {
        val commentId = CommentId(command.commentId)
        val requesterId = WriterId(command.requesterId)
        
        val comment = commentRepository.findById(commentId)
            .orElseThrow { IllegalArgumentException("Comment not found with id: ${command.commentId}") }
        
        // 권한 검증
        if (!commentDomainService.canDeleteComment(comment, requesterId)) {
            throw IllegalStateException("No permission to delete this comment")
        }
        
        // 대댓글이 있는 경우 함께 삭제할지 결정
        if (commentDomainService.shouldDeleteRepliesWhenParentDeleted(commentId)) {
            val replies = commentRepository.findRepliesByParentCommentId(commentId)
            replies.forEach { reply ->
                commentRepository.deleteById(reply.commentId)
                eventPublisher.publish(CommentDeletedEvent(reply.commentId, reply.articleId))
            }
        }
        
        // 댓글 삭제
        commentRepository.deleteById(commentId)
        
        // 삭제 이벤트 발행
        val deleteEvent = CommentDeletedEvent(commentId, comment.articleId)
        eventPublisher.publish(deleteEvent)
        
        return DeleteCommentResult(
            commentId = commentId.value,
            deletedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
    }

    @Transactional(readOnly = true)
    override fun getComment(query: GetCommentQuery): GetCommentResult {
        val comment = commentRepository.findById(CommentId(query.commentId))
            .orElseThrow { IllegalArgumentException("Comment not found with id: ${query.commentId}") }
        
        return GetCommentResult(
            comment = CommentView(
                commentId = comment.commentId.value,
                articleId = comment.articleId.value,
                content = comment.content.value,
                writerId = comment.writerId.value,
                parentCommentId = comment.parentCommentId?.value,
                createdAt = comment.createdAt,
                modifiedAt = comment.modifiedAt
            )
        )
    }

    @Transactional(readOnly = true)
    override fun getCommentsByArticle(query: GetCommentsByArticleQuery): GetCommentsByArticleResult {
        val comments = commentRepository.findByArticleId(ArticleId(query.articleId))
        
        // 댓글을 계층 구조로 변환 (부모-자식 관계)
        val commentMap = comments.associateBy { it.commentId.value }
        val rootComments = comments.filter { it.parentCommentId == null }
        
        val commentViews = rootComments.map { comment ->
            val replies = comments.filter { it.parentCommentId == comment.commentId }
                .map { reply ->
                    CommentView(
                        commentId = reply.commentId.value,
                        articleId = reply.articleId.value,
                        content = reply.content.value,
                        writerId = reply.writerId.value,
                        parentCommentId = reply.parentCommentId?.value,
                        createdAt = reply.createdAt,
                        modifiedAt = reply.modifiedAt
                    )
                }
            
            CommentView(
                commentId = comment.commentId.value,
                articleId = comment.articleId.value,
                content = comment.content.value,
                writerId = comment.writerId.value,
                parentCommentId = comment.parentCommentId?.value,
                createdAt = comment.createdAt,
                modifiedAt = comment.modifiedAt,
                replies = replies
            )
        }
        
        return GetCommentsByArticleResult(comments = commentViews)
    }

    @Transactional(readOnly = true)
    override fun getCommentsByWriter(query: GetCommentsByWriterQuery): GetCommentsByWriterResult {
        val comments = commentRepository.findByWriterId(WriterId(query.writerId))
        
        return GetCommentsByWriterResult(
            comments = comments.map { comment ->
                CommentView(
                    commentId = comment.commentId.value,
                    articleId = comment.articleId.value,
                    content = comment.content.value,
                    writerId = comment.writerId.value,
                    parentCommentId = comment.parentCommentId?.value,
                    createdAt = comment.createdAt,
                    modifiedAt = comment.modifiedAt
                )
            }
        )
    }
}