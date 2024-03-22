#!/bin/sh

docker-compose down

docker volume rm -f backbase_backbase_mysql_data

docker-compose --profile=bootstrap up -d