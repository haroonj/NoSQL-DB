# Decentralized Cluster-Based NoSQL DB System Documentation

## Table of Contents
1. [Overview of the System](#overview-of-the-system)
2. [Getting Started](#getting-started)
3. [Detailed Components Description](#detailed-components-description)
4. [User Guide](#user-guide)
5. [Technical Details](#technical-details)

## Overview of the System
### Project Description
The Decentralized Cluster-Based NoSQL DB System is a scalable, document-based NoSQL database designed for distributed environments. Ideal for applications that require robust and distributed data management, it offers a solution for handling large volumes of unstructured data efficiently.

### System Architecture
The system is composed of two primary types of nodes:
- **Bootstrapping Node**: This node is responsible for the initial setup of the system, handling cluster initialization, and user session management. It plays a crucial role during the system startup and when new users join the system.
- **Regular Nodes**: These nodes are responsible for the core database operations. They handle CRUD (Create, Read, Update, Delete) operations and ensure data consistency and integrity. Each node operates independently but is part of the overall cluster, allowing the system to scale horizontally as needed.

Additionally, the system uses the `NoSQL-Connector`, a custom-built dependency, to facilitate easy interaction between the user's application and the database.

## Getting Started
### Installation and Setup
To set up the database cluster, use the provided docker-compose file. This will start all the necessary components, including the Bootstrapping Node and a pre-defined number of Regular Nodes.

To run the cluster I'm using [docker compose](Final/docker-compose.yml), execute:
```bash
docker-compose up
```
### Initial Configuration
Configure the system in the application.properties file of your Spring Boot application. This involves setting the connection details for the Bootstrapping Node and defining the basic parameters for your database interaction.

Example configuration:
```properties
NoSQL.Connection.url=http://localhost:8999
NoSQL.Connection.database=MyDatabase
NoSQL.Connection.username=user123
NoSQL.Connection.password=secret
NoSQL.Connection.create=true
```

## Detailed Components Description
### Node Structure
#### Bootstrapping Node
**Components**: Includes user management services like UserService and cluster initialization components such as AppStartupListener.

**Functionality**: Responsible for managing the initial setup and ongoing user management within the system.

#### Regular Nodes
**Components**: Equipped with modules to handle CRUD operations. They include security components like JwtFilter for API protection and services for database interaction.

**Functionality**: These nodes are the workhorses of the database, managing the actual storage, retrieval, and manipulation of data.

### Database Operations
**QueryFactory**: A central class that defines various database operations. It includes methods for creating, reading, updating, and deleting databases, collections, and documents within the system.

**Flexibility**: Allows for easy expansion or modification to include more complex operations as needed.

## User Guide
### Usage Instructions
To interact with the database, define models and repositories within your project. These models represent the structure of your data, and repositories provide the methods to interact with the database.

Example Model:
```java
public class Email {
    private String _id;
    private String sender;
    private String receiver;
    // Constructor, getters, setters
}
```
```java
@Repository
public class EmailRepository extends CRUDNoSQLRepository<Email, String> {
}
```
### Interacting with the Repository
Example of creating and saving an email:
```java
Email email = new Email("from@example.com", "to@example.com", "Subject", "Body");
emailRepository.createDocument(email);
```
## Technical Details

### Data Structures and Algorithms
**Hashmaps**: Used for efficient key-value storage and retrieval, which is vital for indexing and managing large datasets in the database.

**JSON Objects**: Serve as the primary format for storing and representing documents, providing flexibility and scalability in data handling.

**Round-Robin Algorithm**: Implemented for load balancing, ensuring equitable distribution of workload across the nodes.

### Concurrency and Locking
**Optimistic Locking**: Utilized to manage concurrent write operations, enhancing the system's ability to handle simultaneous data modifications while maintaining data consistency.
**Conflict Resolution**: In case of a detected conflict during a write operation, the system retries the operation after refreshing the data, thereby avoiding potential data overwrites.


This documentation outlines the key features and functionalities of the Decentralized Cluster-Based NoSQL DB System, providing a comprehensive guide for users and developers.

