#!/usr/bin/bash

#run docker
cd /home/ubuntu/build/scripts
docker run -d -p 80:8080 test.1.1 test
