package com.bookstore.model

import jakarta.persistence.* // Keep JPA annotations
import java.util.HashSet
import java.util.Set
import java.util.UUID

// Note: The kotlin-maven-noarg plugin should generate a no-arg constructor needed by JPA.
// The kotlin-maven-allopen plugin makes this class open, needed by Spring/JPA proxies.
@Entity
data class Author(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null, // Use var if ID can be updated post-creation, UUID? allows null before saving

    var name: String = "", // Initialize with default or make nullable (String?) if allowed

    var biography: String? = null, // Nullable field

    @ManyToMany(mappedBy = "authors", fetch = FetchType.LAZY)
    var books: MutableSet<Book> = HashSet() // Use MutableSet for collections that can change
) {
    // Override equals and hashCode to prevent issues with collections in bidirectional relationships
    // Base equality on the unique ID only
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Author

        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    // Optional: Override toString to avoid infinite loops with bidirectional relationships
    override fun toString(): String {
        return "Author(id=$id, name='$name', biography='$biography')" // Exclude books collection
    }
} 