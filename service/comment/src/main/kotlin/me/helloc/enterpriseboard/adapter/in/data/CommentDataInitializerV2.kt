package me.helloc.enterpriseboard.adapter.`in`.data

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import me.helloc.common.snowflake.Snowflake
import me.helloc.enterpriseboard.CommentApplication
import me.helloc.enterpriseboard.adapter.out.persistence.CommentJpaEntityV2
import me.helloc.enterpriseboard.domain.model.CommentPath
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionTemplate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.system.exitProcess

@Component
class CommentDataInitializerV2 {
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var transactionTemplate: TransactionTemplate

    private val snowflake = Snowflake()
    private val completedBatches = AtomicInteger(0)
    private val totalInsertedRecords = AtomicLong(0)
    private val startTime = System.currentTimeMillis()
    private val random = Random.Default
    private val rootCommentPaths = ConcurrentHashMap<Int, String>()

    @Throws(InterruptedException::class)
    fun initialize() {
        println("=".repeat(60))
        println("🚀 대용량 댓글 V2 데이터 초기화 시작")
        println("=".repeat(60))
        println("📊 초기화 정보:")
        println("   - 총 데이터 수: ${String.format("%,d", TOTAL_COMMENTS)}개")
        println("   - 루트 댓글 수: ${String.format("%,d", ROOT_COMMENTS)}개")
        println("   - 답글 수: ${String.format("%,d", REPLY_COMMENTS)}개")
        println("   - 배치 크기: ${String.format("%,d", BATCH_SIZE)}개")
        println("   - 총 배치 수: ${String.format("%,d", TOTAL_BATCHES)}개")
        println("   - 스레드 풀 크기: ${THREAD_POOL_SIZE}개")
        println("   - 시작 시간: ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}")
        println("=".repeat(60))

        val latch = CountDownLatch(TOTAL_BATCHES)
        val executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE)

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

    private fun insertBatch(batchIndex: Int) {
        transactionTemplate.executeWithoutResult { _ ->
            val startOffset = batchIndex * BATCH_SIZE

            for (i in 0 until BATCH_SIZE) {
                val recordIndex = startOffset + i
                val comment = if (recordIndex < ROOT_COMMENTS) {
                    createRootComment(recordIndex)
                } else {
                    createReplyComment(recordIndex)
                }

                val jpaEntity = CommentJpaEntityV2(
                    commentId = comment.first,
                    content = comment.second,
                    articleId = ARTICLE_ID,
                    writerId = generateWriterId(recordIndex),
                    path = comment.third,
                    deleted = false,
                    createdAt = LocalDateTime.now()
                )
                entityManager.persist(jpaEntity)

                if (recordIndex < ROOT_COMMENTS) {
                    rootCommentPaths[recordIndex] = comment.third
                }

                if (i % FLUSH_INTERVAL == 0 && i > 0) {
                    entityManager.flush()
                    entityManager.clear()
                }
            }

            entityManager.flush()
            entityManager.clear()
        }

        totalInsertedRecords.addAndGet(BATCH_SIZE.toLong())
    }

    private fun createRootComment(index: Int): Triple<Long, String, String> {
        val commentId = snowflake.nextId()
        val content = generateCommentContent("루트 댓글", index)
        
        // Snowflake ID를 62진수로 변환하여 고유한 5자 depth 1 path 생성
        val uniquePath = convertToBase62(commentId, 5)
        
        return Triple(commentId, content, uniquePath)
    }
    
    private fun convertToBase62(value: Long, length: Int): String {
        val charset = CommentPath.CHARSET
        val base = charset.length
        
        var num = value
        var result = ""
        
        repeat(length) {
            result = charset[(num % base).toInt()] + result
            num /= base
        }
        
        return result
    }

    private fun createReplyComment(index: Int): Triple<Long, String, String> {
        val commentId = snowflake.nextId()
        val content = generateCommentContent("답글", index)
        
        val rootIndex = (index - ROOT_COMMENTS) % ROOT_COMMENTS
        val parentPath = rootCommentPaths[rootIndex] ?: throw IllegalStateException("루트 댓글 Path를 찾을 수 없습니다: $rootIndex")
        
        val parentCommentPath = CommentPath(parentPath)
        val childPath = parentCommentPath.createChildPath("")
        
        return Triple(commentId, content, childPath.path)
    }

    private fun generateCommentContent(type: String, index: Int): String {
        val uniqueId = UUID.randomUUID().toString().substring(0, 8)
        val randomContent = generateRandomContent()
        return "$type ${String.format("%07d", index)} - $uniqueId $randomContent"
    }

    private fun generateRandomContent(): String {
        val words = listOf(
            "의견", "생각", "댓글", "답변", "질문", "정보", "내용", "설명",
            "참고", "추가", "보완", "수정", "확인", "검토", "피드백", "제안"
        )

        return (1..3).joinToString(" ") { words.random(random) }
    }

    private fun generateWriterId(index: Int): Long {
        return (index % 1000) + 1L
    }

    private fun updateProgress() {
        val completed = completedBatches.incrementAndGet()
        val progressPercent = (completed * 100.0 / TOTAL_BATCHES).roundToInt()

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

    private fun printCompletionStats() {
        val totalTime = System.currentTimeMillis() - startTime
        val avgThroughput = if (totalTime > 0) {
            (totalInsertedRecords.get() * 1000.0 / totalTime).roundToInt()
        } else 0

        println("\n" + "=".repeat(60))
        println("✅ 댓글 V2 데이터 초기화 완료!")
        println("=".repeat(60))
        println("📊 완료 통계:")
        println("   - 총 처리 데이터: ${String.format("%,d", totalInsertedRecords.get())}개")
        println("   - 루트 댓글: ${String.format("%,d", ROOT_COMMENTS)}개")
        println("   - 답글: ${String.format("%,d", REPLY_COMMENTS)}개")
        println("   - 총 소요 시간: ${formatTime(totalTime)}")
        println("   - 평균 처리량: ${String.format("%,d", avgThroughput)}개/초")
        println("   - 완료 시간: ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}")
        println("=".repeat(60))
    }

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
        const val TOTAL_COMMENTS = 12_000_000
        const val ROOT_COMMENTS = 6_000_000
        const val REPLY_COMMENTS = 6_000_000
        const val BATCH_SIZE = 1_000
        const val TOTAL_BATCHES = TOTAL_COMMENTS / BATCH_SIZE
        const val THREAD_POOL_SIZE = 10
        const val FLUSH_INTERVAL = 500
        const val ARTICLE_ID = 1L

        @JvmStatic
        fun main(args: Array<String>) {
            val context = SpringApplication.run(
                CommentApplication::class.java,
                *args
            )

            try {
                val dataInitializer = context.getBean(CommentDataInitializerV2::class.java)
                dataInitializer.initialize()
                println("✅ 댓글 V2 데이터 초기화가 성공적으로 완료되었습니다!")
            } catch (e: Exception) {
                println("❌ 댓글 V2 데이터 초기화 중 오류 발생: ${e.message}")
                e.printStackTrace()
            } finally {
                context.close()
                exitProcess(0)
            }
        }
    }
}
