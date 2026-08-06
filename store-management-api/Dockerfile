# Build
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Copy maven wrapper and pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code and build the JAR
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Create runtime image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /build/target/store-management-api-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run app
ENTRYPOINT ["java", "-jar", "app.jar"]