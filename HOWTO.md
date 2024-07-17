# The milk problem continued

An example architecture used for managing product inventory which
highlights the use of event collaboration with [RabbitMQ](https://www.rabbitmq.com/).

## History

The milk problem first surfaced while working with a well-known grocery
store to track product inventory in real time.
The choice of database was largely driven by a non-trivial performance
requirement.
The initial solution used an _eventually consistent_ database which was
available and partition tolerant.
Read about the [CAP theorem](https://en.wikipedia.org/wiki/CAP_theorem)
to learn more about the relationship between consistency, availability,
and partition tolerance.

The challenge is that high availability comes at the cost of
consistency.
High availability databases are eventually consistent, and thus are
notorious for _dirty reads_: allowing uncommitted changes from
one transaction to affect a read in another transaction.
As a result, the grocery chain was unable to produce an accurate count
of milk on the shelves.

The below exercise introduces the reader to transactions while highlighting the challenges of dirty reads.
We then move to event collaboration with RabbitMQ while highlighting the challenges with messaging systems.

## The exercise

Start with the `TODO - DIRTY READS` items, then get the tests to pass!
- Remove dirty reads.
- Ensure the correct product quantities.

Once you're done, continue to the `TODO - MESSAGING` items and get the tests to pass.
- Reflect on automatic acknowledgement.
- Reflect on manual acknowledgement.

Here are a few links to supporting documentation.
- [Introduction](https://www.rabbitmq.com/tutorials/tutorial-one-java.html)
- [Work Queues](https://www.rabbitmq.com/tutorials/tutorial-two-java.html)

## Quick start

The below steps walk through the environment setup necessary to run the
application in both local and production environments.

### Install dependencies

1.  Install and start [Docker](https://docs.docker.com/desktop/#download-and-install).
1.  Run Docker Compose.

    ```bash
    docker-compose up
    ```

### Run migrations

1.  Migrate the test database with [Flyway](https://flywaydb.org/documentation/usage/commandline/#download-and-installation).

    ```bash
    FLYWAY_CLEAN_DISABLED=false flyway -user=milk -password=milk -url="jdbc:postgresql://localhost:5432/milk_test" -locations=filesystem:databases/milk clean migrate
    ```

1.  Migrate the development database with Flyway.

    ```bash
    FLYWAY_CLEAN_DISABLED=false flyway -user=milk -password=milk -url="jdbc:postgresql://localhost:5432/milk_development" -locations=filesystem:databases/milk clean migrate
    ```

1.  Populate development data with a product scenario.

    ```bash
    PGPASSWORD=milk psql -h'127.0.0.1' -Umilk -f applications/products-server/src/test/resources/scenarios/products.sql milk_development
    ```

### Run tests

Use Gradle to run tests. You'll see a few failures at first.

```bash
./gradlew build
```


### Run apps

1.  Use Gradle to run the products server

    ```bash
    ./gradlew applications:products-server:run
    ```

1.  Use Gradle to run the simple client

    ```bash
    ./gradlew applications:simple-http-client:run
    ```

Hope you enjoy the exercise!

Thanks,

The IC Team

© 2023 by Initial Capacity, Inc. All rights reserved.
