package me.helloc.enterpriseboard

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ArticleApplication

fun main(args: Array<String>) {
    runApplication<ArticleApplication>(*args)
}