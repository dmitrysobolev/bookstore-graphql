-- Create Author table
CREATE TABLE author (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    biography TEXT
);

-- Create Book table
CREATE TABLE book (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    price NUMERIC(10, 2),
    quantity INTEGER
);

-- Create Book-Author join table
CREATE TABLE book_author (
    book_id UUID NOT NULL,
    author_id UUID NOT NULL,
    PRIMARY KEY (book_id, author_id),
    FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES author(id) ON DELETE CASCADE
); 