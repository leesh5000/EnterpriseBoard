package me.helloc.enterpriseboard.application.facade

import me.helloc.enterpriseboard.application.port.out.CommentRepositoryV2
import me.helloc.enterpriseboard.domain.model.CommentV2
import java.util.*

class FakeCommentRepositoryV2 : CommentRepositoryV2 {

    private val comments = mutableMapOf<Long, CommentV2>()
    private val pathToComment = mutableMapOf<String, CommentV2>()

    override fun save(comment: CommentV2): CommentV2 {
        comments[comment.commentId] = comment
        pathToComment[comment.commentPath.path] = comment
        return comment
    }

    override fun findByPath(path: String): Optional<CommentV2> {
        return Optional.ofNullable(pathToComment[path])
    }

    override fun getByPath(path: String): CommentV2 {
        return findByPath(path).orElseThrow {
            RuntimeException("Comment not found with path: $path")
        }
    }

    override fun findById(commentId: Long): Optional<CommentV2> {
        return Optional.ofNullable(comments[commentId])
    }

    override fun deleteById(commentId: Long) {
        val comment = comments[commentId]
        if (comment != null) {
            pathToComment.remove(comment.commentPath.path)
            comments.remove(commentId)
        }
    }

    override fun getDescendantsLastPath(articleId: Long, pathPrefix: String): String {
        // 해당 경로 prefix로 시작하는 댓글들 중 가장 최신 것의 경로를 반환
        val descendants = pathToComment.values
            .filter { it.articleId == articleId }
            .filter { it.commentPath.path.startsWith(pathPrefix) }
            .filter { it.commentPath.path.length == pathPrefix.length + 5 } // 직계 자식만
            .sortedBy { it.commentPath.path }

        return descendants.lastOrNull()?.commentPath?.path ?: ""
    }

    fun clear() {
        comments.clear()
        pathToComment.clear()
    }

    fun getAllComments(): List<CommentV2> {
        return comments.values.toList()
    }
}
