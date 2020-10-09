#!/bin/bash
docker-compose run maven package
unzip wordnetloom-client/target/wordnetloom-client-3.0-dist.zip
docker-compose run jlink --no-header-files --no-man-pages --compress=2 --strip-debug --module-path /javafx-jmods-11.0.2 --add-modules java.datatransfer,java.desktop,java.logging,java.management,java.naming,java.sql,java.security.jgss,java.scripting,java.xml,jdk.jsobject,jdk.unsupported,jdk.unsupported.desktop,jdk.xml.dom,javafx.controls,javafx.fxml,javafx.swing,javafx.web --output java-runtime
docker-compose run jpackager create-image jpackager create-image --input wordnetloom-client-3.0/ --output . --name wordnetloom --class pl.edu.pwr.wordnetloom.client.Application --main-jar wordnetloom-client-3.0.jar --runtime-image java-runtime/
rm -R wordnetloom-client-3.0
sudo rm -R java-runtime