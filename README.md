# Setup development environment

This project is built using Spring Boot 3.0, Java 17, Gradle 8.2.1, and requires Docker for the database component. Follow the steps below to set up and run the project on your local machine.

## 1. Requirements
Before you begin, ensure you have the following software installed on your system:

- [Java 17](https://www.oracle.com/java/technologies/javase-downloads.html)
- [Gradle 8.2.1](https://gradle.org/install/)
- [Docker](https://docs.docker.com/get-docker/)

## 2. Database Setup

Start the database server using Docker by running the following command in your terminal:

```bash
docker-compose up -d
```
This will launch the required database container in the background.

## 3. Building the Project

To build the project, follow one of the methods below depending on your setup:

#### Using Globally Installed Gradle

- If you have Gradle installed globally, execute the following command in your project directory:

```bash
gradle build
```

#### Using Gradle Wrapper (Recommended)
- If you prefer using the Gradle Wrapper (which is included in the project), run the following command:

```bash
./gradlew build
```

## 4. Running the Project Locally

You have two options for running the project locally, depending on your requirements:

#### Normal Run

- To start the application with the default configuration, use the following command:

```bash
gradle bootRun
```

## Running in IntelliJ IDEA

If you are using IntelliJ IDEA, you can set up Run/Debug configurations for easy project execution. Follow these steps:

1. Go to **Run > Edit Configurations...**

2. Click the **"+"** button and select **Spring Boot**.

3. For normal run configuration:
    - Set the **Name** to a meaningful identifier.
    - Select the appropriate **Java version**.
    - Set the **Module** to "app.main".
    - Set the **Main class** to "com.helmes.app.AppApplication".
    - Set the **Active profiles** to "dev".

4. For clean database run configuration (with clean db):
    - Create a new configuration similar to the normal run, but set the **Active profiles** to "with-clean-db".

5. Click **Apply** and **OK** to save the configurations.

You can now easily run the project from IntelliJ IDEA using these configurations.

## Accessing the API

Once the project is running locally, you can access the API at the following URL:

- Local URL: [https://localhost:9090](https://localhost:8080)

Make sure to replace `localhost` with the appropriate hostname or IP address if needed.

That's it! You've successfully set up and installed the project. You can now start developing and testing your Spring Boot application.