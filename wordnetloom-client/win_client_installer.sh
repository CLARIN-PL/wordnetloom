#!/usr/bin/env bash

mvn package

powershell.exe -NoP -NonI -Command "Expand-Archive '.\target\wordnetloom-client-3.0-distribution.zip' '.\'"

powershell.exe Invoke-WebRequest https://download2.gluonhq.com/openjfx/14.0.2.1/openjfx-14.0.2.1_windows-x64_bin-jmods.zip -O openjfx-14.0.2.1_windows-x64_bin-jmods.zip 

powershell.exe -NoP -NonI -Command "Expand-Archive '.\openjfx-14.0.2.1_windows-x64_bin-jmods.zip' '.\'"

jlink --no-header-files --no-man-pages --compress=2 --strip-debug --output ".\wordnetloom-client-3.0\jdk-with-fx" --module-path javafx-jmods-14.0.2.1 --add-modules javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web,jdk.jsobject,jdk.unsupported,jdk.unsupported.desktop,jdk.xml.dom,java.se

jpackage --name WordnetClient --input wordnetloom-client-3.0 --main-jar wordnetloom-client-3.0.jar --runtime-image ".\wordnetloom-client-3.0\jdk-with-fx" --main-class pl.edu.pwr.wordnetloom.client.Application --win-shortcut --type msi

