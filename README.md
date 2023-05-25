# Roles Management Application

This application provides a role management system where users can create roles, assign roles to users, and retrieve role information based on memberships. It is implemented using Spring Boot, Java 17, PostgreSQL, and Swagger for API documentation.

## Approach and Solution

The problem of managing roles and memberships was approached by designing a RESTful API using Spring Boot. The application follows a layered architecture with separate packages for controllers, services, repositories, and models. The RoleService handles role creation and assignment, while the UserRepository and TeamRepository manage user and team data, respectively. The application uses PostgreSQL as the underlying database for data persistence.

Swagger is integrated to provide API documentation. The SwaggerConfig class is responsible for configuring Swagger with the necessary details and exposing the Swagger UI to visualize and interact with the API endpoints.

## How to Run the Code

To run the code locally, follow these steps:

1. Ensure that you have Java 17 and Docker installed on your machine.
2. Clone the repository to your local machine.
3. Set up the PostgreSQL database by running the provided Docker Compose file:

    ```
    docker-compose up -d
    ```

4. Build the application using Maven:

    ```
    mvn clean package
    ```

5. Run the application:

    ```
    java -jar target/roles.jar
    ```

The application will start running on http://localhost:8080.

## API Documentation

The API endpoints and their usage can be explored using Swagger UI. Once the application is running, access the Swagger UI at http://localhost:8080/swagger-ui.html. The Swagger UI provides a detailed overview of the available endpoints, request/response structures, and allows testing the APIs directly from the interface.

## Suggestions for Improvement

While the current implementation provides basic functionality for role management, there are a few areas that could be improved:

- **Input validation**: Implement robust input validation to ensure that the data passed to the API endpoints is valid and meets the required constraints. This was not implemented because three of the four provided endpoints were down, and thus it was not possible to validate their response formats and limitations. We inferred them based on the one that was up, and creted as many limitations as possible, but having the full details about the APIs would allow for extra validations.
- **Error handling**: Enhance the error handling mechanism by providing meaningful error messages and appropriate HTTP status codes in case of failures.
- **Security**: Implement authentication and authorization mechanisms to secure the API endpoints and restrict access based on user roles.
- **Logging**: Add logging statements throughout the application to facilitate troubleshooting and debugging. This could be done using popular libraries such as SLF4J.

These improvements would enhance the overall usability, security, and maintainability of the application.

One noteworty improvemente point is that, as of this version, initialization of roles in the database is being done inside the Role class. It is known that the actual best practice would be to define a RoleInitializer class extending CommandLineRunner, override the run method and start it in the main application class. That way, the Role class wouldn't be doing anything other representing an entity. However, due to time constraints, this implementation was not possible for this version.

Feel free to fork the repository, make improvements, and create pull requests if you would like to contribute to the project.
