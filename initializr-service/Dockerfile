FROM registry.misys.global.ad/ffdc-base/alpine-java:oraclejre-8
ARG APP_LOCATION="target/initializr-service.jar"
COPY ${APP_LOCATION} initializr-service.jar
EXPOSE 8080
CMD ["java", "-jar", "initializr-service.jar"]