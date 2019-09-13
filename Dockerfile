FROM openjdk:11-slim
VOLUME /tmp

COPY target/smsp-adaptor.jar smsp-adaptor.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/smsp-adaptor.jar"]

