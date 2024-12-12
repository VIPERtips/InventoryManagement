# Build stage
FROM maven:3.9.9-eclipse-temurin-21 as build
COPY . . 
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-alpine

# Copy the application JAR
COPY --from=build target/*.jar demo.jar

# Copy the public/images folder to the runtime image
COPY public/images /public/images

# Set the working directory
WORKDIR /

# Expose the images directory for persistence
VOLUME /public/images

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "demo.jar"]
