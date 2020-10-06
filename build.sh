#!/usr/bin/env bash
docker-compose run maven package
unzip wordnetloom-client/target/wordnetloom-client-3.0-dist.zip
docker-compose run jpackager create-installer deb --verbose --echo-mode --name wordnetloom --input ./wordnetloom-client-3.0 --main-jar ./wordnetloom-client-3.0/wordnetloom-client-3.0.jar --runtime-image java-runtime --class pl.edu.pwr.wordnetloom.client.Application --output .
