package com.bookstore.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String biography;
    
    @ManyToMany(mappedBy = "authors")
    private Set<Book> books = new HashSet<>();
} 