# League Stats Backend
A backend service that caches and serves results from the League of Legends esports API.

## Building

    $ lein do clean, test, uberjar

## Running

    $ java -jar target/league-stats-backend*-standalone.jar

## Routes
These are the routes currently supported by this service.

### Data access
These endpoints allow users to retrieve information about League of Legends tournaments.

    GET /leagues
    GET /tournaments/[league-id]
    GET /stats/[tournament-id]

### Data creation
These endpoints update the data cached in a local database to reduce upstream API load.

    POST /tournaments
    POST /stats
