FROM java:8
MAINTAINER Andrew Braithwaite "andrew@losd.info"
VOLUME ["/tmp"]
ADD build/libs/galen-0.1.0.jar app.jar
RUN bash -c 'touch /app.jar'
RUN mkdir /data
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]
