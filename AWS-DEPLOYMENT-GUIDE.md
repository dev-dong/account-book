# AWS 배포 가이드

## 아키텍처 개요

```
Internet
    ↓
  ALB (Application Load Balancer)
    ↓
Auto Scaling Group
    ↓
EC2 Instances (Private Subnet)
    ↓
RDS MySQL (Private Subnet)
```

## 1. 사전 준비 완료 항목 ✅

- [x] AMI 이미지 생성 (Docker, AWS CLI 포함)
- [x] ALB 대상 그룹 생성
- [x] Auto Scaling 그룹 생성 및 대상 그룹 연결
- [x] S3 버킷 생성: `account-app-bucket-20260114`
- [x] RDS MySQL 인스턴스 생성

## 2. 보안 그룹 설정

### 2.1 ALB 보안 그룹
```
이름: account-alb-sg
인바운드 규칙:
- Type: HTTP, Port: 80, Source: 0.0.0.0/0
- Type: HTTPS, Port: 443, Source: 0.0.0.0/0 (선택사항)
```

### 2.2 EC2 보안 그룹
```
이름: account-ec2-sg
인바운드 규칙:
- Type: Custom TCP, Port: 8080, Source: account-alb-sg
- Type: SSH, Port: 22, Source: My IP (관리용)

아웃바운드 규칙:
- All traffic (기본값 유지)
```

### 2.3 RDS 보안 그룹
```
이름: account-rds-sg
인바운드 규칙:
- Type: MySQL/Aurora, Port: 3306, Source: account-ec2-sg
```

## 3. RDS 설정

### 3.1 RDS 엔드포인트 확인
```bash
# AWS 콘솔에서 RDS → 데이터베이스 → 엔드포인트 복사
# 예: mydb.c9akciq32.ap-northeast-2.rds.amazonaws.com
```

### 3.2 데이터베이스 생성 (필요한 경우)
```sql
-- RDS에 접속하여 데이터베이스 생성
CREATE DATABASE account CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## 4. 환경 설정 파일 준비

### 4.1 .env 파일 생성
```bash
# .env.template을 복사하여 .env 생성
cp .env.template .env

# .env 파일 편집 (실제 RDS 정보로 수정)
vi .env
```

### 4.2 .env 파일 예시
```env
# RDS 설정
DB_HOST=mydb.c9akciq32.ap-northeast-2.rds.amazonaws.com
DB_PORT=3306
DB_NAME=account
DB_USER=admin
DB_PASSWORD=your-actual-password
```

### 4.3 S3에 설정 파일 업로드
```bash
# docker-compose-prod.yml과 .env를 S3에 업로드
aws s3 cp docker-compose-prod.yml s3://account-app-bucket-20260114/ --region ap-northeast-2
aws s3 cp .env s3://account-app-bucket-20260114/ --region ap-northeast-2

# 업로드 확인
aws s3 ls s3://account-app-bucket-20260114/ --region ap-northeast-2
```

## 5. IAM 역할 설정

### 5.1 EC2용 IAM 역할 생성
```
역할 이름: account-ec2-role
신뢰 관계: EC2

정책:
1. S3 읽기 권한
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "s3:GetObject",
                "s3:ListBucket"
            ],
            "Resource": [
                "arn:aws:s3:::account-app-bucket-20260114",
                "arn:aws:s3:::account-app-bucket-20260114/*"
            ]
        }
    ]
}

2. CloudWatch Logs 권한 (선택사항)
- CloudWatchLogsFullAccess
```

## 6. Launch Template 생성

### 6.1 콘솔에서 Launch Template 생성
```
이름: account-app-template
AMI: [생성한 AMI ID]
인스턴스 타입: t3.micro (또는 적절한 크기)
키 페어: [기존 키 페어 선택]
네트워크 설정:
  - 보안 그룹: account-ec2-sg
고급 세부 정보:
  - IAM 인스턴스 프로파일: account-ec2-role
  - 사용자 데이터: [아래 스크립트 복사]
```

### 6.2 User Data 스크립트
```bash
#!/bin/bash

LOG_FILE="/var/log/app-deploy.log"
exec > >(tee -a ${LOG_FILE}) 2>&1

echo "=== 애플리케이션 배포 시작: $(date) ==="

APP_DIR="/home/ubuntu/app"
S3_BUCKET="account-app-bucket-20260114"
AWS_REGION="ap-northeast-2"

cd ${APP_DIR} || exit 1

echo "[1/5] S3에서 설정 파일 다운로드 중..."
aws s3 cp s3://${S3_BUCKET}/docker-compose-prod.yml ./docker-compose.yml --region ${AWS_REGION}
aws s3 cp s3://${S3_BUCKET}/.env . --region ${AWS_REGION}

if [ ! -f ".env" ] || [ ! -f "docker-compose.yml" ]; then
    echo "ERROR: 필수 파일 다운로드 실패"
    exit 1
fi

echo "[2/5] 파일 권한 설정 중..."
chmod 600 .env
chown ubuntu:ubuntu docker-compose.yml .env

echo "[3/5] 기존 컨테이너 정리 중..."
docker compose down 2>/dev/null || true

echo "[4/5] Docker 이미지 최신화 중..."
docker compose pull

echo "[5/5] 애플리케이션 시작 중..."
docker compose up -d

sleep 10
if docker compose ps | grep -q "Up"; then
    echo "=== 배포 완료: $(date) ==="
    docker compose ps
else
    echo "ERROR: 애플리케이션 시작 실패"
    docker compose logs
    exit 1
fi
```

## 7. Auto Scaling Group 업데이트

### 7.1 Launch Template 적용
```
1. Auto Scaling 그룹 선택
2. 편집 → Launch Template 선택
3. 생성한 Launch Template 선택
4. 버전: Latest
5. 저장
```

### 7.2 인스턴스 새로고침 (Rolling Update)
```
1. Auto Scaling 그룹 → 인스턴스 새로고침 시작
2. 최소 정상 인스턴스 비율: 50%
3. 시작
```

## 8. ALB 설정

### 8.1 대상 그룹 헬스체크 설정
```
프로토콜: HTTP
경로: /health
포트: 8080
정상 임계값: 2
비정상 임계값: 3
제한 시간: 5초
간격: 30초
성공 코드: 200
```

### 8.2 ALB 리스너 규칙 확인
```
프로토콜: HTTP
포트: 80
기본 작업: 대상 그룹으로 전달 (account-target-group)
```

## 9. 배포 확인

### 9.1 헬스체크 확인
```bash
# ALB DNS 이름 확인 (AWS 콘솔에서 복사)
curl http://[ALB-DNS-NAME]/health

# 예상 응답: ok
```

### 9.2 로그 확인
```bash
# EC2 인스턴스에 SSH 접속
ssh -i your-key.pem ubuntu@[EC2-IP]

# 배포 로그 확인
sudo tail -f /var/log/app-deploy.log

# 애플리케이션 로그 확인
cd /home/ubuntu/app
docker compose logs -f
```

### 9.3 Swagger UI 접속
```
http://[ALB-DNS-NAME]/swagger-ui.html
```

## 10. 업데이트 배포 프로세스

### 10.1 애플리케이션 코드 변경 시
```bash
# 1. GitHub에 코드 푸시 (CI/CD가 자동으로 Docker Hub에 이미지 푸시)
git push origin main

# 2. Auto Scaling 그룹에서 인스턴스 새로고침 시작
# - AWS 콘솔에서 수동으로 진행
# - 또는 기존 인스턴스를 종료하면 자동으로 새 인스턴스 생성됨
```

### 10.2 환경 설정 변경 시
```bash
# 1. .env 파일 수정
vi .env

# 2. S3에 업로드
aws s3 cp .env s3://account-app-bucket-20260114/ --region ap-northeast-2

# 3. EC2 인스턴스에서 재배포
ssh -i your-key.pem ubuntu@[EC2-IP]
cd /home/ubuntu/app
aws s3 cp s3://account-app-bucket-20260114/.env . --region ap-northeast-2
docker compose down
docker compose pull
docker compose up -d
```

## 11. 모니터링 및 관리

### 11.1 CloudWatch 대시보드 설정 (선택사항)
- ALB 메트릭: TargetResponseTime, HealthyHostCount
- EC2 메트릭: CPUUtilization, NetworkIn/Out
- RDS 메트릭: DatabaseConnections, CPUUtilization

### 11.2 Auto Scaling 정책 설정 (선택사항)
```
스케일 아웃:
- 조건: CPU 사용률 > 70%
- 작업: 인스턴스 1개 추가

스케일 인:
- 조건: CPU 사용률 < 30%
- 작업: 인스턴스 1개 제거
```

## 12. 보안 체크리스트

- [ ] 모든 보안 그룹이 최소 권한 원칙을 따르는가?
- [ ] RDS는 프라이빗 서브넷에 있는가?
- [ ] .env 파일이 S3에 암호화되어 저장되는가?
- [ ] SSH 접근이 필요한 IP로만 제한되어 있는가?
- [ ] ALB에 HTTPS 리스너가 설정되어 있는가? (프로덕션 권장)
- [ ] 백업 정책이 설정되어 있는가?

## 13. 트러블슈팅

### 13.1 인스턴스가 Unhealthy 상태
```bash
# 헬스체크 엔드포인트 확인
curl http://localhost:8080/health

# Docker 컨테이너 상태 확인
docker compose ps

# 로그 확인
docker compose logs
```

### 13.2 RDS 연결 실패
```bash
# 보안 그룹 확인
# EC2 → RDS 3306 포트 허용 여부 확인

# 네트워크 연결 테스트
telnet [RDS-ENDPOINT] 3306

# 환경 변수 확인
docker compose exec app env | grep DB_
```

### 13.3 S3 파일 다운로드 실패
```bash
# IAM 역할 확인
aws sts get-caller-identity

# S3 접근 테스트
aws s3 ls s3://account-app-bucket-20260114/ --region ap-northeast-2
```

## 14. 참고 명령어

```bash
# Auto Scaling 그룹 인스턴스 목록
aws autoscaling describe-auto-scaling-groups \
  --auto-scaling-group-names account-asg \
  --region ap-northeast-2

# ALB 대상 그룹 헬스 상태
aws elbv2 describe-target-health \
  --target-group-arn [TARGET-GROUP-ARN] \
  --region ap-northeast-2

# RDS 상태 확인
aws rds describe-db-instances \
  --region ap-northeast-2
```

---

## 다음 단계 추천

1. **HTTPS 설정**: ACM에서 SSL 인증서 발급 후 ALB에 HTTPS 리스너 추가
2. **Route53 설정**: 도메인 연결
3. **CloudFront 설정**: CDN을 통한 성능 개선
4. **CI/CD 자동화**: GitHub Actions에서 Auto Scaling 인스턴스 새로고침 자동화
5. **백업 정책**: RDS 자동 백업 및 스냅샷 설정
