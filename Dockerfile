FROM registry.redhat.io/ubi8/openjdk-11:latest

USER root
ENV APP_HOME=/Users/bbalasub
WORKDIR $APP_HOME

COPY target/agenda-ruleservice-1.0.0-exec.jar app.jar 
COPY config/settings-offline.xml /Users/bbalasub/config/settings.xml
RUN mkdir -p /Users/bbalasub/.m2/repository/com
COPY libs/* /Users/bbalasub/.m2/repository/com

RUN chown -R 1080:1080 /Users
RUN chmod -R 777 /Users

USER 1080

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]