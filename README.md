
# Project Overview: Real-Time Product Inventory Management System

## Introduction
This project addresses the "milk problem" that emerged while collaborating with a well-known grocery store to track product inventory in real time. The initial solution employed an eventually consistent database chosen to meet stringent performance requirements. However, this approach, while ensuring high availability and partition tolerance, led to issues with inaccurate inventory counts due to eventual consistency. The project leverages robust transaction management and an event-driven architecture to ensure accurate and reliable inventory tracking.

## Relation of the Project to the CAP Theorem

### Introduction to CAP Theorem
The CAP theorem, proposed by computer scientist Eric Brewer, states that a distributed data store can achieve only two out of the following three guarantees at the same time:
- **Consistency**: Every read receives the most recent write or an error.
- **Availability**: Every request receives a response, without guarantee that it contains the most recent write.
- **Partition Tolerance**: The system continues to operate despite an arbitrary number of messages being dropped (or delayed) by the network between nodes.

Given this theorem, a system must make trade-offs depending on the specific requirements and constraints.

### The Milk Problem and CAP Theorem

1. **Initial Solution: Eventual Consistency**
   - **Choice of Database**: The project initially used an eventually consistent database. This type of database prioritizes **availability** and **partition tolerance** over consistency. In the context of the CAP theorem, this database could respond to requests and continue functioning even if parts of the network were unreachable (partition tolerance), and it would provide responses to all requests (availability).
   - **Resulting Issues**: While this approach ensured the system was highly available and could tolerate network partitions, it compromised on consistency. As a result, the system suffered from dirty reads, where uncommitted changes from one transaction were visible to other transactions. This inconsistency led to inaccurate inventory counts, particularly for tracking the quantity of milk on shelves in real time.

2. **Trade-offs Highlighted by CAP Theorem**
   - **Consistency vs. Availability**: The grocery store needed accurate, real-time inventory counts, which required consistent data. However, the choice of an eventually consistent database meant that the system could not guarantee that every read would return the most recent write, leading to inconsistencies.
   - **Partition Tolerance**: The system's ability to handle network partitions was crucial for maintaining operations across distributed locations of the grocery store. However, achieving this came at the cost of consistency.

### Solution and Improvement

To address the issues raised by the CAP theorem trade-offs, the project evolved to incorporate:

1. **Robust Transaction Management**
   - By ensuring that all operations related to inventory updates were handled within transactions, the system could guarantee atomicity, consistency, isolation, and durability (ACID properties).
   - **TransactionManager.kt**: This class was introduced to manage database transactions, ensuring that changes were committed only if all operations within the transaction were successful. This improved consistency by preventing dirty reads and ensuring that all reads reflected the most recent writes.

2. **Event-Driven Architecture**
   - **RabbitMQ Integration**: The use of RabbitMQ for messaging and event handling ensured that inventory updates were processed reliably. This approach provided a way to handle events asynchronously while maintaining a level of consistency required for accurate inventory tracking.
   - **SaferProductUpdateHandler.kt**: This enhanced message handler incorporated robust error handling and message acknowledgment, ensuring that inventory updates were processed reliably and consistently.

## Key Components

1. **Data Classes**
   - **PurchaseInfo.kt**: Represents purchase information with fields for product ID, name, and purchase amount.
   - **ProductInfo.kt**: Represents product information with fields for product ID, name, and quantity, along with methods to increment and decrement the quantity.
   - **ProductRecord.kt**: Represents a product record in the database with fields for product ID, name, and quantity.

2. **Database Interaction**
   - **DatabaseSupport.kt**: Provides utility functions to create and configure a data source using HikariCP.
   - **DatabaseTemplate.kt**: Offers a template for common database operations such as creating, querying, updating, and deleting records.
   - **TransactionManager.kt**: Manages database transactions to ensure data consistency and integrity during operations.

3. **Data Gateway**
   - **ProductDataGateway.kt**: Acts as the data gateway for product-related database operations, including creating, finding, updating, and decrementing product records. It ensures transactional integrity using `TransactionManager`.

4. **Service Layer**
   - **ProductService.kt**: Handles product-related business logic, interacting with `ProductDataGateway` for database operations. It includes methods for finding products, updating inventory based on purchases, and handling exceptions for not found products and insufficient stock.

5. **Messaging and Event Handling**
   - **BasicRabbitConfiguration.kt**: Configures RabbitMQ components such as exchanges, queues, and bindings.
   - **BasicRabbitListener.kt**: Starts a RabbitMQ listener to consume messages from a queue and delegates handling to provided callbacks.
   - **ChannelDeliverCallback.kt**: An interface defining methods for handling message deliveries and setting channels.
   - **CancelCallback.kt**: Implements `CancelCallback` for handling message consumption cancellations.
   - **ProductUpdateHandler.kt**: Handles product update events by processing incoming messages and updating the product inventory.
   - **SaferProductUpdateHandler.kt**: An enhanced version of `ProductUpdateHandler` with robust error handling and message acknowledgment.

6. **Testing Support**
   - **RabbitTestSupport.kt**: Provides utility functions for testing RabbitMQ configurations, such as purging queues and waiting for consumers.

## Summary

The project demonstrates the practical application of the CAP theorem in designing a real-time product inventory management system. Initially, the system used an eventually consistent database to prioritize availability and partition tolerance, which led to consistency issues and inaccurate inventory counts. To address these challenges, the project evolved to include robust transaction management and an event-driven architecture using RabbitMQ, striking a better balance between consistency, availability, and partition tolerance.

By integrating these improvements, the system now ensures accurate and reliable inventory tracking, maintaining consistency without sacrificing availability or partition tolerance. This evolution highlights the importance of balancing the CAP theorem's trade-offs in distributed systems, resulting in a scalable and effective solution for managing product inventory in real-time for grocery stores.


