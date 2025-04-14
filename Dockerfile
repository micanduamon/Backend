# Use OpenJDK 17 as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the target folder
COPY target/*.jar app.jar

# Expose port 8080 (or your configured Spring Boot port)
EXPOSE 8000

# Run the application
CMD ["java", "-jar", "app.jar"]
