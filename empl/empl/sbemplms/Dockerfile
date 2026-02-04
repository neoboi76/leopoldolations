FROM amazoncorretto:17
LABEL maintainer="ltgabriel@mapua.edu.ph"
WORKDIR /opt/app
EXPOSE 8080
COPY target/sbemplms-1.0-SNAPSHOT.jar /opt/app/empl.jar
ENTRYPOINT ["java", "-jar", "empl.jar"]
