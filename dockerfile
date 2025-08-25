# Use a minimal JRE image for production
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy the Spring Boot fat JAR into the container (built by Jenkins/Maven)
COPY ./target/StartToEndDeploymentFlow.jar /app

# Expose the default Spring Boot port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "StartToEndDeploymentFlow.jar"]
