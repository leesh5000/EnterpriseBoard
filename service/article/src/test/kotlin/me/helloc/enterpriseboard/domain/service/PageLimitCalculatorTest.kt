package me.helloc.enterpriseboard.domain.service

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class PageLimitCalculatorTest : StringSpec({

    "첫 번째 페이지의 limit을 계산할 수 있어야 한다" {
        // Given
        val page = 1L
        val pageSize = 10L
        val movablePageCount = 5L

        // When
        val limit = PageLimitCalculator.calculate(page, pageSize, movablePageCount)

        // Then
        // ((1-1) / 5 + 1) * 10 * 5 + 1 = (0/5 + 1) * 50 + 1 = 1 * 50 + 1 = 51
        limit shouldBe 51L
    }

    "movablePageCount 범위 내의 중간 페이지 limit을 계산할 수 있어야 한다" {
        // Given
        val page = 3L
        val pageSize = 10L
        val movablePageCount = 5L

        // When
        val limit = PageLimitCalculator.calculate(page, pageSize, movablePageCount)

        // Then
        // ((3-1) / 5 + 1) * 10 * 5 + 1 = (2/5 + 1) * 50 + 1 = (0 + 1) * 50 + 1 = 51
        limit shouldBe 51L
    }

    "movablePageCount 경계값에서의 limit을 계산할 수 있어야 한다" {
        // Given
        val page = 5L
        val pageSize = 10L
        val movablePageCount = 5L

        // When
        val limit = PageLimitCalculator.calculate(page, pageSize, movablePageCount)

        // Then
        // ((5-1) / 5 + 1) * 10 * 5 + 1 = (4/5 + 1) * 50 + 1 = (0 + 1) * 50 + 1 = 51
        limit shouldBe 51L
    }

    "movablePageCount를 넘어서는 페이지의 limit을 계산할 수 있어야 한다" {
        // Given
        val page = 6L
        val pageSize = 10L
        val movablePageCount = 5L

        // When
        val limit = PageLimitCalculator.calculate(page, pageSize, movablePageCount)

        // Then
        // ((6-1) / 5 + 1) * 10 * 5 + 1 = (5/5 + 1) * 50 + 1 = (1 + 1) * 50 + 1 = 101
        limit shouldBe 101L
    }

    "두 번째 movablePageCount 블록의 중간 페이지 limit을 계산할 수 있어야 한다" {
        // Given
        val page = 8L
        val pageSize = 10L
        val movablePageCount = 5L

        // When
        val limit = PageLimitCalculator.calculate(page, pageSize, movablePageCount)

        // Then
        // ((8-1) / 5 + 1) * 10 * 5 + 1 = (7/5 + 1) * 50 + 1 = (1 + 1) * 50 + 1 = 101
        limit shouldBe 101L
    }

    "두 번째 movablePageCount 블록의 마지막 페이지 limit을 계산할 수 있어야 한다" {
        // Given
        val page = 10L
        val pageSize = 10L
        val movablePageCount = 5L

        // When
        val limit = PageLimitCalculator.calculate(page, pageSize, movablePageCount)

        // Then
        // ((10-1) / 5 + 1) * 10 * 5 + 1 = (9/5 + 1) * 50 + 1 = (1 + 1) * 50 + 1 = 101
        limit shouldBe 101L
    }

    "세 번째 movablePageCount 블록의 첫 번째 페이지 limit을 계산할 수 있어야 한다" {
        // Given
        val page = 11L
        val pageSize = 10L
        val movablePageCount = 5L

        // When
        val limit = PageLimitCalculator.calculate(page, pageSize, movablePageCount)

        // Then
        // ((11-1) / 5 + 1) * 10 * 5 + 1 = (10/5 + 1) * 50 + 1 = (2 + 1) * 50 + 1 = 151
        limit shouldBe 151L
    }

    "다른 pageSize와 movablePageCount 조합으로 계산할 수 있어야 한다" {
        // Given
        val page = 7L
        val pageSize = 20L
        val movablePageCount = 3L

        // When
        val limit = PageLimitCalculator.calculate(page, pageSize, movablePageCount)

        // Then
        // ((7-1) / 3 + 1) * 20 * 3 + 1 = (6/3 + 1) * 60 + 1 = (2 + 1) * 60 + 1 = 181
        limit shouldBe 181L
    }

    "pageSize가 1일 때의 계산을 할 수 있어야 한다" {
        // Given
        val page = 3L
        val pageSize = 1L
        val movablePageCount = 10L

        // When
        val limit = PageLimitCalculator.calculate(page, pageSize, movablePageCount)

        // Then
        // ((3-1) / 10 + 1) * 1 * 10 + 1 = (2/10 + 1) * 10 + 1 = (0 + 1) * 10 + 1 = 11
        limit shouldBe 11L
    }

    "movablePageCount가 1일 때의 계산을 할 수 있어야 한다" {
        // Given
        val page = 5L
        val pageSize = 10L
        val movablePageCount = 1L

        // When
        val limit = PageLimitCalculator.calculate(page, pageSize, movablePageCount)

        // Then
        // ((5-1) / 1 + 1) * 10 * 1 + 1 = (4/1 + 1) * 10 + 1 = (4 + 1) * 10 + 1 = 51
        limit shouldBe 51L
    }

    "큰 숫자로도 계산할 수 있어야 한다" {
        // Given
        val page = 1000L
        val pageSize = 50L
        val movablePageCount = 20L

        // When
        val limit = PageLimitCalculator.calculate(page, pageSize, movablePageCount)

        // Then
        // ((1000-1) / 20 + 1) * 50 * 20 + 1 = (999/20 + 1) * 1000 + 1 = (49 + 1) * 1000 + 1 = 50001
        limit shouldBe 50001L
    }
})