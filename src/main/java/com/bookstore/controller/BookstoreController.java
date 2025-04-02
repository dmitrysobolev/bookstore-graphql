package com.bookstore.controller;

import com.bookstore.model.Author;
import com.bookstore.model.Book;
import com.bookstore.repository.AuthorRepository;
import com.bookstore.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class BookstoreController {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    @QueryMapping
    public List<Book> books() {
        return bookRepository.findAll();
    }

    @QueryMapping
    public Book book(@Argument UUID id) {
        return bookRepository.findById(id).orElse(null);
    }

    @QueryMapping
    public List<Author> authors() {
        return authorRepository.findAll();
    }

    @QueryMapping
    public Author author(@Argument UUID id) {
        return authorRepository.findById(id).orElse(null);
    }

    @MutationMapping
    public Author createAuthor(@Argument String name, @Argument String biography) {
        Author author = new Author();
        author.setName(name);
        author.setBiography(biography);
        return authorRepository.save(author);
    }

    @MutationMapping
    public Author updateAuthor(@Argument UUID id, @Argument String name, @Argument String biography) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        
        if (name != null) author.setName(name);
        if (biography != null) author.setBiography(biography);
        
        return authorRepository.save(author);
    }

    @MutationMapping
    public Boolean deleteAuthor(@Argument UUID id) {
        if (authorRepository.existsById(id)) {
            authorRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @MutationMapping
    public Book createBook(@Argument String title,
                          @Argument List<UUID> authorIds,
                          @Argument String isbn,
                          @Argument Float price,
                          @Argument Integer quantity) {
        Set<Author> authors = new HashSet<>();
        for (UUID authorId : authorIds) {
            Author author = authorRepository.findById(authorId)
                    .orElseThrow(() -> new RuntimeException("Author not found: " + authorId));
            authors.add(author);
        }
        
        Book book = new Book();
        book.setTitle(title);
        book.setAuthors(authors);
        book.setIsbn(isbn);
        book.setPrice(BigDecimal.valueOf(price));
        book.setQuantity(quantity);
        return bookRepository.save(book);
    }

    @MutationMapping
    public Book updateBook(@Argument UUID id,
                          @Argument String title,
                          @Argument List<UUID> authorIds,
                          @Argument String isbn,
                          @Argument Float price,
                          @Argument Integer quantity) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        if (title != null) book.setTitle(title);
        if (authorIds != null) {
            Set<Author> authors = new HashSet<>();
            for (UUID authorId : authorIds) {
                Author author = authorRepository.findById(authorId)
                        .orElseThrow(() -> new RuntimeException("Author not found: " + authorId));
                authors.add(author);
            }
            book.setAuthors(authors);
        }
        if (isbn != null) book.setIsbn(isbn);
        if (price != null) book.setPrice(BigDecimal.valueOf(price));
        if (quantity != null) book.setQuantity(quantity);
        
        return bookRepository.save(book);
    }

    @MutationMapping
    public Boolean deleteBook(@Argument UUID id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }
} 