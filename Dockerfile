FROM maven:3.8.1-openjdk-17-slim AS build
COPY src/ /app/src
COPY pom.xml /app
WORKDIR /app
RUN mvn package -DskipTests

FROM openjdk:17-jdk-slim
COPY --from=build /app/target/rho-interview-challenge-0.0.1.jar rho-interview-challenge-0.0.1.jar
COPY access-refresh-token-keys/ .
COPY keystore.p12 .
ENTRYPOINT ["java","-jar","/rho-interview-challenge-0.0.1.jar"]