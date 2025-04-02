package com.bookstore.repository

import com.bookstore.model.Book
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository // Add @Repository for consistency, though not strictly required for interfaces
interface BookRepository : JpaRepository<Book, UUID> {
    // Add custom query methods here if needed in the future
} 