#!/usr/bin/env bash

mvn clean install
docker-compose down
docker-compose build
docker-compose up

