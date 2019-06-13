FROM openjdk:11-slim
VOLUME /tmp

ADD target/smsp-adaptor.jar smsp-adaptor.jar

# ENV JAVA_OPTS="-Xms512m -Xmx1024m"

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/smsp-adaptor.jar"]

