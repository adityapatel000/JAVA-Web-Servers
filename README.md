# JAVA Web Servers

Three from-scratch Java implementations of a socket-based server, each built on top of raw java.net sockets to demonstrate a different concurrency model for handling client connections.

|     Implementation    | Port |            Concurrency model            |        Client included          |
|-----------------------|------|-----------------------------------------|---------------------------------|
| SingleThreadWebServer | 8010 | Single thread, one connection at a time | Yes                             |
| MultiThreadWebServer  | 8018 | New Thread spawned per connection       | Yes (spawns 100 client threads) |
| ThreadPool            | 8010 | Fixed-size thread pool ExecutorService  | No                              |

## Why this project

Modern frameworks (Spring Boot, etc.) hide connection handling behind an abstraction layer. This project strips that away and implements the connection-handling loop directly with `ServerSocket` and `Socket`, to explore the trade-offs between the three classic approaches to server concurrency:

- **Single-threaded** — simplest possible server; accepts and fully handles one connection before accepting the next. Easy to reason about, but a slow or misbehaving client blocks every other client.
- **Thread-per-connection** — spawns a new `Thread` for every accepted connection so clients are served concurrently. Simple and responsive under light load, but thread creation is expensive and unbounded thread counts can exhaust system resources under heavy load.
- **Thread pool** — uses a fixed-size `ExecutorService` (`Executors.newFixedThreadPool`) to reuse a bounded set of worker threads across incoming connections, avoiding the cost of unbounded thread creation while still serving clients concurrently.

## How each server works

### SingleThreadWebServer
`Server` opens a `ServerSocket` on port 8010 and loops forever: accept a connection, write a greeting, read one line from the client, acknowledge it, close the connection, then move to the next `accept()` call. `Client` connects, sends a message, reads the reply, and closes.

### MultiThreadWebServer
`Server` opens a `ServerSocket` on port 8018 and, for every accepted connection, hands the socket off to a `Consumer<Socket>` running on a brand-new `Thread`, so the accept loop is never blocked by client I/O. `Client` demonstrates load by spinning up 100 threads, each opening its own connection and printing the server's response.

### ThreadPool
`Server` is constructed with a pool size (10 by default) and creates a fixed-size `ExecutorService`. Each accepted connection on port 8010 is submitted as a task (`handleClient`) to the pool instead of getting its own dedicated thread, bounding the number of concurrently active worker threads.

## Running a server

Each folder is self-contained. From the repo root:

```bash
# Compile
javac SingleThreadWebServer/Server.java SingleThreadWebServer/Client.java

# Run the server (in one terminal)
java PROJECTS.WebServer.SingleThreadWebServer.Server

# Run the client (in another terminal)
java PROJECTS.WebServer.SingleThreadWebServer.Client
```

Swap `SingleThreadWebServer` for `MultiThreadWebServer` to try the threaded version (the client there launches 100 concurrent connections). The `ThreadPool` server can be run the same way and tested with any of the two clients above, pointed at port 8010, or with a simple `telnet localhost 8010`.

## Notes

- These are minimal, educational implementations — not HTTP servers (no request parsing, headers, or status lines) and not intended for production use.
- Ports/timeouts are hardcoded per class for simplicity.

## Possible next steps

- Add basic benchmarking (requests/sec, latency under concurrent load) to quantify the difference between the three models.
- Parse real HTTP requests/responses instead of raw line-based messages.
- Add a client for the `ThreadPool` server.
