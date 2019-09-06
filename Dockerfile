FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/social-stats-bot-0.0.1-SNAPSHOT-standalone.jar /social-stats-bot/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/social-stats-bot/app.jar"]
