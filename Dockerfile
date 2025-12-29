# === Build stage: compile the application using Maven 3.9.11 ===
FROM maven:3.9.11-eclipse-temurin-11 AS build
WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Build the WAR file
RUN mvn clean package -DskipTests

# === Runtime stage: lightweight Tomcat container ===
FROM tomcat:9.0.112-jdk11

# Remove default Tomcat applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the generated WAR file from the build stage
COPY --from=build /app/target/SD-Progetto.war /usr/local/tomcat/webapps/ROOT.war

# Copy context.xml to configure JNDI DataSource
COPY src/main/webapp/META-INF/context.xml /usr/local/tomcat/conf/context.xml

# Expose Tomcat port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
