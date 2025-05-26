package com.bookstore.repository

import com.bookstore.model.Author
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AuthorRepository : JpaRepository<Author, UUID> {
    fun findByNameContainingIgnoreCase(name: String): List<Author>

    // Paginated version of findByNameContainingIgnoreCase
    fun findByNameContainingIgnoreCase(name: String, pageable: Pageable): Page<Author>

    // Paginated version of findAll (already provided by PagingAndSortingRepository)
    // override for clarity
    override fun findAll(pageable: Pageable): Page<Author>
}
