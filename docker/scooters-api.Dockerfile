FROM maven:3-amazoncorretto-17-debian AS builder
RUN apt-get update
RUN apt-get install -y binutils
WORKDIR /app
COPY ./pom.xml /app/pom.xml
COPY ./scooters-server/pom.xml /app/scooters-server/pom.xml
COPY ./scooters-proto-java/pom.xml /app/scooters-proto-java/pom.xml
RUN mvn clean
RUN mvn verify --fail-never
RUN $JAVA_HOME/bin/jlink \
         --add-modules java.base,java.xml,java.naming,java.desktop,java.sql \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /javaruntime
COPY ./scooters-server /app/scooters-server
COPY ./scooters-proto-java /app/scooters-proto-java
COPY ./proto /app/proto
RUN mvn package

FROM debian:bookworm-slim
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH "${JAVA_HOME}/bin:${PATH}"
COPY --from=builder /javaruntime $JAVA_HOME
COPY --from=builder /app/scooters-server/target/scooters-server-0.1-SNAPSHOT.jar /app.jar

CMD ["java", "-jar", "/app.jar"]