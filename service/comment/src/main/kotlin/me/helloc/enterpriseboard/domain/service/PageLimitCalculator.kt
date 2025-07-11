package me.helloc.enterpriseboard.domain.service

/**
 * 페이지네이션을 위한 제한값 계산 도메인 서비스
 */
object PageLimitCalculator {
    
    /**
     * 페이지 정보를 기반으로 조회할 최대 레코드 수를 계산합니다.
     * 
     * @param page 현재 페이지 번호 (1부터 시작)
     * @param pageSize 페이지당 항목 수
     * @param movablePageCount 이동 가능한 페이지 수
     * @return 조회할 최대 레코드 수
     */
    fun calculate(
        page: Long,
        pageSize: Long,
        movablePageCount: Long
    ): Long {
        return page * pageSize + movablePageCount * pageSize
    }
}