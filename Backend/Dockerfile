FROM eclipse-temurin:22-jdk AS build
WORKDIR /workspace/app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x ./gradlew && ./gradlew build

FROM eclipse-temurin:22-jre
WORKDIR /app

COPY --from=build /workspace/app/build/libs/*.jar app.jar
RUN chmod +r+x app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
