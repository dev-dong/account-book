#!/bin/bash

# ===================================
# EC2 User Data 스크립트
# Auto Scaling Group 또는 EC2 시작 시 자동 실행
# ===================================

# 로그 파일 설정
LOG_FILE="/var/log/app-deploy.log"
exec > >(tee -a ${LOG_FILE}) 2>&1

echo "=== 애플리케이션 배포 시작: $(date) ==="

# 환경 변수 설정
APP_DIR="/home/ubuntu/app"
S3_BUCKET="account-app-bucket-20260114"
AWS_REGION="ap-northeast-2"

# 작업 디렉토리로 이동
cd ${APP_DIR} || exit 1

echo "[1/5] S3에서 설정 파일 다운로드 중..."
# S3에서 최신 설정 파일 다운로드
aws s3 cp s3://${S3_BUCKET}/docker-compose-prod.yml ./docker-compose.yml --region ${AWS_REGION}
aws s3 cp s3://${S3_BUCKET}/.env . --region ${AWS_REGION}

# 다운로드 성공 여부 확인
if [ ! -f ".env" ] || [ ! -f "docker-compose.yml" ]; then
    echo "ERROR: 필수 파일 다운로드 실패"
    exit 1
fi

echo "[2/5] 파일 권한 설정 중..."
# 파일 권한 설정
chmod 600 .env
chown ubuntu:ubuntu docker-compose.yml .env

echo "[3/5] 기존 컨테이너 정리 중..."
# 기존 컨테이너 정리 (있는 경우)
docker compose down 2>/dev/null || true

echo "[4/5] Docker 이미지 최신화 중..."
# Docker 이미지 최신화
docker compose pull

echo "[5/5] 애플리케이션 시작 중..."
# 애플리케이션 실행
docker compose up -d

# 배포 상태 확인
sleep 10
if docker compose ps | grep -q "Up"; then
    echo "=== 배포 완료: $(date) ==="
    echo "애플리케이션이 정상적으로 시작되었습니다."
    docker compose ps
else
    echo "ERROR: 애플리케이션 시작 실패"
    docker compose logs
    exit 1
fi

echo "=== 헬스체크 URL: http://localhost:8080/health ==="
