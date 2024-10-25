FROM openjdk:17
ARG JAR_FILE=target/prestabanco-backend.jar
COPY ${JAR_FILE} prestabanco-backend.jar
ENTRYPOINT {"java","-jar","/prestabanco-backend.jar"}