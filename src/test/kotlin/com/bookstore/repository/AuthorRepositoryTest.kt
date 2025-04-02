package com.bookstore.repository

import com.bookstore.model.Author
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.util.UUID

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuthorRepositoryTest {

    companion object {
        @Container
        @JvmStatic
        val postgresContainer: PostgreSQLContainer<*> = 
            PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"))
                // .withDatabaseName("testdb") // Optional: Customize DB name
                // .withUsername("testuser")   // Optional: Customize user
                // .withPassword("testpass")   // Optional: Customize password
    }

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @Test
    fun `Flyway should run and seed data`() {
        // Simple test to verify data from V2 seed script exists
        val authors = authorRepository.findAll()
        assertThat(authors.size).isGreaterThanOrEqualTo(10) 
        
        val tolstoy = authorRepository.findByNameContainingIgnoreCase("tolstoy")
        assertThat(tolstoy).isNotEmpty
        assertThat(tolstoy[0].name).isEqualTo("Leo Tolstoy")
    }

    @Test
    fun `findByNameContainingIgnoreCase should return matching authors`() {
        // Act: Search for authors containing "Tolstoy" (case-insensitive)
        // Data is now seeded by Flyway via V2__Seed_Data.sql
        val foundAuthors = authorRepository.findByNameContainingIgnoreCase("tolstoy")

        // Assert: Check that only Leo Tolstoy is found
        assertThat(foundAuthors).hasSize(1)
        assertThat(foundAuthors[0].name).isEqualTo("Leo Tolstoy")
    }

    @Test
    fun `findByNameContainingIgnoreCase should return empty list for no matches`() {
        // Act: Search for a name that doesn't exist
        val foundAuthors = authorRepository.findByNameContainingIgnoreCase("NonExistentName")

        // Assert: Check that the list is empty
        assertThat(foundAuthors).isEmpty()
    }

    @Test
    fun `findByNameContainingIgnoreCase should handle partial matches`() {
        // Act: Search for authors containing "ginia"
        val foundAuthors = authorRepository.findByNameContainingIgnoreCase("ginia")

        // Assert: Check that Virginia Woolf is found
        assertThat(foundAuthors).hasSize(1)
        assertThat(foundAuthors[0].name).isEqualTo("Virginia Woolf")
    }

    @Test
    fun `basic repository save and findById should work`() {
        // Arrange: Create a new author
        val newAuthor = Author(name="Another Test Author", biography = "Another Test Bio")

        // Act: Save and retrieve
        val savedAuthor = authorRepository.save(newAuthor)
        entityManager.flush() // Ensure save is persisted before finding
        entityManager.clear() // Clear persistence context to ensure fresh read
        val foundOptional = authorRepository.findById(savedAuthor.id!!)

        // Assert
        assertThat(foundOptional).isPresent
        assertThat(foundOptional.get().name).isEqualTo("Another Test Author")
        assertThat(savedAuthor.id).isNotNull()
    }
} 
