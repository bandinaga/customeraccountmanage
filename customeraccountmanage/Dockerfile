# Build stage
FROM eclipse-temurin:17-jdk-jammy as builder
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
# Download dependencies first (cached layer)
RUN ./mvnw dependency:go-offline
# Copy source and build
COPY src src
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# Copy JAR from build stage
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]