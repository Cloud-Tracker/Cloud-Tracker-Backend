# JaCoCo Test Coverage

![JaCoCo Coverage](./badges/jacoco.svg)
![JaCoCo Branches](./badges/branches.svg)

# Docker Guide

To utilize Docker for Cloud Tracker, follow these steps:

1. **Ensure Docker is Installed:** Verify that Docker and Docker Compose are installed on your device. Installation procedures may vary based on your operating system.

2. **Navigate to Project Directory:** Use the terminal to navigate to the project directory. Alternatively, you can utilize the integrated terminal in your IDE.

3. **Create .env File:** Create a `.env` file at the root of your project directory with the following content:
    ```plaintext
    # MySQL Configuration
    MYSQL_DATABASE=your_database_name
    MYSQL_USERNAME=your_username
    MYSQL_PASSWORD=your_password
    ```
   Replace `your_database_name`, `your_username`, and `your_password` with your actual MySQL database name, username, and password.

4. **Build Docker Images:** Execute the following command to build the Docker images:
    ```bash
    docker-compose build
    ```

5. **Start Services:** Once the images are built, start the services with:
    ```bash
    docker-compose up
    ```

6. **Stopping the Services:** To stop the running services, use:
    ```bash
    docker-compose down
    ```

7. **Deleting Images:** If you wish to delete the images, you can find the image IDs by running:
    ```bash
    docker images
    ```
   Then, delete the images using:
    ```bash
    docker rmi <image_id>
    ```