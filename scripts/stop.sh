#!/bin/bash
# 배포 전 비활성 포트 프로세스 정리
INACTIVE_PORT=$(cat /home/ec2-user/app/inactive_port 2>/dev/null || echo "8081")

PID=$(lsof -ti:$INACTIVE_PORT 2>/dev/null)
if [ -n "$PID" ]; then
  echo "비활성 포트($INACTIVE_PORT) 프로세스 종료: PID=$PID"
  kill -15 $PID
  sleep 3
fi
echo "정리 완료"
