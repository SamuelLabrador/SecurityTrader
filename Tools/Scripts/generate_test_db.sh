#!/bin/bash

# Pull MySQL image
docker pull mysql/mysql-server:8.0

# Startup MySQL instance
docker run --name test -e MYSQL_ROOT_PASSWORD=password -p 3306:3306 -d mysql:8.0