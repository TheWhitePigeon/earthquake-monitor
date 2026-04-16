## Prerequisites

- Java 17+
- Maven
- PostgreSQL 18
- Node.js + npm

## Database Setup

1. Install PostgreSQL and start the service
2. Open pgAdmin or psql shell
3. Create the database:

```sql
CREATE DATABASE earthquakedb;
```

4. The table will be auto-created by Hibernate on first run

## Backend Setup

1. Navigate to the backend folder:

```bash
cd earthquake
```

2. Open `src/main/resources/application.properties` and set your PostgreSQL password:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/earthquakedb
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD_HERE
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false
server.port=8080
```

3. Run the backend:

```bash
./mvnw spring-boot:run
```

The server starts on `http://localhost:8080`

## Frontend Setup

1. Navigate to the frontend folder:

```bash
cd earthquake-frontend
```

2. Install dependencies:

```bash
npm install
```

3. Start the development server:

```bash
npm run dev
```

The app opens at `http://localhost:5173`

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/earthquakes/fetch` | Fetch from USGS, filter, store and return |
| GET | `/api/earthquakes` | Return all stored earthquakes |
| GET | `/api/earthquakes/filter?since=2026-04-14T18:00:00Z` | Filter by time |
| DELETE | `/api/earthquakes/{id}` | Delete a specific earthquake |

## Running Tests

```bash
./mvnw test
```

5 integration tests covering fetch, filter, storage, and delete logic.

## Assumptions

- Only earthquakes with magnitude > 2.0 are stored
- Existing records are deleted before each new fetch to avoid duplicates
- The USGS API returns data from the last hour in GeoJSON format
- Time filter accepts ISO-8601 format (e.g. `2026-04-14T18:00:00Z`)

## Optional Improvements Implemented

- Global exception handler via `@RestControllerAdvice` covering API unavailability, null fields, and database errors
- Delete individual earthquake records from the frontend
- CORS configured for local frontend development