package me.helloc.enterpriseboard.application.port.out

import me.helloc.enterpriseboard.domain.model.CommentV2
import java.util.*

interface CommentRepositoryV2 {
    fun findByPath(path: String): Optional<CommentV2>
    fun getDescendantsLastPath(articleId: Long, pathPrefix: String): String
    fun getByPath(path: String): CommentV2
    fun save(rootComment: CommentV2): CommentV2
    fun findById(commentId: Long): Optional<CommentV2>
    fun deleteById(commentId: Long)
}
