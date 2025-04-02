-- Insert 10 Authors
INSERT INTO author (id, name, biography) VALUES
(gen_random_uuid(), 'George Orwell', 'Author of 1984 and Animal Farm.'),
(gen_random_uuid(), 'Jane Austen', 'Author of Pride and Prejudice.'),
(gen_random_uuid(), 'J.R.R. Tolkien', 'Author of The Hobbit and The Lord of the Rings.'),
(gen_random_uuid(), 'Agatha Christie', 'Queen of Crime.'),
(gen_random_uuid(), 'Leo Tolstoy', 'Author of War and Peace.'),
(gen_random_uuid(), 'Virginia Woolf', 'Modernist author.'),
(gen_random_uuid(), 'Ernest Hemingway', 'Author of The Old Man and the Sea.'),
(gen_random_uuid(), 'F. Scott Fitzgerald', 'Author of The Great Gatsby.'),
(gen_random_uuid(), 'Mark Twain', 'Author of Adventures of Huckleberry Finn.'),
(gen_random_uuid(), 'Charles Dickens', 'Author of Oliver Twist.');

-- Function to get Author ID by name (adjust if names are not unique or for better performance)
CREATE OR REPLACE FUNCTION get_author_id(author_name VARCHAR)
RETURNS UUID AS $$
DECLARE
    author_uuid UUID;
BEGIN
    SELECT id INTO author_uuid FROM author WHERE name = author_name LIMIT 1;
    RETURN author_uuid;
END;
$$ LANGUAGE plpgsql;

-- Insert Books
DO $$
DECLARE
    author_id UUID;
BEGIN
    -- Books for George Orwell
    author_id := get_author_id('George Orwell');
    INSERT INTO book (id, title, isbn, price, quantity) VALUES
    (gen_random_uuid(), '1984', '978-0451524935', 8.99, 15),
    (gen_random_uuid(), 'Animal Farm', '978-0451526342', 7.50, 20),
    (gen_random_uuid(), 'Down and Out in Paris and London', '978-0156013981', 12.00, 5);
    INSERT INTO book_author (book_id, author_id) SELECT id, author_id FROM book WHERE title IN ('1984', 'Animal Farm', 'Down and Out in Paris and London');

    -- Books for Jane Austen
    author_id := get_author_id('Jane Austen');
    INSERT INTO book (id, title, isbn, price, quantity) VALUES
    (gen_random_uuid(), 'Pride and Prejudice', '978-0141439518', 6.99, 30),
    (gen_random_uuid(), 'Sense and Sensibility', '978-0141439662', 7.25, 18),
    (gen_random_uuid(), 'Emma', '978-0141439587', 8.00, 12);
    INSERT INTO book_author (book_id, author_id) SELECT id, author_id FROM book WHERE title IN ('Pride and Prejudice', 'Sense and Sensibility', 'Emma');

    -- Books for J.R.R. Tolkien
    author_id := get_author_id('J.R.R. Tolkien');
    INSERT INTO book (id, title, isbn, price, quantity) VALUES
    (gen_random_uuid(), 'The Hobbit', '978-0547928227', 10.50, 25),
    (gen_random_uuid(), 'The Fellowship of the Ring', '978-0547928210', 11.00, 22),
    (gen_random_uuid(), 'The Two Towers', '978-0547928203', 11.00, 19);
    INSERT INTO book_author (book_id, author_id) SELECT id, author_id FROM book WHERE title IN ('The Hobbit', 'The Fellowship of the Ring', 'The Two Towers');

    -- Books for Agatha Christie
    author_id := get_author_id('Agatha Christie');
    INSERT INTO book (id, title, isbn, price, quantity) VALUES
    (gen_random_uuid(), 'Murder on the Orient Express', '978-0062693662', 11.50, 28),
    (gen_random_uuid(), 'And Then There Were None', '978-0062073488', 9.75, 35),
    (gen_random_uuid(), 'The Murder of Roger Ackroyd', '978-0062073563', 10.20, 15);
    INSERT INTO book_author (book_id, author_id) SELECT id, author_id FROM book WHERE title IN ('Murder on the Orient Express', 'And Then There Were None', 'The Murder of Roger Ackroyd');

    -- Books for Leo Tolstoy
    author_id := get_author_id('Leo Tolstoy');
    INSERT INTO book (id, title, isbn, price, quantity) VALUES
    (gen_random_uuid(), 'War and Peace', '978-0140447934', 18.00, 10),
    (gen_random_uuid(), 'Anna Karenina', '978-0143035008', 15.50, 14),
    (gen_random_uuid(), 'The Death of Ivan Ilyich', '978-0553210951', 6.50, 20);
    INSERT INTO book_author (book_id, author_id) SELECT id, author_id FROM book WHERE title IN ('War and Peace', 'Anna Karenina', 'The Death of Ivan Ilyich');

    -- Books for Virginia Woolf
    author_id := get_author_id('Virginia Woolf');
    INSERT INTO book (id, title, isbn, price, quantity) VALUES
    (gen_random_uuid(), 'Mrs Dalloway', '978-0156628709', 12.80, 17),
    (gen_random_uuid(), 'To the Lighthouse', '978-0156907392', 13.25, 13),
    (gen_random_uuid(), 'Orlando', '978-0156701600', 11.90, 9);
    INSERT INTO book_author (book_id, author_id) SELECT id, author_id FROM book WHERE title IN ('Mrs Dalloway', 'To the Lighthouse', 'Orlando');

    -- Books for Ernest Hemingway
    author_id := get_author_id('Ernest Hemingway');
    INSERT INTO book (id, title, isbn, price, quantity) VALUES
    (gen_random_uuid(), 'The Old Man and the Sea', '978-0684801223', 9.00, 33),
    (gen_random_uuid(), 'A Farewell to Arms', '978-0684801469', 14.50, 11),
    (gen_random_uuid(), 'For Whom the Bell Tolls', '978-0684803357', 16.00, 7);
    INSERT INTO book_author (book_id, author_id) SELECT id, author_id FROM book WHERE title IN ('The Old Man and the Sea', 'A Farewell to Arms', 'For Whom the Bell Tolls');

    -- Books for F. Scott Fitzgerald
    author_id := get_author_id('F. Scott Fitzgerald');
    INSERT INTO book (id, title, isbn, price, quantity) VALUES
    (gen_random_uuid(), 'The Great Gatsby', '978-0743273565', 9.99, 40),
    (gen_random_uuid(), 'Tender Is the Night', '978-0684801544', 14.00, 8),
    (gen_random_uuid(), 'This Side of Paradise', '978-0684830476', 13.50, 6);
    INSERT INTO book_author (book_id, author_id) SELECT id, author_id FROM book WHERE title IN ('The Great Gatsby', 'Tender Is the Night', 'This Side of Paradise');

    -- Books for Mark Twain
    author_id := get_author_id('Mark Twain');
    INSERT INTO book (id, title, isbn, price, quantity) VALUES
    (gen_random_uuid(), 'Adventures of Huckleberry Finn', '978-0486280615', 5.00, 50),
    (gen_random_uuid(), 'The Adventures of Tom Sawyer', '978-0486400778', 4.50, 45),
    (gen_random_uuid(), 'A Connecticut Yankee in King Arthur''s Court', '978-0486428475', 7.00, 10);
    INSERT INTO book_author (book_id, author_id) SELECT id, author_id FROM book WHERE title IN ('Adventures of Huckleberry Finn', 'The Adventures of Tom Sawyer', 'A Connecticut Yankee in King Arthur''s Court');

    -- Books for Charles Dickens
    author_id := get_author_id('Charles Dickens');
    INSERT INTO book (id, title, isbn, price, quantity) VALUES
    (gen_random_uuid(), 'Oliver Twist', '978-0141439747', 8.25, 25),
    (gen_random_uuid(), 'A Tale of Two Cities', '978-0141439600', 7.75, 30),
    (gen_random_uuid(), 'Great Expectations', '978-0141439563', 8.50, 22);
    INSERT INTO book_author (book_id, author_id) SELECT id, author_id FROM book WHERE title IN ('Oliver Twist', 'A Tale of Two Cities', 'Great Expectations');

END;
$$; 