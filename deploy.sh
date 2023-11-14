#!/bin/bash

PROJECT_ROOT="/home/ec2-user/graduateokv2"
JAR_FILE_NAME="graduate-ok-v2.jar"
JAR_FILE="$PROJECT_ROOT/$JAR_FILE_NAME"
TIME_NOW=$(date +%c)
LOG="$PROJECT_ROOT/log_$TIME_NOW.log"

# redis 설치
sudo yum update
sudo amazon-linux-extras install -y redis6
sudo systemctl start redis

# 시간 변경
sudo rm -rf /etc/localtime
sudo ln -s /usr/share/zoneinfo/Asia/Seoul /etc/localtime

# java kill
sudo killall java
sleep 5

# java 실행
#cp $PROJECT_ROOT/build/libs/*.jar $JAR_FILE
cd $PROJECT_ROOT
sudo chmod 764 $JAR_FILE_NAME
nohup java -jar $JAR_FILE > $LOG 2>&1 &

disown

exit