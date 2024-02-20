# Usa una imagen Alpine ligera con Java 17
FROM openjdk:17-jdk-alpine

# Define la carpeta de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo JAR de la aplicación a la carpeta de trabajo del contenedor
COPY target/inscripcion-empleador-0.0.1-SNAPSHOT.jar /app/inscripcion-empleador.jar

# Expone el puerto 8080 en el contenedor
EXPOSE 8082

# Comando para ejecutar la aplicación al iniciar el contenedor
CMD ["java", "-jar", "inscripcion-empleador.jar"]
