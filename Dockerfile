FROM alpine/git as clone
WORKDIR /usr/src/app
RUN git clone https://github.com/ptrsen/dcep.git


FROM maven:3.6.3-jdk-11 as build
WORKDIR /usr/src/app
COPY --from=clone /usr/src/app/dcep/src /usr/src/app/src
COPY --from=clone /usr/src/app/dcep/pom.xml  /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package


FROM openjdk:11-jre
WORKDIR /usr/src/app
COPY --from=build /usr/src/app/target/dcep-1.0-shaded.jar /usr/src/app
#ENTRYPOINT ["sh", "-c"]
#CMD ["java -jar /usr/app/dcep-1.0-shaded.jar"]
