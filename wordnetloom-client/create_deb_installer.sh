#!/usr/bin/env bash

mvn package
cp ./target/wordnetloom-client-3.0-distribution.zip ./
unzip wordnetloom-client-3.0-distribution.zip
docker build -t deb_installer ./
docker run -d --name deb_installer_container deb_installer
docker cp deb_installer_container:jdk-with-fx ./
cp -r ./{create_deb_installer.sh,Dockerfile,jdk-with-fx} ./wordnetloom-client-3.0
cd wordnetloom-client-3.0
jpackage --name WordnetClient --input ./ --main-jar wordnetloom-client-3.0.jar --runtime-image jdk-with-fx --main-class pl.edu.pwr.wordnetloom.client.Application --linux-shortcut --dest ./../

