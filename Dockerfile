FROM eclipse-temurin:25-jre
WORKDIR /app
COPY target/backendapi-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxRAMPercentage=70", "-jar", "app.jar"]