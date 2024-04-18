# Use an image that includes Maven so that we can use it to build the JAR File
FROM maven:3.8.4-openjdk-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the entire project (including the source code) into the container
COPY . .

# Build the JAR file using Maven
RUN mvn clean package -DskipTests

# Use a lightweight OpenJDK image for the runtime stage
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the build stage to runtime stage
COPY --from=build /app/target/cloud_tracker-0.0.1-SNAPSHOT.jar .

# Set the entry point to run the JAR file
ENTRYPOINT ["java", "-jar", "/app/cloud_tracker-0.0.1-SNAPSHOT.jar"]
