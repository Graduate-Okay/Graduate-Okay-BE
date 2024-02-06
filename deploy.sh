#!/bin/bash

# 현재 구동 중인 java 종료
PID=$(sudo lsof -i TCP -P | grep java | grep LISTEN | awk '{print $2}')
echo $PID

if [ -n "$PID" ]; then
  echo "실행 중인 java 프로세스를 종료합니다 (PID: $PID)"
  sudo kill -15 $PID
  sleep 5
fi

# redis 설치
#sudo yum update
#sudo yum install -y redis6
sudo systemctl start redis6

# 시간 변경
sudo rm -rf /etc/localtime
sudo ln -s /usr/share/zoneinfo/Asia/Seoul /etc/localtime

# nginx 실행
#sudo systemctl reload nginx || sudo systemctl start nginx
sudo systemctl start nginx

# java 실행
sudo chmod 777 /home/ec2-user/deploy/graduate-ok-v2.jar
sudo nohup java -jar /home/ec2-user/deploy/graduate-ok-v2.jar > /home/ec2-user/server.log 2>&1 &