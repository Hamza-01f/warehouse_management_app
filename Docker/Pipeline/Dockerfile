# ============================
#     STAGE 1 — BUILD JAR
# ============================
FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /app

# Copy Maven descriptor first for dependency caching
COPY pom.xml .
RUN mvn -q -e -B dependency:go-offline

# Copy source and build the JAR
COPY src ./src
RUN mvn -q -e -B package -DskipTests

# ============================
#     STAGE 2 — RUN JAR
# ============================
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
