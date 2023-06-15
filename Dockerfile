FROM openjdk:11-jdk
LABEL authors="borish3198"

ENTRYPOINT ["top", "-b"]