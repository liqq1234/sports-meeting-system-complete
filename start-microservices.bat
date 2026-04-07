@echo off
setlocal enabledelayedexpansion

:: 1. 设置工作目录为脚本所在目录
cd /d "%~dp0"

echo ==========================================
echo [体育运动会管理系统] - 微服务一键启动
echo ==========================================

:: 2. 启动基础设施 (Nacos, Redis)
echo [1/4] 正在启动基础设施 (Nacos, Redis)...
docker-compose up -d nacos redis
if %errorlevel% neq 0 (
    echo [错误] Docker-compose 启动失败，请确保 Docker 已运行
    pause
    exit /b 1
)

:: 等待 Nacos 启动 (Nacos 启动较慢，建议等待 15 秒)
echo 等待 Nacos 初始化 (15s)...
timeout /t 15 >nul

:: 3. 启动后端微服务
echo [2/4] 正在启动后端微服务...

:: 网关 (8080)
echo   - 启动网关 (Gateway)...
start "ssms-gateway" cmd /c "cd backend\ssms-gateway && title ssms-gateway && mvn spring-boot:run"
timeout /t 5 >nul

:: 认证服务 (8081)
echo   - 启动认证服务 (Auth)...
start "ssms-auth" cmd /c "cd backend\ssms-auth && title ssms-auth && mvn spring-boot:run"
timeout /t 5 >nul

:: 业务服务 (Sports, Logistics, Medical)
echo   - 启动业务服务 (Sports, Logistics, Medical)...
start "ssms-sports" cmd /c "cd backend\ssms-sports-service && title ssms-sports-service && mvn spring-boot:run"
start "ssms-logistics" cmd /c "cd backend\ssms-logistics-service && title ssms-logistics-service && mvn spring-boot:run"
start "ssms-medical" cmd /c "cd backend\ssms-medical-service && title ssms-medical-service && mvn spring-boot:run"

echo [3/4] 正在检查 Nacos 注册状态...
echo 请在浏览器访问 http://localhost:8848/nacos (nacos/nacos) 确认服务是否全部在线。

:: 4. 启动前端
echo [4/4] 正在启动前端 (Vue)...
start "ssms-frontend" cmd /c "cd frontend && title ssms-frontend && npm run serve"

echo ==========================================
echo [所有启动指令已发送]
echo - 前端: http://localhost:9090
echo - 网关: http://localhost:8080
echo - Nacos: http://localhost:8848/nacos
echo ==========================================
pause
