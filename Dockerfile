# syntax=docker/dockerfile:1

# ---------------------------------------------------------------------------
# Stage 1 — build the React frontend
# ---------------------------------------------------------------------------
FROM node:20-alpine AS frontend
WORKDIR /app/frontend
COPY frontend/package.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build
# output: /app/frontend/dist

# ---------------------------------------------------------------------------
# Stage 2 — build the Spring Boot backend, embedding the frontend build
# ---------------------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-21 AS backend
WORKDIR /app
COPY backend/pom.xml ./
RUN mvn -B -q dependency:go-offline
COPY backend/src ./src
# bake the built SPA into Spring Boot's static resources so one jar serves both
COPY --from=frontend /app/frontend/dist ./src/main/resources/static
RUN mvn -B -q clean package -DskipTests

# ---------------------------------------------------------------------------
# Stage 3 — slim runtime image
# ---------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=backend /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
