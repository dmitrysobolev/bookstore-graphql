package com.bookstore.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.util.HashSet
import java.util.UUID

@Entity
data class Book(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    var title: String = "",

    // Using LAZY fetch to prevent N+1 issues, with @SchemaMapping in controller
    @ManyToMany(fetch = FetchType.LAZY) 
    @JoinTable(
        name = "book_author",
        joinColumns = [JoinColumn(name = "book_id")],
        inverseJoinColumns = [JoinColumn(name = "author_id")]
    )
    var authors: MutableSet<Author> = HashSet(),

    var isbn: String? = null,

    var price: BigDecimal? = null,

    var quantity: Int? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Book

        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Book(id=$id, title='$title', isbn=$isbn, price=$price, quantity=$quantity)" 
    }
} 
