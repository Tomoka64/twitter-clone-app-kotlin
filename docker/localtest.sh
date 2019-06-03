#!/usr/bin/env bash

DOCKER_COMPOSE='docker-compose -f docker-compose.test.yml --project-name test'

$DOCKER_COMPOSE down \
&& $DOCKER_COMPOSE up -d \
&& $DOCKER_COMPOSE run dbmate 'wait' \
&& $DOCKER_COMPOSE exec migrator /bin/sh -c 'mysqldef -uroot -p$MYSQL_ROOT_PASSWORD -hmysql itemae --file=schema.sql' \
&& $DOCKER_COMPOSE exec server /bin/bash -c 'gradle test'
