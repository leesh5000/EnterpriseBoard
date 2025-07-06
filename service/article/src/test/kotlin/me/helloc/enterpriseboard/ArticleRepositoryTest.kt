package me.helloc.enterpriseboard

import me.helloc.enterpriseboard.application.port.out.ArticleRepository
import me.helloc.enterpriseboard.domain.model.Article
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ArticleRepositoryTest {

    @Autowired
    lateinit var articleRepository: ArticleRepository
    // 로거
    private val log = getLogger(ArticleRepositoryTest::class.java)

    // findAllByBoardId 테스트
    @Test
    fun findAllTest() {
        val findAll: List<Article> = articleRepository.findAll(1L, 1499970L, 30L)
        log.info("findAll: $findAll")
        for (article in findAll) {
            log.info("Article: $article")
        }
    }

    @Test
    fun countByBoardIdTest() {
        val count = articleRepository.countByBoardId(1L, 10000L)
        log.info("Count of articles in board 1: $count")
    }
}
