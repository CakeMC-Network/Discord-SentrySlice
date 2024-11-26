FROM gradle:jdk23-alpine
MAINTAINER lunarydess
ARG JAR_FILE=build/libs/*-all.jar
# RUN gradle clean build shadowJar --stacktrace
CMD ["./gradlew", "clean", "build", "shadowJar"]
COPY ${JAR_FILE} bot.jar
ENTRYPOINT ["java", "-jar", "bot.jar"]
