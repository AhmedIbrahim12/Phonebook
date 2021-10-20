FROM openjdk:8-jdk-alpine
COPY ./target/customers-0.0.1-SNAPSHOT.jar customers-0.0.1-SNAPSHOT.jar
COPY ./target/classes/sample.db sample.db
COPY ./target/classes/countries.json countries.json
ENTRYPOINT ["java","-jar","customers-0.0.1-SNAPSHOT.jar"]