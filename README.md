# Bookstore GraphQL API (Kotlin + Spring Boot)

This project demonstrates a simple bookstore management system API built using:

*   Kotlin
*   Spring Boot 3
*   Spring for GraphQL
*   Spring Data JPA (Hibernate)
*   PostgreSQL (via Docker)
*   Flyway (for database migrations and seeding)
*   Maven

## Features

*   Query books and authors.
*   Filter authors by name.
*   Create, update, delete books and authors.
*   Many-to-many relationship between books and authors.
*   UUIDs for primary keys.
*   Database schema management and seeding via Flyway.

## Prerequisites

*   Java 17 JDK
*   Maven 3.x
*   Docker and Docker Compose

## Running the Application

1.  **Clone the repository:**
    ```bash
    git clone git@github.com:dmitrysobolev/bookstore-graphql.git
    cd bookstore-graphql
    ```

2.  **Start the PostgreSQL database:**
    Make sure Docker is running. Navigate to the project root directory and run:
    ```bash
    docker compose up -d
    ```
    This will start a PostgreSQL container named `bookstore-postgres` listening on port 5432. The database `bookstore` will be created with the user `admin` and password `P@ssw0rd` (as configured in `docker-compose.yml` and `application.yml`).

3.  **Build the application:**
    ```bash
    ./mvnw clean install
    ```

4.  **Run the application:**
    ```bash
    ./mvnw spring-boot:run
    ```
    The application will start on port 8080. Flyway will automatically run the migrations in `src/main/resources/db/migration` to create the schema and seed the initial data.

## Testing

The project uses JUnit 5 and Testcontainers for integration testing.

*   **Repository Tests (`src/test/kotlin/.../repository`)**: Use `@DataJpaTest` and Testcontainers to test JPA repositories against a real PostgreSQL database spun up dynamically for the test execution.
*   **Controller Tests (`src/test/kotlin/.../controller`)**: Use `@SpringBootTest` and Testcontainers to run full integration tests of the GraphQL API layer, also against a dynamic PostgreSQL database.

**Running Tests:**

1.  **Ensure Docker is running.** Testcontainers requires a running Docker daemon to manage the test database containers.
2.  Run the tests using Maven:
    ```bash
    ./mvnw test
    ```
    This command will compile the test code and execute all tests using the Surefire plugin.

## Accessing the API

*   **GraphQL Playground (GraphiQL):** Available at [http://localhost:8080/graphiql](http://localhost:8080/graphiql)
*   **GraphQL Endpoint:** `http://localhost:8080/graphql`

### Example Queries/Mutations

Refer to the `src/main/resources/graphql/schema.graphqls` file for the full schema.

**Query all books:**
```graphql
query {
  books {
    id
    title
    isbn
    price
    quantity
    authors {
      id
      name
    }
  }
}
```

**Query authors filtered by name:**
```graphql
query {
  authors(name: "Tolstoy") {
    id
    name
    books {
      id
      title
    }
  }
}
```

**Create an author:**
```graphql
mutation {
  createAuthor(name: "New Author", biography: "A short bio.") {
    id
    name
  }
}
```

**Create a book:**
```graphql
mutation {
  createBook(
    title: "Another New Book"
    isbn: "999-1234567890"
    price: 19.95
    quantity: 5
    authorIds: ["<author-uuid-here>"] # Replace with an existing author ID
  ) {
    id
    title
    authors {
      id
      name
    }
  }
}
```

## Stopping the Database

To stop the PostgreSQL container:
```bash
docker compose down
```

To stop the container and remove the data volume (useful for a clean restart):
```bash
docker compose down -v
``` 