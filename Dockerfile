FROM openjdk:17-jdk-slim
VOLUME /tmp
ARG OPEN_AI_KEY
ENV OPENAI_API_KEY=$OPEN_AI_KEY
# Now you can use $OPENAI_API_KEY in your build steps and it will be available at runtime.
RUN echo "Setting runtime variable: $OPENAI_API_KEY"
COPY deploy/aicodereviewer-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]