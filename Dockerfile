# Use the official OpenJDK 17 image as the base
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the entire project (including the source code) into the container
COPY . .

# Build the JAR file using Maven
RUN ./mvnw clean package -DskipTests

# Set the entry point to run the JAR file
ENTRYPOINT ["java", "-jar", "/app/target/cloud_tracker-0.0.1-SNAPSHOT.jar"]