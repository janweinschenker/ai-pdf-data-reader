## Java Style Guide

This document outlines the coding standards and conventions for Java projects to ensure consistency, readability, and maintainability.

## 1. Code Formatting

Use the GOOGLE code style settings for Java.

https://google.github.io/styleguide/intellij-java-google-style.xml

## 2. Comments

Structure your code in way that it is self-explanatory, but where additional explanations are needed, follow these guidelines:

- Use Javadoc for public classes and methods.
- Use single-line comments (`//`) for brief explanations.
- Use multi-line comments (`/* ... */`) for longer explanations or to comment out blocks of code.

## 3. Naming Conventions

- Classes: PascalCase (e.g., `OrderService`)
- Methods & variables: camelCase (e.g., `calculateTotal`)
- Constants: UPPER_SNAKE_CASE
- Packages: lowercase (e.g., `com.example.orders`)
- Domain objects: Use meaningful names that reflect the business domain (e.g., `Aircraft`, `Task`, `Groundtime`, `AircraftSetting`).
- DTOs: Use `Dto` suffix (e.g., `AircraftDto`, `TaskDto`).
- Rest data transfer objects: Use `ViewModel` suffix (e.g., `AircraftSettingViewModel`).
- JPA entities: Use `Entity` suffix (e.g., `AircraftEntity`, `TaskEntity`).
- MongoDb entities: Use `Document` suffix (e.g., `AircraftDocument`, `TaskDocument`).
- Inbound Adapters: Use `InAdpater` suffix (e.g., `AircraftSettingsRestInAdapter`).
- Outbound Adapters: Use `OutAdapter` suffix (e.g., `SettingModuleOutAdapter`).
- Use cases: Use `UseCase` suffix (e.g., `UpdateAircraftUseCase`, `GetAircraftUseCase`).

## 4. Architectural Style: Hexagonal Architecture (Ports and Adapters)

All Java code in this project should follow the principles of **Hexagonal Architecture** (a.k.a. Ports and Adapters). The goal is to enforce a clean separation between business logic and external systems such as databases, web frameworks, or messaging layers.

### Key Conventions:

- **Domain Core**:
    - Business logic lives in the `core` or `domain` package.
    - No dependencies on frameworks, databases, or web libraries.

- **Ports (Interfaces)**:
    - Define **inbound ports** (use cases) and **outbound ports** (interfaces to infrastructure).
    - Keep these interfaces in the domain layer.

- **Adapters (Implementations)**:
    - Put infrastructure code (e.g. REST controllers, database access, file IO) in separate `adapter` packages.
    - Implement outbound ports here.

- **Dependency Direction**:
    - The core domain must **not depend** on adapter code.
    - Use dependency inversion to allow infrastructure to plug into the core.

### Package Example:

```
src/
├── domain/ (plain data objects representing business entities)
│ 
├── adapter/
│ ├── in/ (e.g. REST, CLI)
│ │ ├── restcontroller/ (provided REST endpoints)
│ │ └── messaging/      (kafka consumer)
│ │
│ └── out/ (e.g. JPA, external APIs)
│   ├── persistence/    (JPA repositories)
│   └── messaging/      (kafka producer)
│ 
├── application/ (or use 'core', for use cases and business logic)
│ └─── port/
│     ├── in/ (intefaces for use cases used by inbound adapters)
│     └── out/ (interfaces for outbound adapters)
│ 
└── infrastructure/ (application infrastructure and configuration)
```

### AI Assistant Note:
When generating Java code, follow this structure. Business logic and interfaces should be framework-agnostic. Adapter implementations should depend on the core, not vice versa.

## 6. Variable declarations
- Use `final` for variables that should not change after initialization.