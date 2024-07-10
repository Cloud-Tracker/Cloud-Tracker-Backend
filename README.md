# Cloud Tracker: Backend

Cloud Tracker is a web application that helps users monitor and manage the costs of their AWS resources. The app provides real-time visualizations to ensure efficient cost management and optimization.


## Demo

Check out the demo!



https://github.com/EmadMoh178/Cloud-Tracker-Backend/assets/108467668/69bf785b-3d8e-4167-87aa-76c5efa60135




## Features

- Monitor AWS resource costs.
- Visualize costs using line charts, pie charts, and bar charts.
- See the forecast for the remaining month.
- Access your account using IAM Roles or by uploading a CSV file of your usage.
- Check out blogs written by administrators to learn more about the cloud and AWS.

## Technologies Used

- **Framework**: Spring Boot
- **Database**: MySQL
- **Security**: JWT
- **CI/CD & Test Coverage**: Jacoco
- **Containerization**: Docker
- **Cloud Integration**: AWS SDK

## JaCoCo Test Coverage

![JaCoCo Coverage](./badges/jacoco.svg)
![JaCoCo Branches](./badges/branches.svg)


## FrontEnd Repo
To access the frontend repo, visit the following link: [Cloud Tracker Frontend](https://github.com/Studying-Workspace/Cloud-Tracker-Frontend).

## Installation Guide

To install and run Cloud Tracker (Backend), follow these steps:

1. **Ensure Docker is Installed**: Verify that Docker and Docker Compose are installed on your device. Installation procedures may vary based on your operating system.

2. **Navigate to Project Directory**: Use the terminal to navigate to the project directory. Alternatively, you can utilize the integrated terminal in your IDE.

3. **Create .env File**: Create a `.env` file at the root of your project directory with the following content:
    ```plaintext
    # MySQL Configuration
    MYSQL_DATABASE=your_database_name
    MYSQL_USERNAME=your_username
    MYSQL_PASSWORD=your_password
    ```
   Replace `your_database_name`, `your_username`, and `your_password` with your actual MySQL database name, username, and password.

4. **Build Docker Images**: Execute the following command to build the Docker images:
    ```bash
    docker-compose build
    ```

5. **Start Services**: Once the images are built, start the services with:
    ```bash
    docker-compose up
    ```

6. **Stopping the Services**: To stop the running services, use:
    ```bash
    docker-compose down
    ```

7. **Deleting Images**: If you wish to delete the images, you can find the image IDs by running:
    ```bash
    docker images
    ```
   Then, delete the images using:
    ```bash
    docker rmi <image_id>
    ```

## Usage

- Register or log in to your account.
- Connect your AWS account using IAM credentials or upload a CSV file.
- View the dashboard for an overview of your AWS costs.

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository.
2. Add your feature or fix.
3. Open a pull request.
