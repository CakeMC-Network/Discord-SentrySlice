FROM gradle:jdk23-alpine
LABEL name="lunarydess"
LABEL email="inbox@luzey.zip"
ARG JAR_FILE=build/libs/*-all.jar
RUN ["./gradlew", "clean", "build", "shadowJar", "--stacktrace"]
# gradle clean build shadowJar --stacktrace
# CMD ["./gradlew", "clean", "build", "shadowJar"]
COPY ${JAR_FILE} bot.jar
ENTRYPOINT ["java", "-jar", "bot.jar"]
