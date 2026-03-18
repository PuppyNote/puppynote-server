#!/bin/bash
APP_DIR=/home/ec2-user/app
INACTIVE_PORT=$(cat $APP_DIR/inactive_port)
ACTIVE_PORT=$(cat $APP_DIR/active_port 2>/dev/null || echo "8080")

echo "Nginx 트래픽 전환: $ACTIVE_PORT → $INACTIVE_PORT"

# Nginx proxy_pass 포트 변경 후 reload
sudo sed -i "s/proxy_pass http:\/\/localhost:$ACTIVE_PORT/proxy_pass http:\/\/localhost:$INACTIVE_PORT/" /etc/nginx/nginx.conf
sudo nginx -t && sudo nginx -s reload

# 활성 포트 파일 업데이트
echo $INACTIVE_PORT > $APP_DIR/active_port
echo "Nginx 전환 완료: 현재 활성 포트 → $INACTIVE_PORT"
