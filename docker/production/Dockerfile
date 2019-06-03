FROM openjdk:8-jre-alpine

# same with ENTRYPOINT
ARG SNAPSHOT=itemae-boot
WORKDIR /home

COPY ./build/distributions/$SNAPSHOT.tar .
RUN tar -xf $SNAPSHOT.tar
RUN rm $SNAPSHOT.tar

ENTRYPOINT ["itemae-boot/bin/itemae"]
