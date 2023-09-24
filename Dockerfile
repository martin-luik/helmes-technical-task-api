FROM openjdk:17

COPY build/libs/app-0.0.1-SNAPSHOT.jar .

CMD ["java", "-jar", "app-0.0.1-SNAPSHOT.jar"]