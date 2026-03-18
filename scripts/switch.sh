#!/bin/bash
APP_DIR=/home/ec2-user/app
INACTIVE_PORT=$(cat $APP_DIR/inactive_port)
ACTIVE_PORT=$(cat $APP_DIR/active_port 2>/dev/null || echo "8080")

echo "Nginx 트래픽 전환: $ACTIVE_PORT → $INACTIVE_PORT"

# Nginx proxy_pass 포트 변경 후 reload (graceful — 처리 중인 요청 완료 후 전환)
sudo sed -i "s/proxy_pass http:\/\/localhost:$ACTIVE_PORT/proxy_pass http:\/\/localhost:$INACTIVE_PORT/" /etc/nginx/nginx.conf
sudo nginx -t && sudo nginx -s reload

# 활성 포트 파일 업데이트
echo $INACTIVE_PORT > $APP_DIR/active_port
echo "Nginx 전환 완료: 현재 활성 포트 → $INACTIVE_PORT"

# 구버전 프로세스 Graceful Shutdown (SIGTERM 후 완전 종료 대기)
OLD_PID=$(lsof -ti:$ACTIVE_PORT 2>/dev/null)
if [ -n "$OLD_PID" ]; then
  echo "구버전 프로세스 Graceful Shutdown: 포트=$ACTIVE_PORT PID=$OLD_PID"
  kill -15 $OLD_PID

  # 최대 30초 대기 (Spring Boot graceful shutdown timeout과 맞춤)
  for i in {1..30}; do
    if ! kill -0 $OLD_PID 2>/dev/null; then
      echo "구버전 프로세스 종료 완료 (${i}초)"
      break
    fi
    sleep 1
  done

  # 30초 후에도 살아있으면 강제 종료
  if kill -0 $OLD_PID 2>/dev/null; then
    echo "강제 종료: PID=$OLD_PID"
    kill -9 $OLD_PID
  fi
fi

echo "전환 완료"
