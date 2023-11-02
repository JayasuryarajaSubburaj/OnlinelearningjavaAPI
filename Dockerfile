	# Use a minimal base image (Alpine Linux)
	#FROM alpine:latest
	
	# Install OpenJDK JRE
	#RUN apk --no-cache add openjdk17
	
	# Stage 1: Build the application
	#FROM maven:3.8.4 AS build
	#WORKDIR /usr/app
	#COPY pom.xml .
	#COPY src src
	#RUN mvn clean package
	
	# Stage 2: Create a smaller runtime image
	#FROM alpine:latest
	#COPY --from=build usr/app/target/Online_Learning_app.jar /usr/app/
	#WORKDIR /usr/app
	#ENTRYPOINT ["java", "-jar", "Online_Learning_app.jar"]
	
	
# Use a minimal base image with OpenJDK 17
FROM openjdk:17

# Stage 1: Build the application
FROM maven:3.8.4 AS build
WORKDIR /usr/src/app
COPY pom.xml .
COPY src src
RUN mvn clean package

# Stage 2: Create the final runtime image
FROM openjdk:17
WORKDIR /usr/app
COPY --from=build /usr/src/app/target/Online_Learning_app.jar .

# Define the entry point for running your Spring Boot application
ENTRYPOINT ["java", "-jar", "Online_Learning_app.jar"]



