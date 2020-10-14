FROM ubuntu:20.04
MAINTAINER Tomasz Naskręt, tomasz.naskret@pwr.edu.pl

# Install Java.
RUN apt-get update && apt-get upgrade -y && apt-get -y install software-properties-common xmlstarlet unzip curl netcat

RUN apt-get install -y default-jdk

# Define Wildfly variables
ENV VERSION 20.0.1.Final
ENV INSTALL_DIR /opt
ENV WILDFLY_HOME ${INSTALL_DIR}/wildfly-${VERSION}
ENV DEPLOYMENT_DIR ${WILDFLY_HOME}/standalone/deployments/
ENV CONFIGURATION_DIR ${WILDFLY_HOME}/standalone/configuration
ENV WILDFLY_CLI $WILDFLY_HOME/bin/jboss-cli.sh

# MySql setup
ENV MYSQL_VERSION 8.0.13

RUN useradd -b /opt -m -s /bin/sh -d ${INSTALL_DIR} serveradmin && echo serveradmin:serveradmin | chpasswd

RUN curl -O https://download.jboss.org/wildfly/${VERSION}/wildfly-${VERSION}.zip \
    && unzip wildfly-${VERSION}.zip -d ${INSTALL_DIR} \
    && rm wildfly-${VERSION}.zip \
    && chown -R serveradmin:serveradmin /opt \
    && chmod a+x ${WILDFLY_HOME}/bin/standalone.sh \
    && chmod -R a+rw ${INSTALL_DIR}

USER serveradmin
RUN rm ${WILDFLY_HOME}/bin/standalone.conf
ADD standalone.conf ${WILDFLY_HOME}/bin/

RUN curl --location --output /tmp/mysql-connector-java-${MYSQL_VERSION}.jar --url http://search.maven.org/remotecontent?filepath=mysql/mysql-connector-java/${MYSQL_VERSION}/mysql-connector-java-${MYSQL_VERSION}.jar

ADD configure-elytron.cli ${CONFIGURATION_DIR}
ADD configure-mysql.cli ${CONFIGURATION_DIR}
ADD configure-ajp.cli ${CONFIGURATION_DIR}

RUN keytool -genkey -alias alias -keysize 2048 -storepass secret -keystore $CONFIGURATION_DIR/jwt.keystore \
    -keypass secret -keyalg RSA -dname "CN=localhost, OU=wildfly, O=pwr, L=Wrocław, S=Dolnyśląsk, C=PL"

RUN echo "=> Starting WildFly server" && \
        bash -c '$WILDFLY_HOME/bin/standalone.sh -c standalone-full.xml &' && \
        echo "=> Waiting for the server to boot" && \
        sleep 6 && \
        echo "=> Adding security configuration" && \
        bash -c '$WILDFLY_CLI --file=$CONFIGURATION_DIR/configure-elytron.cli' && \
        echo "=> Adding mysql configuration" && \
        bash -c '$WILDFLY_CLI --file=$CONFIGURATION_DIR/configure-mysql.cli' && \
        echo "=> Adding ajp configuration" && \
                bash -c '$WILDFLY_CLI --file=$CONFIGURATION_DIR/configure-ajp.cli' && \
        echo "=> Shutting down WildFly and Cleaning up" && \
        $WILDFLY_CLI --connect --command=":shutdown" && \
        rm -rf $WILDFLY_HOME/standalone/configuration/standalone_xml_history/ $WILDFLY_HOME/standalone/log/* && \
        rm -f /tmp/*.jar

COPY ./wordnetloom-server/target/wordnetloom-server.war ${DEPLOYMENT_DIR}

EXPOSE 8080 8081 9990 8009
ENTRYPOINT ${WILDFLY_HOME}/bin/standalone.sh -c standalone-full.xml -b=0.0.0.0 -bmanagment=0.0.0.0 -Djboss.http.port=8080 -Djboss.management.http.port=8081