package com.bookstore.controller

import com.bookstore.dto.AuthorPage
import com.bookstore.dto.BookPage
import com.bookstore.dto.Page
import com.bookstore.dto.PageInput
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
    fun books(@Argument page: PageInput?): BookPage {
        val pageRequest = page?.toPageRequest() ?: PageInput().toPageRequest()
        val result = bookRepository.findAll(pageRequest)
        return Page.from(result)
    }

    @QueryMapping
    fun booksByTitle(@Argument title: String, @Argument page: PageInput?): BookPage {
        val pageRequest = page?.toPageRequest() ?: PageInput().toPageRequest()
        val result = bookRepository.findByTitleContainingIgnoreCase(title, pageRequest)
        return Page.from(result)
    }

    @QueryMapping
    fun book(@Argument id: UUID): Book? = bookRepository.findById(id).orElse(null)

    @QueryMapping
    fun authors(@Argument name: String?, @Argument page: PageInput?): AuthorPage {
        val pageRequest = page?.toPageRequest() ?: PageInput().toPageRequest()
        val result = if (!name.isNullOrBlank()) {
            authorRepository.findByNameContainingIgnoreCase(name, pageRequest)
        } else {
            authorRepository.findAll(pageRequest)
        }
        return Page.from(result)
    }

    @QueryMapping
    fun author(@Argument id: UUID): Author? = authorRepository.findById(id).orElse(null)

    // @SchemaMapping for resolving Book.authors field (handles lazy loading)
    // This is necessary because Book.authors uses FetchType.LAZY to prevent N+1 issues.
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

    @SchemaMapping(typeName = "Author", field = "books")
    fun getBooks(author: Author): Set<Book> {
        // Re-fetch to ensure books are loaded (since they're lazy loaded)
        val authorId = requireNotNull(author.id) { "Author must have an ID to fetch books" }
        val fetchedAuthor = authorRepository.findById(authorId)
            .orElseThrow { RuntimeException("Author not found with id: $authorId") }
        // Return MutableSet from entity
        return fetchedAuthor.books
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
