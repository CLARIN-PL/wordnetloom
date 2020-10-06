#!/bin/bash
docker-compose run jpackager create-image jpackager create-image --input wordnetloom-client-3.0/ --output . --name wordnetloom --class pl.edu.pwr.wordnetloom.client.Application --main-jar wordnetloom-client-3.0.jar --runtime-image java-runtime/

#docker-compose run jpackager create-installer deb --name wordnetloom --icon wordnetloom.png --license-file LICENSE.md --input ./wordnetloom --app-image wordnetloom --class pl.edu.pwr.wordnetloom.client.Application --output .