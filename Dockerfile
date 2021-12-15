FROM openjdk:latest
ADD target/tracom-planner.jar tracom-planner.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "tracom-planner.jar"]