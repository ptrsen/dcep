############################################################
# Clone stage
############################################################

FROM alpine/git as clone
WORKDIR /app
RUN git clone https://github.com/ptrsen/dcep.git

############################################################
# Build stage
############################################################
FROM maven:3.6.3-jdk-11 as build
WORKDIR /app
COPY --from=clone /app/dcep/src /app/src
COPY --from=clone /app/dcep/pom.xml  /app
RUN mvn -f /app/pom.xml clean package

############################################################
# Run stage
###########################################################
FROM openjdk:11-jre
WORKDIR /app
COPY --from=build /app/target/dcep-1.0-shaded.jar /app
RUN apt-get update && apt-get install -y \
    net-tools \
    iperf3 \
    telnet \
    nmap \
    netcat

EXPOSE 10000/udp
#ENTRYPOINT ["sh", "-c"]
#CMD ["java -jar /usr/app/dcep-1.0-shaded.jar"]