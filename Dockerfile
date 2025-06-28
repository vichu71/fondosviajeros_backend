# Usa una imagen base de Amazon Corretto JDK 17
FROM amazoncorretto:17

# Establece el directorio de trabajo
WORKDIR /home/app/

# Instala herramientas necesarias
RUN yum install -y tar gzip && yum clean all

# Copia wrapper y archivos necesarios para compilar dependencias
COPY .mvn/ .mvn/
COPY mvnw .
COPY pom.xml .

# Descarga dependencias sin compilar el código
RUN ./mvnw dependency:go-offline

# Copia el código fuente
COPY src/ src/

# Compila la aplicación sin ejecutar tests
RUN ./mvnw clean package -DskipTests

# Configura las opciones de memoria
ENV JAVA_TOOL_OPTIONS="-Xmx8192m"

# Expone el puerto por defecto del backend
EXPOSE 8080

# Comando de arranque
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.config.location=file:///home/app/application.properties", "-jar", "target/fondosviajeros-0.0.1-SNAPSHOT.jar"]
