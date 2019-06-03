FROM alpine:3.9

RUN wget -q -P /tmp https://github.com/k0kubun/sqldef/releases/download/v0.4.10/mysqldef_linux_amd64.tar.gz
RUN tar -xzf /tmp/mysqldef_linux_amd64.tar.gz -C /usr/local/bin mysqldef
RUN rm -rf /tmp/*

WORKDIR /home
