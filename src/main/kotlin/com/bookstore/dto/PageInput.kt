package com.bookstore.dto

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

/**
 * Data class representing pagination parameters from GraphQL queries.
 */
data class PageInput(
    val page: Int = 0,
    val size: Int = 10,
    val sort: String? = null,
    val direction: SortDirection = SortDirection.ASC
) {
    /**
     * Converts this PageInput to a Spring PageRequest.
     */
    fun toPageRequest(): PageRequest {
        return if (sort != null) {
            val sortDirection = if (direction == SortDirection.ASC) Sort.Direction.ASC else Sort.Direction.DESC
            PageRequest.of(page, size, Sort.by(sortDirection, sort))
        } else {
            PageRequest.of(page, size)
        }
    }
}

/**
 * Enum representing sort direction options.
 */
enum class SortDirection {
    ASC, DESC
}