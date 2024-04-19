# Use Alpine Linux as the base image for the build stage
FROM openjdk:17-jdk-alpine AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the necessary files for the build
COPY pom.xml mvnw mvnw.cmd ./
COPY src ./src
COPY .mvn ./.mvn

# Convert the line endings of mvnw to Unix-style (LF)
RUN dos2unix mvnw

# Build the JAR file using Maven
RUN ./mvnw clean package -DskipTests

# Use a smaller base image for the runtime stage
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/cloud_tracker-0.0.1-SNAPSHOT.jar ./

# Set the entry point to run the JAR file
ENTRYPOINT ["java", "-jar", "cloud_tracker-0.0.1-SNAPSHOT.jar"]