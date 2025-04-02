package com.bookstore

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BookstoreApplication // Class declaration is still needed for Spring Boot

// Top-level main function is idiomatic Kotlin
fun main(args: Array<String>) {
    runApplication<BookstoreApplication>(*args) // Use reified type and spread operator
} 