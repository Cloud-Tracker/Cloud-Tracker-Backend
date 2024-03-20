# Docker Guide

To utilize the Docker for Cloud Tracker, follow these steps:

1. **Ensure Docker is Installed:** Verify that Docker is installed on your device. Installation procedures may vary based on your operating system.

2. **Navigate to Project Directory:** Use the terminal to navigate to the project directory. Alternatively, you can utilize the integrated terminal in your IDE.

3. **Build Docker Image:** Execute the following command to build the Docker image. You can replace `cloud-tracker` with your preferred name:
    ```bash
    docker build -t cloud-tracker .
    ```

4. **Confirm Image Build:** Check if the image was successfully built by running:
    ```bash
    docker images
    ```

5. **Run Docker Image:** Depending on your preference regarding OAuth2 features, choose one of the following options:
    - a) With OAuth2:
        ```bash
        docker run -p 8080:8080 -e GITHUB_CLIENT_ID=your_client_id -e GITHUB_CLIENT_SECRET=your_client_secret cloud-tracker
        ```
      Replace `your_client_id` and `your_client_secret` with your GitHub OAuth2 client ID and client secret.

    - b) Without OAuth2:
        ```bash
        docker run -p 8080:8080 cloud-tracker
        ```

6. **Check Running Containers:** Open a new terminal tab and enter the command:
    ```bash
    docker container ls
    ```

7. **Stopping Containers:** To stop a running container, use:
    ```bash
    docker rm -f <container_id>
    ```
   Replace `<container_id>` with the container ID of the container you wish to stop.

8. **Deleting Image:** If you wish to delete the image, use the following command:
    ```bash
    docker rmi <image_id>
    ```
   You can find `<image_id>` by running:
    ```bash
    docker images
    ```

