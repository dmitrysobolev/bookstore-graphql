package com.bookstore.dto

import org.springframework.data.domain.Page as SpringPage

/**
 * Generic class representing a paginated result for GraphQL responses.
 * This maps Spring's Page to our GraphQL schema's page types.
 */
data class Page<T>(
    val content: List<T>,
    val totalElements: Int,
    val totalPages: Int,
    val number: Int,
    val size: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
) {
    companion object {
        /**
         * Creates a Page from a Spring Page.
         */
        fun <T, R> from(page: SpringPage<T>, transform: (T) -> R): Page<R> {
            return Page(
                content = page.content.map(transform),
                totalElements = page.totalElements.toInt(),
                totalPages = page.totalPages,
                number = page.number,
                size = page.size,
                hasNext = page.hasNext(),
                hasPrevious = page.hasPrevious()
            )
        }

        /**
         * Creates a Page from a Spring Page without transformation.
         */
        fun <T> from(page: SpringPage<T>): Page<T> {
            return from(page) { it }
        }
    }
}

// Type aliases for specific page types used in GraphQL
typealias BookPage = Page<com.bookstore.model.Book>
typealias AuthorPage = Page<com.bookstore.model.Author>