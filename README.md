# Flashgrid - Java Flash Sale Engine

Flashgrid is a high-concurrency event-driven e-commerce engine designed to handle flash sales. It utilizes a caching layer for pre-check validation and a message queue for asynchronous order processing to prevent database overload during high traffic spikes.

## Architecture Structure

The system is composed of the following layers:

1.  **API Layer (Spring Boot)**
    - PurchaseController: Entry point for purchase requests.
    - Handles HTTP traffic and forwards requests to the Service layer.

2.  **Gatekeeper Service (Redis + Lua)**
    - Acts as the first line of defense.
    - Uses Atomic Lua scripts to check and decrement stock in memory.
    - Prevents invalid or sold-out requests from ever reaching the downstream systems.

3.  **Event Bus (RabbitMQ)**
    - Buffers successful purchase requests.
    - Decouples the request ingestion from the heavy order processing logic.
    - Ensures the system remains responsive even if the database is under load.

4.  **Backend Consumer (Spring Boot Service)**
    - Listens to the RabbitMQ queue.
    - Executes the actual business logic (payment, inventory persistence).

5.  **Persistence Layer (PostgreSQL)**
    - Stores the final state of products and orders.
    - Acts as the source of truth after the flash sale event settles.

## Process Flow

The lifecycle of a user purchase request is as follows:

1.  **Stock Initialization**
    - The admin calls the initialization endpoint.
    - Stock count is loaded into Redis.

2.  **Incoming Request**
    - Client sends a POST request to the buy endpoint.

3.  **Atomic Validation (Redis)**
    - The application executes a Lua script on the Redis server.
    - The script checks if stock > 0.
    - If stock is available, it decrements the Redis counter by 1 atomically.
    - If stock is 0, it returns a sold-out signal.

4.  **Circuit Breaking**
    - If Redis signals sold-out, the Controller immediately returns HTTP 429 (Too Many Requests / Sold Out) to the client.
    - No database connection is opened; no message is sent to the queue.

5.  **Async Queuing**
    - If Redis signals success, the Controller sends a "Purchase Request" message to the RabbitMQ exchange.
    - The Controller immediately returns HTTP 200 (Accepted) to the client, acknowledging the request was received.

6.  **Order Fulfillment**
    - The OrderConsumer service picks up the message from the queue.
    - It performs database operations to update the persistent inventory in PostgreSQL.

## Prerequisites

- Java 25
- Docker & Docker Compose
- Maven

## Setup and Running

1.  **Start Infrastructure**
    Run the supporting services (PostgreSQL, Redis, RabbitMQ) using Docker Compose.

    docker-compose up -d

2.  **Build Application**
    Navigate to the project directory and build using Maven.

    ./mvnw clean package

3.  **Run Application**
    Start the Spring Boot application.

    java -jar target/flashgrid-0.0.1-SNAPSHOT.jar

## API Endpoints

### 1. Initialize Stock
Populates the Redis cache for a specific product.

- **URL**: /purchase/init
- **Method**: POST
- **Query Parameters**:
    - `productId` (Long): The ID of the product.
    - `stock` (int): The quantity to set.
- **Example**:
    POST http://localhost:8080/purchase/init?productId=1&stock=1000

### 2. Buy Product
Attempts to purchase a product. This request is rate-limited by inventory availability.

- **URL**: /purchase/buy
- **Method**: POST
- **Body** (JSON):
    {
      "userId": 101,
      "productId": 1
    }
