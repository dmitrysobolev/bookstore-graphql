package com.bookstore.controller

import com.bookstore.model.Author
import com.bookstore.model.Book
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.graphql.test.tester.GraphQlTester
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // Load full context, random port
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Use Testcontainers DB
class BookstoreControllerTest {

    companion object {
        @Container
        @JvmStatic
        val postgresContainer: PostgreSQLContainer<*> = 
            PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"))
    }

    @Autowired
    private lateinit var graphQlTester: GraphQlTester // Client for GraphQL testing

    @Test
    fun `query books should return seeded books`() {
        // Arrange: Query to get all books and their authors
        val query = """
            query {
              books(page: {size: 50}) {
                content {
                  id
                  title
                  authors {
                    name
                  }
                }
              }
            }
            """.trimIndent() // Trim indentation and ensure proper newlines

        // Act & Assert
        graphQlTester.document(query)
            .execute()
            .path("books.content")
            .entityList(Book::class.java)
            .hasSizeGreaterThan(29)
            .get().let { books -> // Extract list first, then assert
                assertThat(books).isNotNull // Basic check on the list itself
                // Check a specific book/author relationship seeded by Flyway
                val gatsby = books.find { it.title == "The Great Gatsby" }
                assertThat(gatsby).isNotNull
                assertThat(gatsby?.authors).isNotEmpty
                assertThat(gatsby?.authors?.any { it.name == "F. Scott Fitzgerald" }).isTrue()
            }
    }

    @Test
    fun `query authors with name filter should return specific author and books`() {
        // Arrange: Query for Leo Tolstoy and his books
        val authorName = "Leo Tolstoy"
        val query = """
            query (${'$'}name: String) {
              authors(name: ${'$'}name) {
                content {
                  id
                  name
                  books {
                    title
                  }
                }
              }
            }
            """.trimIndent() // Trim indentation and ensure proper newlines

        // Act & Assert
        graphQlTester.document(query)
            .variable("name", authorName)
            .execute()
            .path("authors.content")
            .entityList(Author::class.java)
            .hasSize(1)
            .get().let { authors -> // Extract list first, then assert
                 assertThat(authors).hasSize(1) // Assert size again for clarity
                 val author = authors[0]
                 assertThat(author.name).isEqualTo(authorName)
                 assertThat(author.books).hasSize(3) // Tolstoy has 3 books in seed data
                 assertThat(author.books.map { it.title }).containsExactlyInAnyOrder(
                     "War and Peace", 
                     "Anna Karenina", 
                     "The Death of Ivan Ilyich"
                 )
             }
    }

    @Test
    fun `createAuthor mutation should add author`() {
        // Arrange: Mutation to create a new author
        val authorName = "Test Author McTestFace"
        val biography = "A prolific writer of tests."
        val mutation = """
            mutation (${'$'}name: String!, ${'$'}bio: String) {
              createAuthor(name: ${'$'}name, biography: ${'$'}bio) {
                id
                name
                biography
              }
            }
            """.trimIndent() // Trim indentation and ensure proper newlines

        // Act & Assert
        graphQlTester.document(mutation)
            .variable("name", authorName)
            .variable("bio", biography)
            .execute()
            .path("createAuthor")
            .entity(Author::class.java)
            .satisfies { author ->
                assertThat(author.id).isNotNull()
                assertThat(author.name).isEqualTo(authorName)
                assertThat(author.biography).isEqualTo(biography)
            }

        // Optional: Verify with a query - standard string with escaped internal quotes
        graphQlTester.document("{ authors(name: \"$authorName\") { content { name } } }")
            .execute()
            .path("authors.content[0].name")
            .entity(String::class.java)
            .isEqualTo(authorName)
    }

    // TODO: Add tests for createBook, updateAuthor, updateBook, deleteAuthor, deleteBook
}
