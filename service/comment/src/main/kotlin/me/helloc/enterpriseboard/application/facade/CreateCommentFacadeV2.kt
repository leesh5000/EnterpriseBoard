package me.helloc.enterpriseboard.application.facade

import me.helloc.enterpriseboard.application.port.`in`.CreateCommentUseCaseV2
import me.helloc.enterpriseboard.application.port.out.CommentRepositoryV2
import me.helloc.enterpriseboard.domain.model.CommentV2
import me.helloc.enterpriseboard.domain.service.CommentFactoryV2
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class CreateCommentFacadeV2(
    private val repository: CommentRepositoryV2,
    private val commentFactory: CommentFactoryV2,
) : CreateCommentUseCaseV2 {

    override fun create(content: String, parentPath: String, articleId: Long, writerId: Long): CommentV2 {
        return if (parentPath.isEmpty()) {
            val descendantsLastPath = repository.getDescendantsLastPath(articleId, parentPath)
            val rootComment = commentFactory.createRootComment(content, articleId, writerId, descendantsLastPath)
            repository.save(rootComment)
        } else {
            val parent = repository.getByPath(parentPath)
            val descendantsLastPath = repository.getDescendantsLastPath(articleId, parent.commentPath.path)
            val childComment = commentFactory.createChildComment(content, articleId, writerId, parent, descendantsLastPath)
            repository.save(childComment)
        }
    }
}
