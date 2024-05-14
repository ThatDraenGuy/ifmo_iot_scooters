FROM maven:3-amazoncorretto-17-debian AS builder
RUN apt-get update
RUN apt-get install -y binutils
RUN $JAVA_HOME/bin/jlink \
	 --add-modules java.base,java.xml,java.naming,java.desktop,java.sql,jdk.httpserver,java.management \
	 --strip-debug \
	 --no-man-pages \
	 --no-header-files \
	 --compress=2 \
	 --output /javaruntime
WORKDIR /app
COPY ./pom.xml /app/pom.xml
COPY ./scooters-core/pom.xml /app/scooters-core/pom.xml
COPY ./scooters-map-api/pom.xml /app/scooters-map-api/pom.xml
COPY ./scooters-server/pom.xml /app/scooters-server/pom.xml
COPY ./scooters-proto-java/pom.xml /app/scooters-proto-java/pom.xml
RUN mvn clean
RUN mvn verify --fail-never
COPY ./scooters-core /app/scooters-core
COPY ./scooters-server /app/scooters-server
COPY ./scooters-proto-java /app/scooters-proto-java
COPY ./proto /app/proto
RUN mvn package

FROM debian:bookworm-slim

RUN apt-get update
RUN apt-get install -y curl
RUN curl -sSL "https://github.com/fullstorydev/grpcurl/releases/download/v1.8.6/grpcurl_1.8.6_linux_x86_64.tar.gz" | tar -xz -C /usr/local/bin

ENV JAVA_HOME=/opt/java/openjdk
ENV PATH "${JAVA_HOME}/bin:${PATH}"
COPY --from=builder /javaruntime $JAVA_HOME
COPY --from=builder /app/scooters-server/target/scooters-server-0.1-SNAPSHOT.jar /app.jar

CMD ["java", "-jar", "/app.jar"]