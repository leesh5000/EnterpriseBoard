package me.helloc.enterpriseboard

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CommentApplication

fun main(args: Array<String>) {
    runApplication<CommentApplication>(*args)
}
