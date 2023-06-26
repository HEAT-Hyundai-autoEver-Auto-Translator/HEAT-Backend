#!/usr/bin/bash

#환경변수 설정
export $(cat ../env/.env | xargs)

#docker이미지 생성
cd /home/ubuntu/build/scripts
docker build -t test.1.1 -f ../Dockerfile ../
