FROM openjdk:21-slim as build
WORKDIR /workspace/app

# Copy Gradle wrapper and source code
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY src src

# Give execution permissions to the Gradle wrapper and build the application
RUN chmod +x ./gradlew && ./gradlew build

# Start with a clean slate for the runtime image
FROM openjdk:21-slim
WORKDIR /app

COPY --from=build /workspace/app/build/libs/*.jar app.jar
RUN chmod +r+x app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]