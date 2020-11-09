#!/bin/bash
docker-compose run --rm maven package
unzip wordnetloom-client/target/wordnetloom-client-3.0-dist.zip
docker-compose run --rm jlink --no-header-files --no-man-pages --compress=2 --strip-debug --module-path /javafx-jmods-11.0.2 --add-modules java.base,java.datatransfer,java.desktop,java.logging,java.management,java.naming,java.sql,java.security.sasl,java.security.jgss,java.scripting,java.xml,java.se,jdk.crypto.ec,jdk.crypto.cryptoki,jdk.net,jdk.security.auth,jdk.security.jgss,jdk.jsobject,jdk.unsupported,jdk.unsupported.desktop,jdk.xml.dom,javafx.controls,javafx.fxml,javafx.swing,javafx.web --output java-runtime
docker-compose run --rm jpackager create-image --input wordnetloom-client-3.0/ --output . --name wordnetloom --class pl.edu.pwr.wordnetloom.client.Application --main-jar wordnetloom-client-3.0.jar --runtime-image java-runtime/
rm -R wordnetloom-client-3.0
sudo rm -R java-runtime