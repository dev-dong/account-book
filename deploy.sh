#!/bin/bash

# 로그 파일 설정
LOG_FILE="/var/log/user-data.log"
exec > >(tee -a ${LOG_FILE}) 2>&1

echo "=========================================="
echo "EC2 초기 설정 시작: $(date)"
echo "=========================================="

# 1. 시스템 업데이트
echo "[1/6] 시스템 업데이트 중..."
apt-get update -y
apt-get upgrade -y
apt-get install -y unzip ca-certificates curl

# 2. Docker 설치
echo "[2/6] Docker 설치 중..."

# GPS 키 설정
install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
chmod a+r /etc/apt/keyrings/docker.asc

# 저장소 등록
tee /etc/apt/sources.list.d/docker.sources <<EOF
Types: deb
URIs: https://download.docker.com/linux/ubuntu
Suites: $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}")
Components: stable
Signed-By: /etc/apt/keyrings/docker.asc
EOF

apt-get update -y
apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Docker 서비스 시작
systemctl start docker
systemctl enable docker

# ubuntu 사용자를 docker 그룹에 추가
usermod -aG docker ubuntu

echo "Docker 설치 완료: $(docker --version)"

# 3. Docker Compose 설치
echo "[3/6] Docker Compose 설치 중..."
curl -L "https://github.com/docker/compose/releases/download/v2.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose
ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose

echo "Docker Compose 설치 완료: $(docker-compose --version)"

# 4. AWS CLI 설치
echo "[4/6] AWS CLI 확인 중..."
cd /tmp
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
./aws/install
echo "AWS CLI 설치 완료: $(aws --version)"

# 5. 작업 디렉토리 생성
echo "[5/6] 작업 디렉토리 생성 중..."
APP_DIR="/home/ubuntu/app"
mkdir -p ${APP_DIR}
chown ubuntu:ubuntu ${APP_DIR}

# 6. S3에서 설정 파일 다운로드
echo "[6/6] S3에서 설정 파일 다운로드 중..."

S3_BUCKET="my-account-app-bucket-20260112"
S3_PATH="dev"

# 파일 다운로드
cd ${APP_DIR}

if [ -z "${S3_PATH}" ]; then
    # 루트에서 다운로드
    aws s3 cp s3://${S3_BUCKET}/docker-compose.yml . --region ap-northeast-2
    aws s3 cp s3://${S3_BUCKET}/.env . --region ap-northeast-2
else
    # 폴더에서 다운로드
    aws s3 sync s3://${S3_BUCKET}/${S3_PATH}/ . --region ap-northeast-2
fi

# 파일 권한 설정
chmod 600 .env
chown ubuntu:ubuntu docker-compose.yml .env

echo "설정 파일 다운로드 완료"
