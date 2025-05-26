package com.bookstore.repository

import com.bookstore.model.Book
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface BookRepository : JpaRepository<Book, UUID> {
    // Add method to find books by title (with pagination)
    fun findByTitleContainingIgnoreCase(title: String, pageable: Pageable): Page<Book>

    // Paginated version of findAll (already provided by PagingAndSortingRepository)
    // override for clarity
    override fun findAll(pageable: Pageable): Page<Book>
}
