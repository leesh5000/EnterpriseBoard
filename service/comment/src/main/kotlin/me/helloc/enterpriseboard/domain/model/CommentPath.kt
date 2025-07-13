package me.helloc.enterpriseboard.domain.model

data class CommentPath(
    val path: String
) {

    init {
        if (path.isEmpty()) {
            throw IllegalArgumentException("Comment path cannot be empty")
        }
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

    fun createChildPath(descendantsLastPath: String): CommentPath {
        if (descendantsLastPath.isEmpty()) {
            return CommentPath(
                path = path + MIN_CHUNK
            )
        }
        val childrenLastPath = findChildrenLastPath(descendantsLastPath)
        return CommentPath(
            path = next(childrenLastPath)
        )
    }

    fun findChildrenLastPath(descendantsLastPath: String): String {
        return descendantsLastPath.substring(0, (getDepth() + 1) * DEPTH_CHUNK_SIZE)
    }

    companion object {
        const val CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        const val DEPTH_CHUNK_SIZE = 5
        const val MAX_DEPTH = 5
        val MIN_CHUNK = CHARSET[0].toString().repeat(DEPTH_CHUNK_SIZE)
        val MAX_CHUNK = CHARSET[CHARSET.length - 1].toString().repeat(DEPTH_CHUNK_SIZE)

        // 동적으로 생성되는 정규표현식
        val PATH_REGEX: Regex by lazy {
            createPathRegex()
        }

        private fun createPathRegex(): Regex {
            // CHARSET에서 정규표현식 특수문자 이스케이프 처리
            val escapedCharset = CHARSET
                .replace("\\", "\\\\")
                .replace("]", "\\]")
                .replace("-", "\\-")
                .replace("^", "\\^")
            
            // 정규표현식 동적 생성: ^(?:[charset]{chunkSize}){1,maxDepth}$
            val pattern = "^(?:[$escapedCharset]{$DEPTH_CHUNK_SIZE}){1,$MAX_DEPTH}$"
            return Regex(pattern)
        }

        fun isValidPath(path: String): Boolean {
            return PATH_REGEX.matches(path)
        }

        fun isDepthOverflowed(path: String): Boolean {
            return calculateDepth(path) > MAX_DEPTH
        }

        fun calculateDepth(path: String): Int {
            return path.length / DEPTH_CHUNK_SIZE
        }

        fun isChunkOverflowed(lastChunk: String): Boolean {
            return lastChunk == MAX_CHUNK
        }

        fun next(path: String): String {

            if (path.isEmpty()) {
                return MIN_CHUNK
            }

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

    }
}
