FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
WORKDIR /app
COPY /src/main/resources/predict-reviews.json .
COPY /src/main/resources/genai_key.json .

ENV GOOGLE_APPLICATION_CREDENTIALS=predict-reviews.json
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
#docker build --build-arg JAR_FILE=target/*.jar -t genai-server:latest .