FROM openjdk:24-slim
MAINTAINER lunarydess
ADD build/libs/Discord-SentrySlice-0.0.0-dev-all.jar /bot.jar
ENTRYPOINT ["java","-jar","/bot.jar"]
