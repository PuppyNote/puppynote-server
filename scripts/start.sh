#!/bin/bash
APP_DIR=/home/ec2-user/app
LOG_DIR=/home/ec2-user/logs
REGION="ap-northeast-2"
SSM_PREFIX="/puppynote/prd"

mkdir -p $LOG_DIR

# 현재 활성 포트 확인 → 비활성 포트에 배포
ACTIVE_PORT=$(cat $APP_DIR/active_port 2>/dev/null || echo "8080")
if [ "$ACTIVE_PORT" = "8080" ]; then
  INACTIVE_PORT="8081"
else
  INACTIVE_PORT="8080"
fi

echo "활성 포트: $ACTIVE_PORT → 배포 대상 포트: $INACTIVE_PORT"
echo $INACTIVE_PORT > $APP_DIR/inactive_port

# SSM에서 환경변수 로드
get_ssm() {
  aws ssm get-parameter \
    --name "${SSM_PREFIX}/$1" \
    --with-decryption \
    --region $REGION \
    --query "Parameter.Value" \
    --output text
}

echo "SSM Parameter Store에서 환경변수 로드 중..."
export DB_HOST=$(get_ssm DB_HOST)
export DB_USERNAME=$(get_ssm DB_USERNAME)
export DB_PASSWORD=$(get_ssm DB_PASSWORD)
export JWT_SECRET_KEY=$(get_ssm JWT_SECRET_KEY)
export REDIS_HOST=$(get_ssm REDIS_HOST)
export REDIS_PORT=$(get_ssm REDIS_PORT)
export REDIS_PASSWORD=$(get_ssm REDIS_PASSWORD)
export EMAIL_PASSWORD=$(get_ssm EMAIL_PASSWORD)
export AWS_ACCESS_KEY=$(get_ssm AWS_ACCESS_KEY)
export AWS_SECRET_KEY=$(get_ssm AWS_SECRET_KEY)
export ES_HOST=$(get_ssm ES_HOST)
export ES_PORT=$(get_ssm ES_PORT)
export ES_USERNAME=$(get_ssm ES_USERNAME)
export ES_PASSWORD=$(get_ssm ES_PASSWORD)
export CLOUDFRONT_DOMAIN=$(get_ssm CLOUDFRONT_DOMAIN)
echo "환경변수 로드 완료"

JAR_FILE=$(ls $APP_DIR/*.jar | head -1)
echo "시작할 JAR: $JAR_FILE (포트: $INACTIVE_PORT)"

nohup java \
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=75.0 \
  -XX:+UseG1GC \
  -Dspring.profiles.active=prd \
  -Dserver.port=$INACTIVE_PORT \
  -Duser.timezone=Asia/Seoul \
  -Djava.security.egd=file:/dev/./urandom \
  -jar $JAR_FILE \
  > $LOG_DIR/app-$INACTIVE_PORT.log 2>&1 &

echo "앱 시작됨: PID=$! / 포트: $INACTIVE_PORT"
