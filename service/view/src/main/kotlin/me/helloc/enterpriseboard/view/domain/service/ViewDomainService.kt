package me.helloc.enterpriseboard.view.domain.service

import me.helloc.enterpriseboard.view.domain.model.*
import me.helloc.enterpriseboard.view.domain.repository.ViewRepository
import org.springframework.stereotype.Component

/**
 * 조회수 도메인 서비스
 */
@Component
class ViewDomainService(
    private val viewRepository: ViewRepository
) {
    
    /**
     * 중복 조회 방지 - 같은 IP에서 1시간 내 재조회 방지
     */
    fun shouldIncreaseViewCount(articleId: ArticleId, ipAddress: IpAddress, userId: UserId?): Boolean {
        // IP 기반 중복 체크 (1시간)
        if (viewRepository.existsByArticleIdAndIpAddressWithinHours(articleId, ipAddress, 1)) {
            return false
        }
        
        // 사용자 기반 중복 체크 (로그인한 경우, 1시간)
        if (userId != null && viewRepository.existsByArticleIdAndUserIdWithinHours(articleId, userId, 1)) {
            return false
        }
        
        return true
    }
    
    /**
     * 조회수 증가 가능 여부 확인
     */
    fun canIncreaseViewCount(articleId: ArticleId, userId: UserId?, ipAddress: IpAddress): Boolean {
        return shouldIncreaseViewCount(articleId, ipAddress, userId)
    }
}