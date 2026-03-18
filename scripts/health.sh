#!/bin/bash
APP_DIR=/home/ec2-user/app
INACTIVE_PORT=$(cat $APP_DIR/inactive_port 2>/dev/null || echo "8081")

echo "헬스체크 대상 포트: $INACTIVE_PORT"

# 최대 60초 대기
for i in {1..12}; do
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:$INACTIVE_PORT/health-check)
  if [ "$STATUS" = "200" ]; then
    echo "헬스체크 성공"
    exit 0
  fi
  echo "대기 중... ($i/12)"
  sleep 5
done

echo "헬스체크 실패"
exit 1
