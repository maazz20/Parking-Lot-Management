# Parking Lot System

A full-stack parking lot management application built with Spring Boot and React. The backend manages parking lots, slots, tickets, and vehicle lookups, while the frontend provides a dashboard for parking operations.

## Features

- Create a parking lot with a custom name and slot count
- Park a vehicle in the nearest available slot
- Mark a vehicle as exited and calculate the parking fee
- View live occupied slot status
- Search a vehicle slot by registration number
- Find registration numbers by vehicle color
- Find slot numbers by vehicle color
- React dashboard with forms, slot map, metrics, and search tools

## Tech Stack

- Backend: Java, Spring Boot, Spring Web MVC, Spring Data JPA, Bean Validation
- Database: MySQL
- Frontend: React, Vite, Lucide React
- Build tools: Maven, npm

## Project Structure

```text
parking-lot-system/
├── frontend/                 # React + Vite frontend
├── src/main/java/            # Spring Boot backend source
├── src/main/resources/       # Backend configuration
├── src/test/java/            # Backend tests
└── pom.xml                   # Maven project file
```

## Backend Setup

1. Create a MySQL database:

```sql
CREATE DATABASE parking_lot_db;
```

2. Update database credentials in `src/main/resources/application.properties` if needed:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/parking_lot_db
spring.datasource.username=root
spring.datasource.password=maaz1234
```

3. Run the Spring Boot application:

```bash
mvn spring-boot:run
```

The backend runs on:

```text
http://localhost:8080
```

## Frontend Setup

1. Move into the frontend folder:

```bash
cd frontend
```

2. Install dependencies:

```bash
npm install
```

3. Start the React development server:

```bash
npm run dev
```

The frontend runs on:

```text
http://127.0.0.1:5173
```

The Vite dev server proxies `/api` requests to the Spring Boot backend at `http://localhost:8080`.

## API Endpoints

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/parking-lots` | Create a parking lot |
| `POST` | `/api/parking-lots/park` | Park a vehicle |
| `POST` | `/api/parking-lots/leave/{slotNumber}` | Exit a vehicle from a slot |
| `GET` | `/api/parking-lots/status` | Get occupied parking slots |
| `GET` | `/api/parking-lots/registration/{registrationNumber}` | Find slot by registration number |
| `GET` | `/api/parking-lots/color/{color}` | Find registrations by color |
| `GET` | `/api/parking-lots/color/{color}/slots` | Find slots by color |

## Example Requests

Create a parking lot:

```json
{
  "name": "Main Parking",
  "totalSlots": 10
}
```

Park a vehicle:

```json
{
  "registrationNumber": "KA-01-HH-1234",
  "color": "White"
}
```

## Testing

Run backend tests:

```bash
mvn test
```

Build the frontend:

```bash
cd frontend
npm run build
```

## Notes

- Start the backend before using the frontend features.
- MySQL must be running and the configured database must exist.
- Parking fee is calculated at `20.0` per hour, with a minimum of one hour.
