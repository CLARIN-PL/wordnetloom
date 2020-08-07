FROM openjdk:14-jdk-buster

RUN apt-get update && apt-get install -y unzip

RUN wget https://download2.gluonhq.com/openjfx/14.0.2.1/openjfx-14.0.2.1_linux-x64_bin-jmods.zip
RUN unzip openjfx-14.0.2.1_linux-x64_bin-jmods.zip

RUN jlink --no-header-files --no-man-pages --compress=2 --strip-debug --output jdk-with-fx --module-path /javafx-jmods-14.0.2.1 --add-modules javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web,jdk.jsobject,jdk.unsupported,jdk.unsupported.desktop,jdk.xml.dom,java.se

