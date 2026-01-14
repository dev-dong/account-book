#!/bin/bash

# 4. AWS CLI 설치
cd /tmp
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
./aws/install

# 5. 작업 디렉토리 생성
APP_DIR="/home/ubuntu/app"
mkdir -p ${APP_DIR}
chown ubuntu:ubuntu ${APP_DIR}

# 6. S3에서 설정 파일 다운로드
S3_BUCKET="account-app-bucket-20260114"

# 파일 다운로드
cd ${APP_DIR}
aws s3 cp s3://${S3_BUCKET}/docker-compose.yml . --region ap-northeast-2
aws s3 cp s3://${S3_BUCKET}/.env . --region ap-northeast-2

# 파일 권한 설정
chmod 600 .env
chown ubuntu:ubuntu docker-compose.yml .env

# docker start
docker compose up -d