# Imagen base con Java 21
FROM eclipse-temurin:21-jdk-alpine

# Carpeta de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el JAR compilado
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar

# Exponemos el puerto
EXPOSE 8080

# Comando para iniciar Spring Boot
CMD ["java", "-jar", "app.jar"]