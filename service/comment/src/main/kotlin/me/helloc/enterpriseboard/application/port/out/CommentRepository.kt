package me.helloc.enterpriseboard.application.port.out

import me.helloc.enterpriseboard.domain.model.Comment
import java.util.*

interface CommentRepository {
    fun save(comment: Comment): Comment
    fun findById(commentId: Long): Optional<Comment>
    fun getById(commentId: Long): Comment
    fun deleteById(commentId: Long)
    fun existsById(commentId: Long): Boolean
    fun countBy(articleId: Long, parentCommentId: Long, limit: Long): Long

}
