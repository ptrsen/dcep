FROM alpine/git as clone
WORKDIR /app
RUN git clone https://github.com/ptrsen/dcep.git


FROM maven:3.6-jdk-11-alpine as build
WORKDIR /app
COPY --from=clone /src  /app/src
COPY --from=clone pom.xml  /app
RUN mvn -f /app/pom.xml clean package


FROM openjdk:11-jre-alpine
WORKDIR /app
COPY --from=build /app/target/dcep-1.0-shaded.jar /app
CMD ["java -jar dcep-1.0-shaded.jar"]