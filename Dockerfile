FROM openjdk:18-jdk-alpine

EXPOSE 8085

ADD build/libs/cloud-storage-0.0.1-SNAPSHOT.jar cloud-storage.jar

ENTRYPOINT ["java", "-jar", "cloud-storage.jar"]