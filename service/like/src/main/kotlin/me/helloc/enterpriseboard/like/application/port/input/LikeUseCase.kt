package me.helloc.enterpriseboard.like.application.port.input

import me.helloc.enterpriseboard.like.application.port.input.command.*
import me.helloc.enterpriseboard.like.application.port.input.query.*

/**
 * 좋아요 유스케이스 인터페이스
 */
interface LikeUseCase {
    // Command operations
    fun addLike(command: AddLikeCommand): AddLikeResult
    fun removeLike(command: RemoveLikeCommand): RemoveLikeResult
    fun toggleLike(command: ToggleLikeCommand): ToggleLikeResult
    
    // Query operations
    fun getLikesByArticle(query: GetLikesByArticleQuery): GetLikesByArticleResult
    fun getLikesByUser(query: GetLikesByUserQuery): GetLikesByUserResult
    fun getLikeCount(query: GetLikeCountQuery): GetLikeCountResult
    fun isLikedByUser(query: IsLikedByUserQuery): IsLikedByUserResult
}