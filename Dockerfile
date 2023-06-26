FROM openjdk:11-jdk
LABEL authors="borish3198"
VOLUME /tmp

COPY build/libs/HEAT-0.0.1-SNAPSHOT.jar HEAT-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "HEAT-0.0.1-SNAPSHOT.jar"]
