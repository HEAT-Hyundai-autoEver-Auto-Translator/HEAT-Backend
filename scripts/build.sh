#!/usr/bin/bash

#docker이미지 생성
cd /home/ubuntu/build/scripts
docker build -t test.1.1 -f ../Dockerfile ../
