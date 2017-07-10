FROM java:8-jdk-alpine
LABEL organization="RENCI"
LABEL maintainer="michael_conway@unc.edu"
LABEL description="iRODS DataONE Connector event logging service"

ADD target/default-event-service-indexer-4.2.1.0-SNAPSHOT.jar /
ADD target/lib /
CMD ["java", "-jar", "default-event-service-indexer-4.2.1.0-SNAPSHOT.jar"]



# build: docker build -t diceunc/dataone-indexer:4.2.1.0 .

# run:  docker run -i  -p 8080:8080 -v /etc/irods-ext:/etc/irods-ext   -v /home/mcc/webdavcert:/tmp/cert --add-host irods420.irodslocal:172.16.250.101 diceunc/dataone-indexer:4.2.1.0
