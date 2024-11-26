FROM gradle:jdk23-alpine
MAINTAINER lunarydess
WORKDIR /app
ADD --chown=gradle:gradle /app/sentryslice /app
RUN ./gradlew clean build shadowJar --stacktrace
ADD build/libs/Discord-SentrySlice-0.0.0-dev-all.jar /app/bot.jar
ENTRYPOINT ["java","-jar","/app/bot.jar"]
