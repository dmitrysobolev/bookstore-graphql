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
import org.springframework.transaction.annotation.Transactional // For potentially complex operations
import java.math.BigDecimal
import java.util.UUID

@Controller // Keep Spring annotation
class BookstoreController(
    // Use constructor injection (final fields in Java -> non-nullable vals in Kotlin)
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository
) {

    @QueryMapping
    fun books(): List<Book> = bookRepository.findAll()

    @QueryMapping
    fun book(@Argument id: UUID): Book? = bookRepository.findById(id).orElse(null)

    @QueryMapping
    fun authors(@Argument name: String?): List<Author> { // name can be null
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
    @Transactional // Good practice for operations involving multiple saves/reads
    fun createAuthor(@Argument name: String, @Argument biography: String?): Author {
        // Use apply scope function for concise object creation and setup
        val author = Author().apply {
            this.name = name
            this.biography = biography // Assign directly, can be null
        }
        return authorRepository.save(author)
    }

    @MutationMapping
    @Transactional
    fun updateAuthor(@Argument id: UUID, @Argument name: String?, @Argument biography: String?): Author {
        val author = authorRepository.findById(id)
            .orElseThrow { RuntimeException("Author not found with id: $id") }

        // Use scope functions for conditional updates
        name?.let { author.name = it }
        // Biography can be explicitly set to null or a new value
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
        // Find authors, throw if any not found
        val authors = authorIds.map { authorId ->
            authorRepository.findById(authorId)
                .orElseThrow { RuntimeException("Author not found with id: $authorId") }
        }.toMutableSet()

        val book = Book().apply {
            this.title = title
            this.authors = authors
            this.isbn = isbn
            // Convert Float to BigDecimal safely
            this.price = price?.toBigDecimal() ?: BigDecimal.ZERO // Provide default or handle null price
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

        // Use let for concise nullable updates
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