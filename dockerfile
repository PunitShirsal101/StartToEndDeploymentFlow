# Optional PostgreSQL stage (build with: docker build -f dockerfile --target postgres -t postgresql-16.10-alpine-img .)
FROM postgres:16.10-alpine AS postgres
ENV POSTGRES_DB=mydb
ENV POSTGRES_USER=postgre
ENV POSTGRES_PASSWORD=postgre
EXPOSE 5432
VOLUME ["/var/lib/postgresql/data"]

# --- Application stage (default final image) ---
# Use the official eclipse-temurin:21-jre-alpine image from Docker Hub
FROM eclipse-temurin:21-jre-alpine

# Set working directory inside the container
WORKDIR /app

# Copy the compiled Java application JAR file into the container
COPY ./target/StartToEndDeploymentFlow.jar /app

# Expose the port the Spring Boot application will run on
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "StartToEndDeploymentFlow.jar"]
