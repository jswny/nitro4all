FROM maven:3.6-jdk-13-alpine
WORKDIR /nitro4all
ADD . /nitro4all
RUN mvn clean install
CMD ["java", "-jar", "target/nitro4all-1.0-SNAPSHOT-fat.jar"]