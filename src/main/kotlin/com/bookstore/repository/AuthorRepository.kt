package com.bookstore.repository

import com.bookstore.model.Author
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository // Keep Spring annotation
interface AuthorRepository : JpaRepository<Author, UUID> {
    // Spring Data JPA generates implementation from method name
    // Parameter type can be non-nullable String if the name argument is always expected
    fun findByNameContainingIgnoreCase(name: String): List<Author>
} 