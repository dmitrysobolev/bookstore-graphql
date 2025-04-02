package com.bookstore.controller

import com.bookstore.model.Author
import com.bookstore.model.Book
import com.bookstore.repository.AuthorRepository
import com.bookstore.repository.BookRepository
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

@Controller
class BookstoreController(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository
) {

    @QueryMapping
    fun books(): List<Book> = bookRepository.findAll()

    @QueryMapping
    fun book(@Argument id: UUID): Book? = bookRepository.findById(id).orElse(null)

    @QueryMapping
    fun authors(@Argument name: String?): List<Author> {
        return if (!name.isNullOrBlank()) {
            authorRepository.findByNameContainingIgnoreCase(name)
        } else {
            authorRepository.findAll()
        }
    }

    @QueryMapping
    fun author(@Argument id: UUID): Author? = authorRepository.findById(id).orElse(null)

    // @SchemaMapping for resolving Book.authors field (handles lazy loading if needed)
    // Consider if FetchType.EAGER on Book.authors makes this less critical, but good practice.
    @SchemaMapping(typeName = "Book", field = "authors")
    fun getAuthors(book: Book): Set<Author> {
        // Re-fetch to ensure authors are loaded if potentially lazy
        // Use requireNotNull to assert book.id is not null, or handle appropriately
        val bookId = requireNotNull(book.id) { "Book must have an ID to fetch authors" }
        val fetchedBook = bookRepository.findById(bookId)
            .orElseThrow { RuntimeException("Book not found with id: $bookId") } 
        // Return MutableSet from entity
        return fetchedBook.authors
    }

    @MutationMapping
    @Transactional
    fun createAuthor(@Argument name: String, @Argument biography: String?): Author {
        val author = Author().apply {
            this.name = name
            this.biography = biography
        }
        return authorRepository.save(author)
    }

    @MutationMapping
    @Transactional
    fun updateAuthor(@Argument id: UUID, @Argument name: String?, @Argument biography: String?): Author {
        val author = authorRepository.findById(id)
            .orElseThrow { RuntimeException("Author not found with id: $id") }

        name?.let { author.name = it }
        author.biography = biography 

        return authorRepository.save(author)
    }

    @MutationMapping
    @Transactional
    fun deleteAuthor(@Argument id: UUID): Boolean {
        return if (authorRepository.existsById(id)) {
            authorRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    @MutationMapping
    @Transactional
    fun createBook(
        @Argument title: String,
        @Argument authorIds: List<UUID>,
        @Argument isbn: String?,
        @Argument price: Float?,
        @Argument quantity: Int?
    ): Book {
        val authors = authorIds.map { authorId ->
            authorRepository.findById(authorId)
                .orElseThrow { RuntimeException("Author not found with id: $authorId") }
        }.toMutableSet()

        val book = Book().apply {
            this.title = title
            this.authors = authors
            this.isbn = isbn
            this.price = price?.toBigDecimal() ?: BigDecimal.ZERO
            this.quantity = quantity
        }
        return bookRepository.save(book)
    }

    @MutationMapping
    @Transactional
    fun updateBook(
        @Argument id: UUID,
        @Argument title: String?,
        @Argument authorIds: List<UUID>?,
        @Argument isbn: String?,
        @Argument price: Float?,
        @Argument quantity: Int?
    ): Book {
        val book = bookRepository.findById(id)
            .orElseThrow { RuntimeException("Book not found with id: $id") }

        title?.let { book.title = it }
        isbn?.let { book.isbn = it }
        price?.let { book.price = it.toBigDecimal() }
        quantity?.let { book.quantity = it }

        authorIds?.let { ids ->
            book.authors = ids.map { authorId ->
                authorRepository.findById(authorId)
                    .orElseThrow { RuntimeException("Author not found with id: $authorId") }
            }.toMutableSet()
        }

        return bookRepository.save(book)
    }

    @MutationMapping
    @Transactional
    fun deleteBook(@Argument id: UUID): Boolean {
        return if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id)
            true
        } else {
            false
        }
    }
} 