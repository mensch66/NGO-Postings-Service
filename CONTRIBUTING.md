# Contributing to This Service (Spring Boot)

Thank you for contributing to this VRMS microservice!  
This service is part of the larger **Volunteer Resource Management System (VRMS)** ecosystem.

Before contributing, please **also read the main contribution guide**:

**Main Contribution Guide:**  
[CONTRIBUTING.md](https://github.com/udaysingh21/Volunteer-Resource-Management-System/blob/main/CONTRIBUTING.md
)

---

## Tech Stack

- Java 17+
- Spring Boot
- Maven or Gradle (depending on the repo)
- PostgreSQL (or whichever DB your service uses)
- Docker (optional but recommended)

---

## Running the Service Locally

### 1. Install dependencies

If Maven:
```bash
mvn clean install
```

### 2. Start the service

If Maven:
```bash
mvn spring-boot:run
```

---

## Code Style Guidelines

- Follow standard Spring Boot project structure
- Use meaningful commit messages
- Add Javadoc where appropriate
- Avoid business logic in controllers
- Follow layered architecture (Controller → Service → Repository)

---

## How to Contribute

### 1. Create a feature branch
```bash
git checkout -b feature/my-change
```

### 2. Make your changes

### 3. Commit and push
```bash
git commit -am "Describe your change"
git push origin feature/my-change
```

### 4. Open a Pull Request

Open a Pull Request in this microservice's repo, **not** in VRMS.

---

## Updating in VRMS (Submodule Pointer)

After your PR is merged, update the VRMS meta-repository:
```bash
cd ../VRMS
git add service-folder-name
git commit -m "Update submodule pointer for <service-name>"
git push
```

---

**Thank you again for contributing!**