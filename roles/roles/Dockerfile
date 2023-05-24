# Use a base image with Java 17
FROM adoptopenjdk:17-jdk-hotspot

# Set the working directory
WORKDIR /app

# Copy the project files
COPY roles/pom.xml .
COPY roles/src ./src

# Build the application
RUN ./mvnw package -DskipTests

# Set the entry point for running the application
ENTRYPOINT ["java", "-jar", "target/roles.jar"]