package me.helloc.enterpriseboard.domain.service

/**
 * 페이지네이션 제한값 계산 유틸리티
 *
 * Java의 @NoArgsConstructor(access = AccessLevel.PRIVATE)와 동일한 효과를 제공하는
 * Kotlin object 선언을 사용합니다.
 */
object PageLimitCalculator {

    /**
     * 페이지 제한값을 계산합니다.
     *
     * @param page 현재 페이지 번호
     * @param pageSize 페이지 크기
     * @param movablePageCount 이동 가능한 페이지 수
     * @return 계산된 페이지 제한값
     */
    fun calculate(page: Long, pageSize: Long, movablePageCount: Long): Long {
        return (((page - 1) / movablePageCount) + 1) * pageSize * movablePageCount + 1
    }
}
