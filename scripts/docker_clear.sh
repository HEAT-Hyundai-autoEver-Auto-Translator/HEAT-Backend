#!/usr/bin/bash

# ⭐️ 모든 컨테이너 종료
docker stop `docker ps -a -q`

# 모든 컨테이너, 이미지 삭세
yes | docker system prune -a

if [ -d /home/ubuntu/build ]; then
    sudo rm -rf /home/ubuntu/build
fi
sudo mkdir -vp /home/ubuntu/build
