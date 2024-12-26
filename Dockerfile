# Install Java
FROM eclipse-temurin:23-jdk AS builder

LABEL maintainer="cz"

## How to build the application

# Create a directory /app and change directory to /app
# Outside of /app
WORKDIR /app

# Inside /app directory
# Copy files over src destination
COPY mvnw .
#(Directory Directory)
COPY .mvn .mvn 

# (Singlefile .)
COPY pom.xml . 
COPY src src

# Build the application
RUN chmod +x mvnw && ./mvnw package -Dmaven.test.skip=true

# If build is successful then the jar is in ./target/day13homework-0.0.1-SNAPSHOT.jar



FROM eclipse-temurin:23-jdk

LABEL maintainer="cz"

WORKDIR /app

COPY --from=builder /app/target/project-0.0.1-SNAPSHOT.jar project.jar

## How to run the application
ENV SERVER_PORT=8080

# What port does the application need
EXPOSE ${SERVER_PORT}

# run the application
ENTRYPOINT java -jar project.jar

HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
CMD curl -s -f http://localhost:${SERVER_PORT}/healthz || exit 1