# League Stats Backend
A backend service that proxies and caches results from the League of Legends esports API.

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
