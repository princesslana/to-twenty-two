FROM openjdk:11 AS build
WORKDIR /var/src

# Download gradle
COPY gradlew .
COPY gradle/ gradle/
RUN ./gradlew --no-daemon --version

# Build
COPY . .
RUN ./gradlew --no-daemon shadowJar

FROM openjdk:11-jre
WORKDIR /var/app
RUN mkdir /var/data

ENV TTT_DATA=/var/data

COPY --from=build /var/src/build/libs/to-twenty-two-all.jar .

CMD java -jar to-twenty-two-all.jar
