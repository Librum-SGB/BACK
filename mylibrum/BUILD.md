Build and test instructions for MyLibrum (Windows)

Summary

This file explains how to run the Maven build and tests locally on Windows PowerShell and how to recover the Maven Wrapper if it's missing.

Prerequisites

- JDK installed (recommended: Java 11/17/21; use the version your project requires). Ensure `JAVA_HOME` is set and points to the JDK installation.
- Maven (optional if using the wrapper). If Maven is not available, prefer restoring the wrapper or install Maven.

Quick checks (PowerShell)

```powershell
# Check Java
java -version
# Check Maven (optional)
mvn -v
# Try the wrapper (if present)
.\mvnw.cmd -v
```

Run build (preferred: Maven Wrapper if present)

If the wrapper exists in the project root (mylibrum\mvnw.cmd):

```powershell
cd mylibrum
.\mvnw.cmd clean test
```

If you have Maven installed, run directly from repository root:

```powershell
mvn -f mylibrum/pom.xml clean test
```

Fixing the missing Maven Wrapper

If you see an error about `.mvn/wrapper/maven-wrapper.properties` missing, either recover the wrapper files from version control or generate the wrapper locally (requires Maven installed):

```powershell
cd mylibrum
mvn -N io.takari:maven:wrapper
# After generation you can run:
.\mvnw.cmd clean test
```

If you cannot install Maven but want the wrapper, copy the `.mvn` directory and `mvnw`/`mvnw.cmd` from another project or add the wrapper files from upstream/source control.

Fast recovery (download wrapper JAR):

If the wrapper scripts exist but `.mvn/wrapper/maven-wrapper.jar` is missing, run the provided helper from the repository root:

```powershell
cd mylibrum
.\fetch-maven-wrapper.ps1
.\mvnw.cmd clean test
```

Troubleshooting

- "Cannot start maven from wrapper": confirm `.mvn/wrapper/maven-wrapper.properties` exists and is readable.
- "mvn: command not found": install Maven and add it to PATH, or use the generated wrapper.
- If tests fail after compilation: paste the failing test output here and I will help triage and fix failures.

Next steps I can take for you

- Create and commit a complete Maven Wrapper (`.mvn/wrapper/*`, `mvnw`, `mvnw.cmd`) in this repository so you can run builds without installing Maven. (You must confirm if you want me to add those files.)
- Generate a Flyway/Liquibase migration template to add new columns to the DB schema.

