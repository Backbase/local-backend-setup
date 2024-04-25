# This command stops and removes the containers and volumes defined in the docker-compose.yml file,
# including MySQL and its data, and then deletes the Docker network.
docker compose down -v && docker network rm rch-poc_default

docker rm $(docker ps -a -q)

colima stop

colima start --cpu 4 --memory 16 --disk 100

# start the service with bootstrap profile
docker compose --profile=bootstrap up -d --remove-orphans