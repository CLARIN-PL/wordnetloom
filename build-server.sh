#!/usr/bin/env bash
docker-compose run --rm maven clean
docker-compose run --rm maven package
docker-compose build quarkus
docker-compose run --publish 8080:8080 -d quarkus