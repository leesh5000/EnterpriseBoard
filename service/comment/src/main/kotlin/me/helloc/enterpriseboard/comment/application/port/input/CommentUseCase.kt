package me.helloc.enterpriseboard.comment.application.port.input

import me.helloc.enterpriseboard.comment.application.port.input.command.*
import me.helloc.enterpriseboard.comment.application.port.input.query.*

/**
 * 댓글 유스케이스 인터페이스
 */
interface CommentUseCase {
    // Command operations
    fun createComment(command: CreateCommentCommand): CreateCommentResult
    fun updateComment(command: UpdateCommentCommand): UpdateCommentResult
    fun deleteComment(command: DeleteCommentCommand): DeleteCommentResult
    
    // Query operations
    fun getComment(query: GetCommentQuery): GetCommentResult
    fun getCommentsByArticle(query: GetCommentsByArticleQuery): GetCommentsByArticleResult
    fun getCommentsByWriter(query: GetCommentsByWriterQuery): GetCommentsByWriterResult
}