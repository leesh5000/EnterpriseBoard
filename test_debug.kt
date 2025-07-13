import me.helloc.enterpriseboard.domain.model.CommentPath

fun main() {
    val emptyPath = CommentPath()
    println("Empty path: '${emptyPath.path}'")
    println("Empty path depth: ${emptyPath.getDepth()}")
    println("Empty path isRoot: ${emptyPath.isRoot()}")
    
    val firstChild = emptyPath.createChildPath("")
    println("First child path: '${firstChild.path}'")
    println("First child depth: ${firstChild.getDepth()}")
    println("First child isRoot: ${firstChild.isRoot()}")
}