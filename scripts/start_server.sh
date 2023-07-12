#!/usr/bin/bash

#run docker
cd /home/ubuntu/build/scripts
docker run -d -p 8080:8080 --env-file=../env/.env test.1.1 test
#docker run -d -p 8081:8081 --env-file=../env/.env test.1.1 test2
