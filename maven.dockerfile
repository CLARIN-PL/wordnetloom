FROM openjdk:11
MAINTAINER Tomasz Naskręt, tomasz.naskret@pwr.edu.pl

RUN apt-get update && apt-get install -y unzip maven
RUN wget https://download2.gluonhq.com/openjfx/11.0.2/openjfx-11.0.2_linux-x64_bin-sdk.zip
RUN unzip openjfx-11.0.2_linux-x64_bin-sdk.zip