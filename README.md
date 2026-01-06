# 🐻‍❄️ whiTee — Software Dependability Project

## 📌 Overview

This repository contains **whiTee**, a fork of the original project developed for the *Tecnologie Software per il Web (TSW)* course.
As part of the **Software Dependability (SD)** course, the project has been extended to apply techniques for:

* Formal specification and verification
* Structural and mutation testing
* Performance evaluation
* Containerization
* Security analysis

The focus is not on introducing new features, but on evaluating and strengthening the system using the methodologies introduced during the course.

---

## 🔧 Dependability & Analysis Toolchain

As part of the SD course activities, this project integrates several tools to assess and improve correctness, test quality, performance, and security.

### 🧾 **OpenJML — JML Formal Specification**

OpenJML is used to introduce formal methods into the codebase:

* JML (Java Modeling Language) specifications are written for selected core methods.
* Static checking verifies that implementations respect the specified contracts.

This enforces explicit behavioral contracts and supports reasoning about correctness beyond ordinary testing.

---

### 🧪 **JaCoCo — Code Coverage Measurement**

JaCoCo is used as the structural code coverage tool:

* Measures line and branch coverage for the production code.
* Highlights untested or weakly tested portions of the system.
* Produces an XML report (`target/site/jacoco/jacoco.xml`) consumed by Codecov.

---

### 📊 **Codecov — Coverage Reporting in CI/CD**

Codecov is used to publish and visualize coverage in CI:

* Uploads JaCoCo reports from GitHub Actions.
* Provides a web dashboard for coverage trends and per-file insights.
* Adds pull request comments with coverage diffs.

This ensures continuous visibility of test effectiveness.

* **Codecov Report:**
  [https://app.codecov.io/gh/rosacarota/SD-Progetto](https://app.codecov.io/gh/rosacarota/SD-Progetto)

---

### 🧬 **PiTest — Mutation Testing**

PiTest evaluates the *effectiveness* of the test suite:

* Generates mutants (small controlled changes) in project classes.
* Executes the JUnit 5 test suite against each mutant.
* Marks mutants as **killed** when at least one test fails, otherwise **survived**.

PiTest produces reports under `target/pit-reports`.

---

### 🚀 **JMH - Microbenchmarks**

JMH (Java Microbenchmark Harness) is used to analyze **micro-level performance characteristics** of isolated backend components.
It is appropriate for benchmarking:

* Utility functions
* Business logic routines
* Computation-heavy operations

---

### 🛡️ **GitGuardian — Secret Leakage Detection**

GitGuardian is used to scan the repository for accidentally exposed secrets.
It monitors:

* API keys, tokens, and credentials
* Hard-coded secrets in source files
* Sensitive patterns introduced across commits or branches

This prevents accidental credential leaks and improves the security posture of the project during development.

---

### 🧭 **Snyk — Comprehensive Vulnerability Analysis**

Snyk is used to detect security issues across the project's codebase and environment.
It provides:

* Vulnerability scanning for direct and transitive Maven dependencies
* SAST checks for insecure coding patterns
* Container image and base-layer vulnerability analysis
* Detection of misconfigurations in Docker and IaC files
* Continuous monitoring for newly disclosed CVEs

This ensures that both the application and its execution environment remain secure and up to date.

---

### 🧹 **SonarQube Cloud — Static Code & Security Analysis**

SonarQube Cloud performs static analysis on the codebase to detect:

* Code smells and maintainability issues
* Potential bugs and logical errors
* Security hotspots and unsafe patterns

Its dashboards offer actionable metrics that help improve long-term maintainability, readability, and robustness.

---

## 🧱 Quality Gates

### 🧪 **JaCoCo Coverage Gates**

The build requires:

* 80% line coverage
* 80% branch coverage

Non-essential classes (e.g., beans, populators, utilities) are excluded so that the metrics reflect meaningful test obligations.
If coverage falls below these thresholds, the build fails during the `verify` phase.

---

### 🧬 **PiTest Mutation Gates**

To ensure actual test effectiveness, PiTest enforces:

* 50% minimum mutation score
* 80% minimum mutation coverage

The build fails if too many mutants survive or if insufficient mutated lines are exercised, ensuring that the test suite is capable of revealing faulty behavior.

Note that we excluded the same classes as we did with JaCoCo.

---

### 🧹 **SonarQube Quality Gates**

The project adopts the Sonar way quality gate, which evaluates only new code and requires all the following conditions to be satisfied:

* No new bugs
* No new vulnerabilities
* Limited technical debt
* All new security hotspots are reviewed
* Sufficient test coverage on new code
* Limited code duplication

If any of these criteria are not met, SonarQube marks the analysis as failed, blocking the quality gate.

---

## 🚀 CI/CD Pipeline

The repository uses a **three-workflow GitHub Actions setup** to automate testing, quality analysis, security checks, and container delivery.

### 1️⃣ CI/CD Pipeline (Build, Tests, Coverage & Code Quality)

Triggered on every push or pull request to `main`, this workflow performs:

* **Build & Test:** compilation, execution of the test suite, execution of mutation testing, and enforcement of JaCoCo and PiTest quality gates
* **Coverage & Mutation Reporting:** generation of JaCoCo and PiTest HTML reports
* **GitHub Pages Publishing:** automatic publication of generated reports
* **Codecov Integration:** upload of coverage data and test results to Codecov
* **Test Summary:** human-readable JUnit report via dorny/test-reporter
* **Artifacts:** upload of compiled classes, JaCoCo report, GitHub Pages reports and the final WAR artifact

After the build and test phase, a dedicated job performs:
* **SonarQube Cloud Analysis:** static analysis of code quality and security, using coverage data produced by the build and executed in a separate job after a successful build and test phase.

📄 **Public Reports (GitHub Pages):**

* **JaCoCo Reports:**
  [https://rosacarota.github.io/SD-Progetto/Jacoco/](https://rosacarota.github.io/SD-Progetto/Jacoco/)

* **PiTest Reports:**
  [https://rosacarota.github.io/SD-Progetto/PiTest/](https://rosacarota.github.io/SD-Progetto/PiTest/)

---

### 2️⃣ Security Pipeline (Secrets & Vulnerability Scanning)

Triggered **after a successful CI/CD Pipeline run** (or manually via `workflow_dispatch`), this workflow focuses on security analysis:

* **GitGuardian Scan:** detection of hard-coded secrets and credential leaks in the repository history
* **Snyk SAST:** source code analysis to identify insecure coding patterns
* **Snyk Maven Scan:** vulnerability scanning of direct and transitive Maven dependencies
* **Automated Build & Configuration:** generation of a `context.xml` file from GitHub Secrets and build of a local Docker image (`app`) for analysis
* **Snyk Docker Scan:** vulnerability scanning of the built container image with severity thresholds

This stage ensures that the project passes secret detection and vulnerability checks before the Docker image is published.

---

### 3️⃣ Docker Publishing Workflow (Multi-Architecture Image Release)

Triggered **after a successful Security Pipeline run** (or manually via `workflow_dispatch`), this workflow handles the final container delivery:

* **Automated Build:** compiles the application and generates a production-ready `context.xml` using GitHub Secrets

* **Multi-Architecture Image Build:** uses Docker Buildx to produce images for both `amd64` and `arm64`

* **Metadata Injection:** automatically tags the image using `docker/metadata-action`

* **DockerHub Publishing:** pushes the final image to:

  ```text
  rosacarota/whitee-app:latest
  ```

* **Build Summary & Downloadable Artifact:** the action automatically provides a **Docker Build Summary**, including cache usage, build duration, and a downloadable `.dockerbuild` archive

This workflow ensures that only code that has passed all tests and security checks is released as a reproducible and traceable Docker image.

---
## 🔐 Dependabot Integration

The repository also enables **GitHub Dependabot**, which:

* Monitors dependencies for security vulnerabilities
* Can automatically create pull requests with safe version upgrades
* Ensures the project uses secure and up-to-date libraries

Dependabot acts independently from CI/CD, strengthening the security posture of the codebase.

---

## 🐳 Running whiTee with Docker

### 1️⃣ Create a `.env` file

Create a `.env` file in the project root with the following variables:

```
MYSQL_ROOT_PASSWORD=your_root_pw
MYSQL_DATABASE=whiTee
MYSQL_USER=your_user
MYSQL_PASSWORD=your_pw
MYSQL_PORT=3306

AES_KEY_BASE64=your_base64_key
```
---

### 2️⃣ Run the Application

```bash
docker compose up --build
```

This command starts:

* `db` → MySQL 8.0
* `app` → whiTee web application (Tomcat)

The application will be available at:

```
http://localhost:8080
```

---

### ⚓ Local Development Override

For local builds, the file `docker-compose.override.yml` allows replacing the DockerHub image with a locally built one:

```yaml
services:
  app:
    build: .
    image: whitee-app:local
```

---

## 👥 Credits

whiTee was originally designed and developed by **our team** as part of the TSW course.
This SD edition allowed us to revisit our own project from a new angle — applying formal methods, testing techniques, performance analysis, and security practices to strengthen and evaluate the system we first created.
