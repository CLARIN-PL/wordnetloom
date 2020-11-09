#!/usr/bin/env bash
docker-compose run --rm maven clean
docker-compose run --rm maven package
docker-compose build wildfly