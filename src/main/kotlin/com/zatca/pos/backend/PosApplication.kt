package com.zatca.pos.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PosApplication

fun main(args: Array<String>) {
    runApplication<PosApplication>(*args)
}