package me.helloc.enterpriseboard.article.infrastructure.adapter

import me.helloc.common.snowflake.Snowflake
import me.helloc.enterpriseboard.article.application.port.output.IdGenerator
import org.springframework.stereotype.Component

/**
 * ID 생성 어댑터 - 외부 라이브러리를 도메인에서 격리
 */
@Component
class IdGeneratorAdapter : IdGenerator {
    
    private val snowflake = Snowflake()
    
    override fun generateId(): Long {
        return snowflake.nextId()
    }
}