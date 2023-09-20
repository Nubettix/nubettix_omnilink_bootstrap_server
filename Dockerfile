FROM openjdk:17
LABEL authors="Nubettix LLC"

# Crea el directorio donde se guardará la aplicación
WORKDIR /app

# Copia el archivo JAR de tu aplicación al contenedor
COPY build/libs/omnilink-server-bootstrap-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8007
# Ejecuta la aplicación Nota: La app requiere el server config en ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]