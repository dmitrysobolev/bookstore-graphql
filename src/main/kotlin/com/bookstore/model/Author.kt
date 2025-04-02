package com.bookstore.model

import jakarta.persistence.*
import java.util.HashSet
import java.util.Set
import java.util.UUID

// Note: The kotlin-maven-noarg plugin should generate a no-arg constructor needed by JPA.
// The kotlin-maven-allopen plugin makes this class open, needed by Spring/JPA proxies.
@Entity
data class Author(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    var name: String = "",

    var biography: String? = null,

    @ManyToMany(mappedBy = "authors", fetch = FetchType.LAZY)
    var books: MutableSet<Book> = HashSet()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Author

        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Author(id=$id, name='$name', biography='$biography')"
    }
} 