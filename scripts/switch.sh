#!/bin/bash
APP_DIR=/home/ec2-user/app
INACTIVE_PORT=$(cat $APP_DIR/inactive_port)
ACTIVE_PORT=$(cat $APP_DIR/active_port 2>/dev/null || echo "8080")

echo "Nginx 트래픽 전환: $ACTIVE_PORT → $INACTIVE_PORT"

# Nginx proxy_pass 포트 변경
sudo sed -i "s/proxy_pass http:\/\/localhost:$ACTIVE_PORT/proxy_pass http:\/\/localhost:$INACTIVE_PORT/" /etc/nginx/nginx.conf
sudo nginx -t && sudo systemctl reload nginx

# 기존 활성 포트 프로세스 종료
OLD_PID=$(lsof -ti:$ACTIVE_PORT 2>/dev/null)
if [ -n "$OLD_PID" ]; then
  echo "기존 프로세스 종료: 포트=$ACTIVE_PORT PID=$OLD_PID"
  kill -15 $OLD_PID
fi

# 활성 포트 파일 업데이트
echo $INACTIVE_PORT > $APP_DIR/active_port
echo "전환 완료: 현재 활성 포트 → $INACTIVE_PORT"
