package me.helloc.enterpriseboard.comment.domain.service

import me.helloc.enterpriseboard.comment.domain.model.*
import me.helloc.enterpriseboard.comment.domain.repository.CommentRepository
import org.springframework.stereotype.Component

/**
 * 댓글 도메인 서비스
 */
@Component
class CommentDomainService(
    private val commentRepository: CommentRepository
) {
    
    /**
     * 댓글 작성 권한 확인
     */
    fun canCreateComment(articleId: ArticleId, writerId: WriterId): Boolean {
        // 비즈니스 규칙: 하루 댓글 작성 제한 등
        return true // 현재는 항상 허용
    }
    
    /**
     * 댓글 수정 권한 확인
     */
    fun canUpdateComment(comment: Comment, requesterId: WriterId): Boolean {
        return comment.writerId == requesterId
    }
    
    /**
     * 댓글 삭제 권한 확인
     */
    fun canDeleteComment(comment: Comment, requesterId: WriterId): Boolean {
        return comment.writerId == requesterId
    }
    
    /**
     * 대댓글 작성 가능 여부 확인
     */
    fun canReplyToComment(parentCommentId: CommentId): Boolean {
        val parentComment = commentRepository.findById(parentCommentId)
        return parentComment.isPresent && !parentComment.get().isReply() // 2단계 이상 중첩 방지
    }
    
    /**
     * 댓글 삭제 시 하위 댓글도 삭제할지 결정
     */
    fun shouldDeleteRepliesWhenParentDeleted(parentCommentId: CommentId): Boolean {
        return true // 부모 댓글 삭제 시 대댓글도 함께 삭제
    }
}