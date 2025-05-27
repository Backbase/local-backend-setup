#!/bin/sh

#-v Remove named volumes declared in the "volumes" section of the Compose file and anonymous volumes attached to containers
docker-compose -f docker-compose.yaml down -v

#-docker volume rm -f backbase_mysql_data

docker system prune -a -f --volumes
docker volume prune --all

docker-compose up -d