# Redis-Java

![Redis](https://img.shields.io/badge/Redis-v6.2.5-brightred.svg) ![Java](https://img.shields.io/badge/Java-v17-orange.svg)

A custom implementation of a Redis server in Java, designed to simulate the core features and functionalities of Redis, including data storage, caching, and persistence. This project serves as a learning resource for understanding distributed caching and the principles behind Redis.

## Features

- **Data Storage**: Efficient storage of key-value pairs.
- **Expiration Handling**: Support for setting expiration times on keys.
- **Persistence Options**: Save cache to a file and load it upon server startup.
- **Multi-client Support**: Handle connections from multiple clients simultaneously.

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven (for dependency management)

### Installation

1. **Clone the repository**:

   ```bash
   git clone https://github.com/dev-sandhu-harsh/Redis-Java.git
   ```

2. **Navigate to the project directory**:

   ```bash
   cd Redis-Java
   ```

3. **Build the project** (if using Maven):

   ```bash
   mvn clean install
   ```

### Running the Server

To start the Redis server, execute the following command:

```bash
java -cp target/Redis-Java-1.0-SNAPSHOT.jar com.yourpackage.RedisServer
```

Replace `com.yourpackage.RedisServer` with the actual main class path if different.

### Usage

Once the server is running, you can connect to it using any Redis client or a custom client implementation you create. Use the following commands to interact with the server:

- **SET key value**: Store a value associated with a key.
- **GET key**: Retrieve the value for a given key.
- **EXPIRE key seconds**: Set an expiration time for a key.

### Example

```bash
# Set a key
SET mykey "Hello, Redis!"

# Get the key
GET mykey
```

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Make your changes and commit them (`git commit -m 'Add new feature'`).
4. Push to the branch (`git push origin feature-branch`).
5. Create a pull request.

## Acknowledgments

- [Redis](https://redis.io/) for inspiration and guidance on caching principles.
- All contributors who have helped in making this project better.

## Contact

For any inquiries or feedback, feel free to reach out:

- **Email**: harshsandhu913@gmail.com
- **LinkedIn**: [Harsh Sandhu](https://www.linkedin.com/in/harsh-sandhu/)
- **GitHub**: [dev-sandhu-harsh](https://github.com/dev-sandhu-harsh)
