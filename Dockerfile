FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY deploy/aicodereviewer-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]