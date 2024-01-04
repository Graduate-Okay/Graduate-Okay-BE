#!/bin/bash

PROJECT_ROOT="/home/ec2-user/graduateokv2"
JAR_FILE_NAME="graduate-ok-v2.jar"
JAR_FILE="$PROJECT_ROOT/$JAR_FILE_NAME"
TIME_NOW=$(date +%c)
FORMATTED_DATE=$(date +'%y%m%d')
LOG="$PROJECT_ROOT/log_$FORMATTED_DATE.log"
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

# 현재 구동 중인 java 종료
PID=$(sudo lsof -i TCP -P | grep java | grep LISTEN | awk '{print $2}')
echo $PID

if [ -n "$PID" ]; then
  echo "실행 중인 java 프로세스를 종료합니다 (PID: $PID)"
  sudo kill -15 $PID
  sleep 5
fi

# redis 설치
echo "$TIME_NOW > Redis 설치" >> $DEPLOY_LOG
sudo yum update
sudo yum install -y redis6
sudo systemctl start redis6

# 시간 변경
echo "$TIME_NOW > 시간 변경" >> $DEPLOY_LOG
sudo rm -rf /etc/localtime
sudo ln -s /usr/share/zoneinfo/Asia/Seoul /etc/localtime

# nginx 실행
sudo systemctl start nginx

# java 실행
echo "$TIME_NOW > $JAR_FILE_NAME 파일 실행" >> $DEPLOY_LOG
sudo chmod 777 $JAR_FILE
nohup java -jar $JAR_FILE > $LOG 2>&1 &