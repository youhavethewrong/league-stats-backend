# League Stats Backend ![CI status](https://circleci.com/gh/youhavethewrong/league-stats-backend.svg?style=shield&circle-token=9bfc47f96dfed9cbefc12ef4b2956cbbb610da96 "CI status")
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
