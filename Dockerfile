FROM openjdk:17
ARG JAR_FILE=target/prestabanco-backend.jar
COPY ${JAR_FILE} prestabanco-backend.jar
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh
ENTRYPOINT ["/wait-for-it.sh", "mysql-db:3306", "--", "java", "-jar", "/prestabanco-backend.jar"]