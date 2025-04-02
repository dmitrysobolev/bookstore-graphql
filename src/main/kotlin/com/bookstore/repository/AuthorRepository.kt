package com.bookstore.repository

import com.bookstore.model.Author
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AuthorRepository : JpaRepository<Author, UUID> {
    fun findByNameContainingIgnoreCase(name: String): List<Author>
} 