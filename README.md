# Ryyde

A ride-sharing backend built as a modular Spring Boot monolith, backed by MongoDB.

Ryyde lets a driver publish a ride along a route, and lets passengers request to join
that ride. The current codebase contains the **domain foundation**: the persistent
entities, their MongoDB/GeoJSON mappings, and the status enums that drive the ride,
request, and booking lifecycles. Controllers, services, and repositories are scaffolded
by package but not yet implemented.

---

## Table of contents

- [Tech stack](#tech-stack)
- [Architecture](#architecture)
- [Project structure](#project-structure)
- [Domain model](#domain-model)
- [Enums](#enums)
- [Lifecycles](#lifecycles)
- [Getting started](#getting-started)
- [Configuration](#configuration)
- [Build and test](#build-and-test)
- [Roadmap](#roadmap)
- [Conventions](#conventions)

---

## Tech stack

| Concern            | Choice                                            |
| ------------------ | ------------------------------------------------- |
| Language           | Java 21                                           |
| Framework          | Spring Boot 4.1.1                                 |
| Build tool         | Maven (wrapper included: `mvnw` / `mvnw.cmd`)     |
| Persistence        | Spring Data MongoDB (MongoDB)                     |
| Web layer          | Spring Web MVC                                    |
| Security           | Spring Security                                   |
| Boilerplate        | Lombok (`@Data`, `@NoArgsConstructor`)            |
| Testing            | JUnit 5 + `spring-boot-starter-*-test` starters   |

Coordinates: `com.ryyde:booking:0.0.1-SNAPSHOT`.

> **Note on Spring Boot 4.x:** the MongoDB annotations in use are
> `org.springframework.data.mongodb.core.mapping.Document` and `...mapping.MongoId`,
> which are the Spring Data MongoDB 4.x import paths.

---

## Architecture

Ryyde is a **single deployable Spring Boot application** (a monolith) organised
**package-by-feature** rather than package-by-layer. Each business capability owns its
own vertical slice of the application:

```
com.ryyde.<feature>.
├── controller   # HTTP endpoints        (planned)
├── dto          # request/response DTOs (planned)
├── model        # MongoDB documents     (implemented)
├── repository   # Spring Data repos     (planned)
└── service      # business logic        (planned)
```

Cross-cutting concerns that do not belong to any single feature live under
`com.ryyde.common`:

```
com.ryyde.common.
├── enums        # shared status/type enums (implemented)
├── exception    # exception types and handlers (planned)
└── response     # shared API response wrappers (planned)
```

This gives each feature a stable boundary today while leaving room to extract a feature
into its own service later, if the monolith ever needs splitting.

**Why a monolith?** Ride matching, ride lifecycle, and bookings all read and write the
same `rides` and `bookings` data in a single transactional world. Keeping them in one
process avoids distributed coordination until there is a concrete scaling reason to split.

### Domain ownership

| Feature       | Responsibility                                                        |
| ------------- | --------------------------------------------------------------------- |
| `user`        | Passengers and drivers, including credentials and ratings             |
| `vehicle`     | Vehicles owned by drivers and their seat capacity                     |
| `ride`        | A published trip: route, schedule, seats, price, status               |
| `rideRequest` | A passenger's intent to join a ride                                   |
| `booking`     | The confirmed commercial relationship between a passenger and a ride  |
| `matching`    | Route/driver matching algorithms and the service that runs them       |

---

## Project structure

```
ryyde/
├── mvnw, mvnw.cmd              # Maven wrapper
├── pom.xml                     # Dependencies and build configuration
├── README.md
└── src/
    ├── main/
    │   ├── java/com/ryyde/
    │   │   ├── RyydeApplication.java           # @SpringBootApplication entry point
    │   │   ├── booking/
    │   │   │   ├── controller/  dto/  repository/  service/   # (planned)
    │   │   │   └── model/Booking.java
    │   │   ├── ride/
    │   │   │   ├── controller/  dto/  repository/  service/   # (planned)
    │   │   │   └── model/{Ride, Location, RoutePoint}.java
    │   │   ├── rideRequest/
    │   │   │   └── model/RideRequest.java
    │   │   ├── user/
    │   │   │   ├── controller/  dto/  repository/  service/   # (planned)
    │   │   │   └── model/User.java
    │   │   ├── vehicle/
    │   │   │   ├── controller/  dto/  repository/  service/   # (planned)
    │   │   │   └── model/Vehicle.java
    │   │   ├── matching/
    │   │   │   ├── algorithm/  service/                       # (planned)
    │   │   │   └── ...
    │   │   └── common/
    │   │       ├── enums/       # VehicleType, RideStatus, RideRequestStatus, BookingStatus
    │   │       ├── exception/   # (planned)
    │   │       └── response/    # (planned)
    │   └── resources/
    │       ├── application.yaml
    │       ├── static/
    │       └── templates/
    └── test/java/com/ryyde/RyydeApplicationTests.java
```

---

## Domain model

All entities are MongoDB documents mapped with `@Document(collection = "...")` and use a
`String` id annotated with `@MongoId`. Objects use Lombok `@Data`, so accessors, `equals`,
`hashCode`, and `toString` are generated.

### Relationships

```
User (driver) ──1:N──> Vehicle ──1:N──> Ride
   │                                    │
   └──1:N───────────────────────────────┘
                                        │
                                        ├──1:N──> RideRequest ──N:1──> User (passenger)
                                        └──1:N──> Booking     ──N:1──> User (passenger)
```

References between documents are stored as plain `String` id fields (e.g. `driverId`,
`vehicleId`, `rideId`) rather than as object references. Location values are **embedded**
(not referenced) in `Ride`, `RideRequest`, and `Booking`, because a pickup/drop-off point
is a value object owned by the ride or request that contains it.

### `User` — collection `users`

| Field          | Type      | Notes                                  |
| -------------- | --------- | -------------------------------------- |
| `id`           | `String`  | `@MongoId`                             |
| `name`         | `String`  | Display name                           |
| `email`        | `String`  | Login/contact identity                 |
| `phone`        | `String`  | Contact number                         |
| `passwordHash` | `String`  | Hashed password — never the plaintext  |
| `rating`       | `Double`  | Driver/passenger reputation score      |
| `createdAt`    | `Instant` | Account creation timestamp             |

### `Vehicle` — collection `vehicles`

| Field                | Type          | Notes                              |
| -------------------- | ------------- | ---------------------------------- |
| `id`                 | `String`      | `@MongoId`                         |
| `ownerId`            | `String`      | Id of the owning `User` (driver)   |
| `type`               | `VehicleType` | `BIKE`, `SCOOTER`, `CAR`, or `SUV` |
| `brand`              | `String`      | Manufacturer                       |
| `model`              | `String`      | Model name                         |
| `registrationNumber` | `String`      | Plate / registration identifier    |
| `seatCapacity`       | `Integer`     | Total seats available              |

### `Location` — collection `locations`

| Field         | Type       | Notes                                                    |
| ------------- | ---------- | -------------------------------------------------------- |
| `id`          | `String`   | `@MongoId`                                               |
| `type`        | `String`   | Defaults to `"Point"`, the GeoJSON type discriminator     |
| `coordinates` | `double[]` | GeoJSON order: `[longitude, latitude]`                    |

`Location` models a GeoJSON Point so it can be used with MongoDB geospatial queries and
indexes. Note that GeoJSON stores **longitude first**, the reverse of how coordinates are
usually spoken aloud.

### `RoutePoint` — collection `route_points`

| Field                 | Type     | Notes                                    |
| --------------------- | -------- | ---------------------------------------- |
| `id`                  | `String` | `@MongoId`                               |
| `latitude`            | `double` | Primitive latitude                       |
| `longitude`           | `double` | Primitive longitude                      |
| `distanceFromStartKm` | `double` | Cumulative distance from the ride origin |

`RoutePoint` is the ordered polyline of a ride, which is what makes along-route matching
possible: a passenger only needs to be picked up and dropped off at points on the driver's
path, not exactly at its endpoints.

### `Ride` — collection `rides`

| Field                  | Type               | Notes                                             |
| ---------------------- | ------------------ | ------------------------------------------------- |
| `id`                   | `String`           | `@MongoId`                                        |
| `driverId`             | `String`           | Id of the driving `User`                          |
| `vehicleId`            | `String`           | Id of the `Vehicle` used                          |
| `startLocation`        | `Location`         | Embedded GeoJSON Point                            |
| `endLocation`          | `Location`         | Embedded GeoJSON Point                            |
| `route`                | `List<RoutePoint>` | Embedded polyline of the trip                     |
| `departureTime`        | `LocalDateTime`    | Scheduled departure                               |
| `estimatedArrivalTime` | `LocalDateTime`    | ETA at the destination                            |
| `distanceKm`           | `Double`           | Total trip distance                               |
| `totalSeats`           | `Integer`          | Seats offered for the whole trip                  |
| `availableSeats`       | `Integer`          | Seats not yet booked — decreases on each booking  |
| `sharingCharge`        | `BigDecimal`       | Per-seat price shared by passengers               |
| `status`               | `RideStatus`       | Lifecycle state                                   |
| `createdAt`            | `Instant`          | Creation timestamp                                |

`availableSeats` is deliberately denormalised alongside `totalSeats` so seat availability
can be read without aggregating bookings.

### `RideRequest` — collection `ride_requests`

| Field             | Type                | Notes                                     |
| ----------------- | ------------------- | ----------------------------------------- |
| `id`              | `String`            | `@MongoId`                                |
| `rideId`          | `String`            | Target `Ride`                             |
| `passengerId`     | `String`            | Requesting `User`                         |
| `pickupLocation`  | `Location`          | Embedded; typically a point on the route  |
| `dropLocation`    | `Location`          | Embedded; typically a point on the route  |
| `estimatedCharge` | `BigDecimal`        | Quoted fare for the requested segment     |
| `status`          | `RideRequestStatus` | Lifecycle state                           |
| `createdAt`       | `Instant`           | Creation timestamp                        |

### `Booking` — collection `bookings`

| Field            | Type            | Notes                                    |
| ---------------- | --------------- | ---------------------------------------- |
| `id`             | `String`        | `@MongoId`                               |
| `rideId`         | `String`        | Booked `Ride`                            |
| `passengerId`    | `String`        | Booking `User`                           |
| `pickupLocation` | `Location`      | Embedded pickup point                    |
| `dropLocation`   | `Location`      | Embedded drop-off point                  |
| `sharingCharge`  | `BigDecimal`    | Agreed price for this seat/segment       |
| `status`         | `BookingStatus` | Lifecycle state                          |
| `createdAt`      | `Instant`       | Creation timestamp                       |

---

## Enums

Shared enums live in `com.ryyde.common.enums`.

### `VehicleType`

`BIKE`, `SCOOTER`, `CAR`, `SUV` — the classes of vehicle a driver may register.

### `RideStatus`

| Value       | Meaning                                        |
| ----------- | ---------------------------------------------- |
| `SCHEDULED` | Published and accepting bookings               |
| `ACTIVE`    | The driver is currently on the trip            |
| `COMPLETED` | The trip finished normally                     |
| `CANCELLED` | The trip was called off before completion      |

### `RideRequestStatus`

| Value       | Meaning                                       |
| ----------- | --------------------------------------------- |
| `PENDING`   | Awaiting a decision from the driver           |
| `ACCEPTED`  | Driver approved; the request can be booked    |
| `REJECTED`  | Driver declined the request                   |
| `CANCELLED` | The passenger withdrew the request            |

### `BookingStatus`

| Value       | Meaning                                       |
| ----------- | --------------------------------------------- |
| `REQUESTED` | Created, not yet confirmed                    |
| `ACCEPTED`  | Confirmed; a seat is held                     |
| `REJECTED`  | Declined by the driver                        |
| `CANCELLED` | Cancelled after creation                      |
| `COMPLETED` | The passenger completed the trip              |

---

## Lifecycles

### Ride

```
SCHEDULED ──> ACTIVE ──> COMPLETED
     │           │
     └───────────┴──> CANCELLED
```

A ride is created `SCHEDULED` and becomes `ACTIVE` once the driver departs. From either
state it may be `CANCELLED`; only an `ACTIVE` ride can reach `COMPLETED`.

### Ride request

```
PENDING ──> ACCEPTED
   │
   ├───────> REJECTED
   └───────> CANCELLED
```

### Booking

```
REQUESTED ──> ACCEPTED ──> COMPLETED
     │            │
     ├──> REJECTED └──> CANCELLED
     └──> CANCELLED
```

> These transitions are the intended semantics of the status enums. The state machine
> itself is not yet enforced in code — see [Roadmap](#roadmap).

---

## Getting started

### Prerequisites

- **Java 21** (`java -version`)
- **MongoDB** reachable at `localhost:27017` (local install, Docker, or Atlas)
- No global Maven required — use the bundled wrapper

Start MongoDB locally with Docker if you do not already have it:

```bash
docker run -d --name ryyde-mongo -p 27017:27017 mongo:7
```

### Run the application

```bash
./mvnw spring-boot:run          # macOS / Linux / Git Bash
mvnw.cmd spring-boot:run        # Windows cmd
```

The app starts on `http://localhost:8080` with the application name `ryyde`.

Because only domain models and enums exist so far, there are no HTTP endpoints to call
yet — a successful start means the context loaded and MongoDB connected.

---

## Configuration

Settings live in `src/main/resources/application.yaml`:

```yaml
spring:
  application:
    name: ryyde
```

With no MongoDB URI configured, Spring Boot falls back to its default of
`mongodb://localhost:27017/test`. Point it at a real database by adding:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/ryyde
```

or by setting the `SPRING_DATA_MONGODB_URI` environment variable. Keep credentials in
environment variables or a local, git-ignored profile — never commit them.

---

## Build and test

```bash
./mvnw clean compile     # compile
./mvnw test              # run the test suite
./mvnw clean package     # build the executable jar in target/
java -jar target/booking-0.0.1-SNAPSHOT.jar
```

The single test, `RyydeApplicationTests#contextLoads`, is a `@SpringBootTest` smoke test
that verifies the Spring context starts. It needs a reachable MongoDB instance.

---

## Roadmap

The package skeleton already reserves the following work:

1. **Repositories** — Spring Data `MongoRepository` interfaces per aggregate, plus a
   geospatial index on `Location.coordinates`.
2. **DTOs and controllers** — request/response models and REST endpoints per feature.
3. **Security** — password hashing, authentication, and authorization rules using the
   existing Spring Security dependency.
4. **Matching** — the `matching` module: route-based algorithms that find rides whose
   polyline serves a passenger's pickup and drop-off, exposed via a `matching.service`.
5. **Lifecycle enforcement** — guard the status transitions above in the service layer,
   and keep `availableSeats` consistent with the bookings that reference a ride.
6. **Shared error handling** — `common.exception` handlers and `common.response`
   wrappers for consistent API responses.

### Open design questions

- `RideRequest` and `Booking` currently model overlapping data. Decide whether a request
  and its resulting booking should be one aggregate or two distinct stages.
- Primitives (`double`, `Integer`) are used for some model fields and boxed types for
  others. Standardise on one convention before adding validation.
- `pom.xml` declares both `spring-boot-starter-mongodb` and
  `spring-boot-starter-data-mongodb`; confirm which is actually required and drop the
  redundant one.

---

## Conventions

Commit messages follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>: <short imperative summary>

<What changed and, more importantly, why.>

- Specific change
- Specific change

Co-Authored-By: Codebuff <noreply@codebuff.com>
```

Common types: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`.

Coding guidelines for this repository:

- Organise code by feature under `com.ryyde.<feature>`, not by layer.
- Domain documents live in `<feature>.model` and are named for the business concept.
- Status and type values shared across features belong in `com.ryyde.common.enums`.
- No empty placeholder classes — add a type when it has behaviour to hold.
