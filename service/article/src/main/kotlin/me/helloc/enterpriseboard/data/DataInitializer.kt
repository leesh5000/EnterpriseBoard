package me.helloc.enterpriseboard.data

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import me.helloc.common.snowflake.Snowflake
import me.helloc.enterpriseboard.adapter.out.persistence.ArticleJpaEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionTemplate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.system.exitProcess

@Component
class DataInitializer {
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var transactionTemplate: TransactionTemplate

    private val snowflake = Snowflake()
    private val completedBatches = AtomicInteger(0)
    private val totalInsertedRecords = AtomicLong(0)
    private val startTime = System.currentTimeMillis()
    private val random = Random.Default

    /**
     * 1200만 건의 게시글 데이터 초기화 (main 함수로 실행 가능)
     */
    @Throws(InterruptedException::class)
    fun initialize() {
        println("=".repeat(60))
        println("🚀 대용량 게시글 데이터 초기화 시작")
        println("=".repeat(60))
        println("📊 초기화 정보:")
        println("   - 총 데이터 수: ${String.format("%,d", TOTAL_ARTICLES)}개")
        println("   - 배치 크기: ${String.format("%,d", BATCH_SIZE)}개")
        println("   - 총 배치 수: ${String.format("%,d", TOTAL_BATCHES)}개")
        println("   - 스레드 풀 크기: ${THREAD_POOL_SIZE}개")
        println("   - 시작 시간: ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}")
        println("=".repeat(60))

        val latch = CountDownLatch(TOTAL_BATCHES)
        val executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE)

        // 배치 작업 실행
        for (batchIndex in 0 until TOTAL_BATCHES) {
            executorService.submit {
                try {
                    insertBatch(batchIndex)
                    latch.countDown()
                    updateProgress()
                } catch (e: Exception) {
                    println("❌ 배치 $batchIndex 실행 실패: ${e.message}")
                    e.printStackTrace()
                    latch.countDown()
                }
            }
        }

        latch.await()
        executorService.shutdown()

        printCompletionStats()
    }

    /**
     * 배치 단위로 데이터 삽입
     */
    private fun insertBatch(batchIndex: Int) {
        transactionTemplate.executeWithoutResult { _ ->
            val startOffset = batchIndex * BATCH_SIZE

            for (i in 0 until BATCH_SIZE) {
                val recordIndex = startOffset + i
                val article = createUniqueArticle(recordIndex)

                val jpaEntity = ArticleJpaEntity(
                    articleId = article.first,
                    title = article.second,
                    content = article.third,
                    boardId = BOARD_ID,
                    writerId = generateWriterId(recordIndex),
                    createdAt = LocalDateTime.now(),
                    modifiedAt = LocalDateTime.now()
                )
                entityManager.persist(jpaEntity)

                // 주기적으로 flush/clear하여 메모리 관리
                if (i % FLUSH_INTERVAL == 0 && i > 0) {
                    entityManager.flush()
                    entityManager.clear()
                }
            }

            // 배치 완료 시 flush/clear
            entityManager.flush()
            entityManager.clear()
        }

        totalInsertedRecords.addAndGet(BATCH_SIZE.toLong())
    }

    /**
     * 고유한 게시글 데이터 생성
     */
    private fun createUniqueArticle(index: Int): Triple<Long, String, String> {
        val articleId = snowflake.nextId()
        val uniqueId = UUID.randomUUID().toString().substring(0, 8)
        val timestamp = System.currentTimeMillis()

        val title = "게시글 제목 ${String.format("%07d", index)} - $uniqueId"
        val content = buildString {
            append("게시글 내용 ${String.format("%07d", index)} - $timestamp\n")
            append("고유 식별자: $uniqueId\n")
            append("생성 시각: ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))}\n")
            append("랜덤 데이터: ${generateRandomContent()}\n")
            append("해시값: ${(title + timestamp).hashCode()}\n")
        }

        return Triple(articleId, title, content)
    }

    /**
     * 랜덤 콘텐츠 생성
     */
    private fun generateRandomContent(): String {
        val words = listOf(
            "데이터", "시스템", "성능", "최적화", "아키텍처", "서비스", "플랫폼", "인프라",
            "확장성", "안정성", "모니터링", "로그", "메트릭", "알림", "백업", "복구",
            "보안", "인증", "권한", "암호화", "네트워크", "데이터베이스", "캐시", "큐"
        )

        return (1..5).joinToString(" ") { words.random(random) }
    }

    /**
     * 작성자 ID 생성 (1-1000 범위)
     */
    private fun generateWriterId(index: Int): Long {
        return (index % 1000) + 1L
    }

    /**
     * 진행률 업데이트 및 출력
     */
    private fun updateProgress() {
        val completed = completedBatches.incrementAndGet()
        val progressPercent = (completed * 100.0 / TOTAL_BATCHES).roundToInt()

        // 5% 단위로 진행률 출력
        if (progressPercent % 5 == 0 && progressPercent > 0) {
            val currentTime = System.currentTimeMillis()
            val elapsedTime = currentTime - startTime
            val remainingTime = if (completed > 0) {
                ((elapsedTime.toDouble() / completed) * (TOTAL_BATCHES - completed)).toLong()
            } else 0L

            val currentThroughput = if (elapsedTime > 0) {
                (totalInsertedRecords.get() * 1000.0 / elapsedTime).roundToInt()
            } else 0

            synchronized(this) {
                val lastProgress = ((completed - 1) * 100.0 / TOTAL_BATCHES).roundToInt()
                if (lastProgress / 5 != progressPercent / 5) {
                    println("📈 진행률: ${progressPercent}% | " +
                            "완료: ${String.format("%,d", completed)}/${String.format("%,d", TOTAL_BATCHES)} 배치 | " +
                            "처리량: ${String.format("%,d", currentThroughput)}개/초 | " +
                            "소요: ${formatTime(elapsedTime)} | " +
                            "예상 남은 시간: ${formatTime(remainingTime)}")
                }
            }
        }
    }

    /**
     * 완료 통계 출력
     */
    private fun printCompletionStats() {
        val totalTime = System.currentTimeMillis() - startTime
        val avgThroughput = if (totalTime > 0) {
            (totalInsertedRecords.get() * 1000.0 / totalTime).roundToInt()
        } else 0

        println("\n" + "=".repeat(60))
        println("✅ 데이터 초기화 완료!")
        println("=".repeat(60))
        println("📊 완료 통계:")
        println("   - 총 처리 데이터: ${String.format("%,d", totalInsertedRecords.get())}개")
        println("   - 총 소요 시간: ${formatTime(totalTime)}")
        println("   - 평균 처리량: ${String.format("%,d", avgThroughput)}개/초")
        println("   - 완료 시간: ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}")
        println("=".repeat(60))
    }

    /**
     * 시간 포매팅 (밀리초 -> 시:분:초)
     */
    private fun formatTime(timeMillis: Long): String {
        val seconds = timeMillis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60

        return when {
            hours > 0 -> "${hours}시간 ${minutes % 60}분 ${seconds % 60}초"
            minutes > 0 -> "${minutes}분 ${seconds % 60}초"
            else -> "${seconds}초"
        }
    }

    companion object {
        const val TOTAL_ARTICLES = 12_000_000        // 1200만 건
        const val BATCH_SIZE = 5_000                 // 배치당 5000개 (기존 2000 -> 5000)
        const val TOTAL_BATCHES = TOTAL_ARTICLES / BATCH_SIZE // 2400개 배치
        const val THREAD_POOL_SIZE = 20              // 20개 스레드 (기존 10 -> 20)
        const val FLUSH_INTERVAL = 1000              // 1000개마다 flush/clear
        const val BOARD_ID = 1L                      // 모든 게시글 동일한 boardId

        @JvmStatic
        fun main(args: Array<String>) {
            // Spring Boot 애플리케이션으로 실행
            val context = org.springframework.boot.SpringApplication.run(
                me.helloc.enterpriseboard.ArticleApplication::class.java,
                *args
            )

            try {
                val dataInitializer = context.getBean(DataInitializer::class.java)
                dataInitializer.initialize()
                println("✅ 데이터 초기화가 성공적으로 완료되었습니다!")
            } catch (e: Exception) {
                println("❌ 데이터 초기화 중 오류 발생: ${e.message}")
                e.printStackTrace()
            } finally {
                context.close()
                exitProcess(0)
            }
        }
    }
}
