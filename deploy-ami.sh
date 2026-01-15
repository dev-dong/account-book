#!/bin/bash

# 1. 환경 변수 설정
APP_DIR="/home/ubuntu/app"
S3_BUCKET="account-app-bucket-20260114"

# 2. 작업 디렉토리로 이동
cd ${APP_DIR}

# 3. S3에서 최신 설정 파일 다운로드
aws s3 cp s3://${S3_BUCKET}/docker-compose.yml . --region ap-northeast-2
aws s3 cp s3://${S3_BUCKET}/.env . --region ap-northeast-2

# 4. 파일 권한 설정
chmod 600 .env
chown ubuntu:ubuntu docker-compose.yml .env

# 5. Docker 이미지 최신화 및 실행
docker compose pull
docker compose up -d