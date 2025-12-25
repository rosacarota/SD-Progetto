# OpenJML check
FROM maven:3.9.11-eclipse-temurin-21 AS openjml-check

# Install tooling needed to download/unzip OpenJML, plus runtime libs required by the
# bundled Z3 solver shipped with OpenJML.
# - libgomp1 is required because OpenJML's Z3 binary depends on libgomp.so.1 (OpenMP runtime).
RUN apt-get update && \
    apt-get install -y wget unzip ca-certificates libgomp1 && \
    rm -rf /var/lib/apt/lists/*

# Download and unpack OpenJML into /opt/openjml.
WORKDIR /opt/openjml
RUN wget -L -O openjml.zip "https://github.com/OpenJML/OpenJML/releases/download/21-0.19/openjml-ubuntu-22.04-21-0.19.zip" && \
    unzip -q openjml.zip && \
    rm openjml.zip && \
    chmod +x /opt/openjml/openjml

# Add OpenJML to PATH so we can invoke "openjml" directly.
ENV PATH="/opt/openjml:${PATH}"

# Copy project sources and pom.xml so Maven can compile and compute the dependency classpath.
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Compile (skip tests) so target/classes exists (OpenJML needs compiled classes available).
RUN mvn -q -DskipTests test-compile

# Build the project's classpath (including dependencies) and store it in a file.
# We later pass it to OpenJML via --class-path.
RUN mvn -q -DskipTests dependency:build-classpath \
    -Dmdep.includeScope=test \
    -Dmdep.outputFile=/tmp/cp.txt

# Run OpenJML in ESC mode (static verification).
# - --release 21: aligns OpenJML (21) with Java library specifications shipped with the tool.
# - --prover z3_4_3 + --exec ...: explicitly choose the bundled Z3 solver.
# - --progress: prints progress so the build doesn't look "stuck" during long proofs.
# - We verify only AcquistoBean for now (instead of all *Bean.java) to iterate incrementally.
RUN openjml --esc --release 21 \
    --progress \
    --prover z3_4_3 \
    --exec /opt/openjml/Solvers-linux/z3-4.3.1 \
    --class-path "$(cat /tmp/cp.txt):target/classes" \
    src/main/java/model/acquisto/AcquistoBean.java \
    src/main/java/model/maglietta/MagliettaBean.java \
 && echo "OPENJML DONE"

RUN echo "openjml-ok" > /openjml.ok

# === Build stage: compile the application using Maven 3.9.11 ===
FROM maven:3.9.11-eclipse-temurin-11 AS build
WORKDIR /app

# Force Docker to execute the OpenJML stage (otherwise it may be skipped)
COPY --from=openjml-check /openjml.ok /tmp/openjml.ok

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
