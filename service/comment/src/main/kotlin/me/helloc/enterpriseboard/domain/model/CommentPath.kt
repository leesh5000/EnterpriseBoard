package me.helloc.enterpriseboard.domain.model

data class CommentPath(
    val path: String
) {

    init {
        if (isDepthOverflowed(path)) {
            throw IllegalArgumentException("Comment path is too long: ${path.length} characters, max allowed is $MAX_DEPTH")
        }
        if (path.length % DEPTH_CHUNK_SIZE != 0) {
            throw IllegalArgumentException("Comment path must be a multiple of $DEPTH_CHUNK_SIZE characters")
        }
    }

    fun getDepth(): Int {
        return calculateDepth(path)
    }

    fun isRoot(): Boolean {
        return getDepth() == 1
    }

    fun getParentPath(): String {
        return path.substring(0, path.length - DEPTH_CHUNK_SIZE)
    }

    fun createChildPath(descendantsTopPath: String = ""): CommentPath {
        if (descendantsTopPath.isEmpty()) {
            return CommentPath(
                path = path + MIN_CHUNK
            )
        }
        val childrenTopPath = findChildrenTopPath(descendantsTopPath)
        return CommentPath(
            path = increase(childrenTopPath)
        )
    }

    fun findChildrenTopPath(descendantsTopPath: String): String {
        return descendantsTopPath.substring(0, (getDepth() + 1) * DEPTH_CHUNK_SIZE)
    }

    fun increase(path: String): String {
        val lastChunk = path.substring(path.length - DEPTH_CHUNK_SIZE)
        if (isChunkOverflowed(lastChunk)) {
            throw IllegalArgumentException("Cannot increase path: $path, last chunk is overflowed")
        }
        
        val charsetLength = CHARSET.length
        
        // 청크를 62진수 정수로 변환
        var value = 0
        for (ch in lastChunk) {
            value = value * charsetLength + CHARSET.indexOf(ch)
        }
        
        // 1 증가
        value += 1
        
        // 정수를 다시 62진수 문자열로 변환
        var result = ""
        var tempValue = value
        repeat(DEPTH_CHUNK_SIZE) {
            result = CHARSET[tempValue % charsetLength] + result
            tempValue /= charsetLength
        }
        
        val parentPath = path.substring(0, path.length - DEPTH_CHUNK_SIZE)
        return parentPath + result
    }

    private fun isChunkOverflowed(lastChunk: String): Boolean {
        return lastChunk == MAX_CHUNK
    }

    companion object {
        const val CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        const val DEPTH_CHUNK_SIZE = 5
        const val MAX_DEPTH = 5
        val MIN_CHUNK = CHARSET[0].toString().repeat(DEPTH_CHUNK_SIZE)
        val MAX_CHUNK = CHARSET[CHARSET.length - 1].toString().repeat(DEPTH_CHUNK_SIZE)

        fun isDepthOverflowed(path: String): Boolean {
            return path.length > MAX_DEPTH
        }

        fun calculateDepth(path: String): Int {
            return path.length / DEPTH_CHUNK_SIZE
        }
    }
}
