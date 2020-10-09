#!/usr/bin/env bash
docker-compose run maven clean
docker-compose run maven package
docker-compose build wildfly