# Use an official Gradle image to build the project
FROM gradle:8.10.2-jdk17 as builder

# Set the working directory to /beApp
WORKDIR /beApp

# Copy the Gradle wrapper and top-level files
COPY gradlew ./
COPY gradle gradle/
COPY settings.gradle ./

# Copy the application code from the app directory
COPY app/build.gradle app/
COPY app/src app/src

# Make the Gradle wrapper executable
RUN chmod +x ./gradlew

# Build the application (optional, you can also skip this step)
RUN ./gradlew build --no-daemon --refresh-dependencies

# Use a lightweight JDK image for the runtime
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the built jar file from the builder stage
COPY --from=builder /beApp/app/build/libs/*.jar app.jar

# Run the Spring Boot application using java -jar
ENTRYPOINT ["java", "-jar", "app.jar"]
