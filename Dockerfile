FROM amazoncorretto:17
COPY target/*.jar app.jar
COPY src/main/resources/db/ /db/
ENTRYPOINT ["java","-jar","/app.jar"]