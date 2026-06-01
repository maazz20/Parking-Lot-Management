# Parking Lot Management System

A Spring Boot backend application for managing parking lots, vehicles, parking tickets, and fee calculation.

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- Hibernate
- MySQL
- Maven

## Features

- Create Parking Lot
- Park Vehicle
- Leave Vehicle
- Automatic Slot Allocation
- Parking Ticket Generation
- Fee Calculation
- Search By Registration Number
- Search By Vehicle Color
- Parking Status API
- Validation
- Global Exception Handling

## API Endpoints

POST /api/parking-lots

POST /api/parking-lots/park

POST /api/parking-lots/leave/{slotNumber}

GET /api/parking-lots/status

GET /api/parking-lots/registration/{registrationNumber}

GET /api/parking-lots/color/{color}

GET /api/parking-lots/color/{color}/slots
