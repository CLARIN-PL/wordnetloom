FROM openjdk:11
MAINTAINER Tomasz Naskręt, tomasz.naskret@pwr.edu.pl

RUN apt-get update && apt-get install -y unzip maven

RUN wget https://download2.gluonhq.com/openjfx/11.0.2/openjfx-11.0.2_linux-x64_bin-sdk.zip
RUN unzip openjfx-11.0.2_linux-x64_bin-sdk.zip

RUN wget https://download2.gluonhq.com/openjfx/11.0.2/openjfx-11.0.2_linux-x64_bin-jmods.zip
RUN unzip openjfx-11.0.2_linux-x64_bin-jmods.zip

RUN wget https://download2.gluonhq.com/jpackager/11/jdk.packager-linux.zip
RUN unzip jdk.packager-linux.zip -d packager
RUN chmod a+x packager/jpackager
#RUN mv jpackager /usr/local/openjdk-11/bin
#RUN mv jdk.packager.jar /usr/local/openjdk-11/jmods

RUN rm jdk.packager-linux.zip
RUN rm openjfx-11.0.2_linux-x64_bin-sdk.zip