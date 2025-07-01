package me.helloc.common.test

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

/**
 * 헥사고날 아키텍처 테스트를 위한 기본 어노테이션들
 */

/**
 * 도메인 레이어 테스트용 어노테이션
 * - 순수 도메인 로직만 테스트
 * - Spring Context 없이 실행
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Test
annotation class DomainTest

/**
 * 애플리케이션 서비스 테스트용 어노테이션
 * - 유스케이스 테스트
 * - Mock을 사용한 격리 테스트
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@SpringBootTest
@ActiveProfiles("test")
annotation class ApplicationServiceTest

/**
 * 어댑터 테스트용 어노테이션
 * - 인프라스트럭처 어댑터 테스트
 * - 실제 외부 시스템과의 통합 테스트
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@SpringBootTest
@ActiveProfiles("test")
annotation class AdapterTest

/**
 * 아키텍처 테스트용 어노테이션
 * - 패키지 의존성 규칙 검증
 * - 헥사고날 아키텍처 제약사항 검증
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Test
annotation class ArchitectureTest

/**
 * 통합 테스트용 어노테이션
 * - 전체 시스템 통합 테스트
 * - End-to-End 테스트
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
annotation class IntegrationTest