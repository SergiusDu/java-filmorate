# Filmorate API

Filmorate is a RESTful web service for managing a collection of films and user data. It provides a simple API for
creating, retrieving, updating, and deleting films and users. The project is built with Java and the Spring Boot
framework, following a modular, domain-driven design approach.

## Project Structure

This project uses a multi-module Maven setup to enforce separation of concerns and improve maintainability. Each module
has a distinct responsibility:

* `common`: Contains shared code used across different modules, such as custom exceptions, validation utilities, and
  configuration properties.
* `films`: Implements all business logic related to films. This includes the domain model, application services,
  repository interfaces, and the web controller for the `/films` endpoint.
* `users`: Implements all business logic related to users. This includes the domain model, application services,
  repository interfaces, and the web controller for the `/users` endpoint.
* `filmorate-app`: The main application module. It brings all the other modules together, contains the main application
  class (`FilmorateApplication`), and is responsible for building the final executable JAR file.

## Technologies Used

* **Java 22**
* **Spring Boot 3.x**
* **Apache Maven** for dependency management and build automation
* **JUnit 5** & **AssertJ** for unit testing
* **Lombok** to reduce boilerplate code

## Prerequisites

Before you begin, ensure you have the following installed on your system:

* **Java Development Kit (JDK)** version 22 or later.
* **Apache Maven**.

## How to Run the Project

Follow these steps to get the application running on your local machine.

### 1. Clone the Repository

First, clone the project repository from GitHub to your local machine:

```bash
git clone https://github.com/SergiusDu/java-filmorate.git
cd java-filmorate
```

### 2. Build the Project

Navigate to the root directory of the project (where the main `pom.xml` is located) and run the following Maven command.
This will compile the code, run tests, and package all modules.

```bash
mvn clean install
```

This command cleans the `target` directories, then builds all the modules (`common`, `films`, `users`, and
`filmorate-app`) in the correct order.

### 3. Run the Application

You can run the application in two ways:

#### Running in Development Mode (Dev Start)

This mode is recommended for active development. It uses the Spring Boot Maven plugin and enables hot-reloading (
automatic application restart on code changes), which significantly speeds up the development cycle.

From the project's root directory, run:

```bash
# This command starts the app on http://localhost:8080
mvn spring-boot:run
```

* `mvn spring-boot:run`: This command starts the Spring Boot application.

#### Running the Packaged Application

This mode runs the compiled JAR file, which is how the application would typically be run in a production or testing
environment.

First, ensure you have built the project with `mvn clean install`. Then, run the following command:

```bash
# By default, this also starts the app on http://localhost:8088
# because the 'dev' profile is active in the JAR's configuration.
java -jar filmorate-app/target/filmorate-app-0.0.1-SNAPSHOT.jar
```

## API Endpoints

The service exposes the following REST endpoints.

### Film Endpoints

* `GET /films`: Retrieves a list of all films.
* `POST /films`: Adds a new film.
* `PUT /films`: Updates an existing film.

**Example `POST /films` body:**

```json
{
  "name": "Inception",
  "description": "A thief who steals corporate secrets through the use of dream-sharing technology...",
  "releaseDate": "2010-07-16",
  "duration": 148
}
```

### User Endpoints

* `GET /users`: Retrieves a list of all users.
* `POST /users`: Adds a new user.
* `PUT /users`: Updates an existing user.

**Example `POST /users` body:**

```json
{
  "email": "contact@user.com",
  "login": "userLogin",
  "name": "John Doe",
  "birthday": "1990-01-15"
}
```

## Configuration

Application settings can be configured in the `src/main/resources/` directory of the `filmorate-app` module:

* `application.yml`: Default configuration, including validation rules (e.g., max film description length, earliest
  release date).
* `application-dev.yml`: Development-specific profile. It is activated by default and sets the server port to `8088` and
  enables `DEBUG` logging.