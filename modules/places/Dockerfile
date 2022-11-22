FROM openjdk:17-alpine3.14

COPY target/places-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT echo "starting in 20s..." && sleep 20 && java -jar app.jar