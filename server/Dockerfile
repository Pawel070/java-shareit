FROM amazoncorretto:11-alpine-jdk
FROM openjdk:11
COPY target/*.jar server.jar
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar","/server.jar"]
# ENV TZ=Europe/Moscow
# ENV TZ=Asia/Jerusalem