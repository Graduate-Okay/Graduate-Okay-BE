#!/bin/bash

PROJECT_ROOT="/home/ec2-user/graduateokv2"
JAR_FILE_NAME="graduate-ok-v2.jar"
JAR_FILE="$PROJECT_ROOT/$JAR_FILE_NAME"
TIME_NOW=$(date +%c)
LOG="$PROJECT_ROOT/log_$TIME_NOW.log"
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

# 현재 구동 중인 애플리케이션 pid 확인
CURRENT_PID=$(pgrep -f $JAR_FILE)

# 프로세스가 켜져 있으면 종료
if [ -z $CURRENT_PID ]; then
  echo "$TIME_NOW > 현재 실행 중인 애플리케이션이 없습니다" >> $DEPLOY_LOG
else
  echo "$TIME_NOW > 실행 중인 $CURRENT_PID 애플리케이션 종료 " >> $DEPLOY_LOG
  kill -15 $CURRENT_PID
fi

# redis 설치
echo "$TIME_NOW > Redis 설치" >> $DEPLOY_LOG
sudo yum update
sudo amazon-linux-extras install -y redis6
sudo systemctl start redis

# 시간 변경
echo "$TIME_NOW > 시간 변경" >> $DEPLOY_LOG
sudo rm -rf /etc/localtime
sudo ln -s /usr/share/zoneinfo/Asia/Seoul /etc/localtime

# java kill
#sudo killall java
#sleep 5

# java 실행
echo "$TIME_NOW > $JAR_FILE 파일 실행" >> $DEPLOY_LOG
#cp $PROJECT_ROOT/build/libs/*.jar $JAR_FILE
cd $PROJECT_ROOT
nohup java -jar $JAR_FILE > $LOG 2>&1 &

#disown
#
#exit