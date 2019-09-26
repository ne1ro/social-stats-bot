# social-stats-bot

Messenger bot that shows some social stats.


## CI

![](https://github.com/ne1ro/social-stats-bot/workflows/ClojureCI/badge.svg)

## Getting Started

1. Start the application: `lein run`
2. Go to [localhost:8080](http://localhost:8080/) to see: `Hello World!`
3. Read your app's source code at src/social_stats_bot/service.clj. Explore the docs of functions
   that define routes and responses.
4. Run your app's tests with `lein test`. Read the tests at test/social_stats_bot/service_test.clj.


## Developing your service

1. Start a new REPL: `lein repl`
2. Start your service in dev-mode: `(def dev-serv (run-dev))`
3. Connect your editor to the running REPL session.
   Re-evaluated code will be seen immediately in the service.

### [Docker](https://www.docker.com/) container support

1. Configure your service to accept incoming connections (edit service.clj and add  ::http/host "0.0.0.0" )
2. Build an uberjar of your service: `lein uberjar`
3. Build a Docker image: `sudo docker build -t social-stats-bot .`
4. Run your Docker image: `docker run -p 8080:8080 social-stats-bot`
