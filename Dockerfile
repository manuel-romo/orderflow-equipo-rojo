# ---- Etapa 1. Construcción de jar
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn -B package -DskipTests -pl orders-api -am

# ---- Etapa 2. Imagen final
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/orders-api/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]