FROM maven:3.6-jdk-13-alpine AS build
WORKDIR /app
ADD . /app
RUN mvn clean install

FROM openjdk:13-alpine AS run
ENV JARFILE "nitro4all-1.0-SNAPSHOT-fat.jar"
WORKDIR /app
COPY --from=build /app/target/${JARFILE} .
CMD ["/bin/sh", "-c", "java -jar ${JARFILE}"]