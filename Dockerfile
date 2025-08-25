# Build a lightweight runtime image for Spring Boot app
FROM eclipse-temurin:17-jre-alpine

# Install curl for container healthcheck
RUN apk add --no-cache curl

WORKDIR /app

# Copy the fat jar built by Maven (finalName=CICD)
COPY target/CICD.jar app.jar

# Expose application port
EXPOSE 8080

# Healthcheck hitting Spring Boot Actuator
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD curl -fsS http://localhost:8080/actuator/health || exit 1

# JVM options can be overridden at runtime via JAVA_OPTS
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
