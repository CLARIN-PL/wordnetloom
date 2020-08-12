#!/usr/bin/env bash

mvn package
cp ./target/wordnetloom-client-3.0-distribution.zip ./
unzip wordnetloom-client-3.0-distribution.zip

curl -O https://download2.gluonhq.com/openjfx/14.0.2.1/openjfx-14.0.2.1_osx-x64_bin-jmods.zip
unzip openjfx-14.0.2.1_linux-x64_bin-jmods.zip

jlink --no-header-files --no-man-pages --compress=2 --strip-debug --output jdk-with-fx --module-path /javafx-jmods-14.0.2.1 --add-modules javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web,jdk.jsobject,jdk.unsupported,jdk.unsupported.desktop,jdk.xml.dom,java.se

cp -R jdk-with-fx ./wordnetloom-client-3.0
cd wordnetloom-client-3.0
jpackage --name WordnetClient --input ./ --main-jar wordnetloom-client-3.0.jar --runtime-image jdk-with-fx --main-class pl.edu.pwr.wordnetloom.client.Application
