# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk

# Set the working directory in the container
WORKDIR /app

# Copy your JAR file from the local machine to the container
COPY build/libs/gateway-0.0.1-SNAPSHOT.jar /app/gateway-miyembro-0.0.1-SNAPSHOT.jar

# Expose the port that your config server will run on (default is 8888)
EXPOSE 8888

ENV SPRING_PROFILES_ACTIVE=prod

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/gateway-miyembro-0.0.1-SNAPSHOT.jar"]
