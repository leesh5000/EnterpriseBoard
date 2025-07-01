package me.helloc.common.test

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import org.junit.jupiter.api.Test

/**
 * 헥사고날 아키텍처 규칙 검증을 위한 헬퍼 클래스
 */
class ArchitectureTestHelper {
    
    companion object {
        /**
         * 특정 패키지의 클래스들을 검증
         */
        fun verifyHexagonalArchitecture(basePackage: String) {
            val importedClasses = ClassFileImporter().importPackages(basePackage)
            
            // 레이어 아키텍처 규칙 검증
            verifyLayerDependencies(importedClasses, basePackage)
            
            // 도메인 레이어 규칙 검증
            verifyDomainLayer(importedClasses, basePackage)
            
            // 애플리케이션 레이어 규칙 검증
            verifyApplicationLayer(importedClasses, basePackage)
            
            // 인프라스트럭처 레이어 규칙 검증
            verifyInfrastructureLayer(importedClasses, basePackage)
        }
        
        private fun verifyLayerDependencies(classes: JavaClasses, basePackage: String) {
            layeredArchitecture()
                .layer("Domain").definedBy("$basePackage.domain..")
                .layer("Application").definedBy("$basePackage.application..")
                .layer("Infrastructure").definedBy("$basePackage.infrastructure..")
                .layer("Interfaces").definedBy("$basePackage.interfaces..")
                
                .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure", "Interfaces")
                .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure", "Interfaces")
                .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
                .whereLayer("Interfaces").mayNotBeAccessedByAnyLayer()
                
                .check(classes)
        }
        
        private fun verifyDomainLayer(classes: JavaClasses, basePackage: String) {
            // 도메인 모델은 Spring 어노테이션을 사용하지 않아야 함
            noClasses()
                .that().resideInAPackage("$basePackage.domain.model..")
                .should().beAnnotatedWith("org.springframework.stereotype.Component")
                .orShould().beAnnotatedWith("org.springframework.stereotype.Service")
                .orShould().beAnnotatedWith("org.springframework.stereotype.Repository")
                .check(classes)
            
            // 도메인 모델은 JPA 어노테이션을 사용하지 않아야 함
            noClasses()
                .that().resideInAPackage("$basePackage.domain.model..")
                .should().beAnnotatedWith("jakarta.persistence.Entity")
                .orShould().beAnnotatedWith("jakarta.persistence.Table")
                .check(classes)
            
            // 도메인 서비스는 @Component 어노테이션을 가져야 함
            classes()
                .that().resideInAPackage("$basePackage.domain.service..")
                .should().beAnnotatedWith("org.springframework.stereotype.Component")
                .check(classes)
        }
        
        private fun verifyApplicationLayer(classes: JavaClasses, basePackage: String) {
            // 애플리케이션 서비스는 @Service 어노테이션을 가져야 함
            classes()
                .that().resideInAPackage("$basePackage.application.service..")
                .should().beAnnotatedWith("org.springframework.stereotype.Service")
                .check(classes)
            
            // 애플리케이션 서비스는 @Transactional 어노테이션을 가져야 함
            classes()
                .that().resideInAPackage("$basePackage.application.service..")
                .should().beAnnotatedWith("org.springframework.transaction.annotation.Transactional")
                .check(classes)
        }
        
        private fun verifyInfrastructureLayer(classes: JavaClasses, basePackage: String) {
            // JPA 리포지토리는 infrastructure 패키지에 있어야 함
            classes()
                .that().areAnnotatedWith("jakarta.persistence.Entity")
                .should().resideInAPackage("$basePackage.infrastructure..")
                .check(classes)
            
            // 어댑터는 @Component 또는 @Repository 어노테이션을 가져야 함
            classes()
                .that().resideInAPackage("$basePackage.infrastructure.adapter..")
                .should().beAnnotatedWith("org.springframework.stereotype.Component")
                .orShould().beAnnotatedWith("org.springframework.stereotype.Repository")
                .check(classes)
        }
    }
}

/**
 * 샘플 아키텍처 테스트
 */
@ArchitectureTest
class SampleArchitectureTest {
    
    @Test
    fun `Article 서비스는 헥사고날 아키텍처 규칙을 준수해야 한다`() {
        ArchitectureTestHelper.verifyHexagonalArchitecture("me.helloc.enterpriseboard.article")
    }
    
    @Test
    fun `Comment 서비스는 헥사고날 아키텍처 규칙을 준수해야 한다`() {
        ArchitectureTestHelper.verifyHexagonalArchitecture("me.helloc.enterpriseboard.comment")
    }
}