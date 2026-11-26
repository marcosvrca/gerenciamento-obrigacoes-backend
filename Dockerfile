FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN mkdir -p /app/uploads
COPY --from=build /app/target/gerenciamento-obrigacoes-1.0.0-SNAPSHOT.jar app.jar
ENV STORAGE_PATH=/app/uploads
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
