#!/bin/bash

# 로그 파일 설정
LOG_FILE="/var/log/user-data.log"
exec > >(tee -a ${LOG_FILE}) 2>&1

echo "=== AMI 최적화 빌드 시작: $(date) ==="

# 1. 시스템 업데이트 및 필수 도구 통합 설치
echo "[1/4] 시스템 업데이트 및 필수 도구 설치..."
export DEBIAN_FRONTEND=noninteractive
apt-get update -y
apt-get install -y unzip ca-certificates curl gnupg

# 2. Docker 설치
echo "[2/4] Docker 엔진 설치..."

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

# Docker 서비스 최적화
systemctl start docker
systemctl enable docker

# ubuntu 사용자를 docker 그룹에 추가
usermod -aG docker ubuntu

# docker-compose 하이픈 호환성 링크
ln -sf /usr/libexec/docker/cli-plugins/docker-compose /usr/local/bin/docker-compose

# 3. AWS CLI v2 설치 최적화
echo "[3/4] AWS CLI v2 설치..."
cd /tmp
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip -q awscliv2.zip
./aws/install --update
rm -rf awscliv2.zip aws/

# 4. 앱 디렉토리 선점 및 권한 부여
echo "[4/4] 작업 디렉토리 준비..."
APP_DIR="/home/ubuntu/app"
mkdir -p ${APP_DIR}
chown ubuntu:ubuntu ${APP_DIR}

echo "=== AMI 도구 빌드 완료: $(aws --version) ==="
