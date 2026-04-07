@echo off
echo ==========================================
echo [体育运动会管理系统] - 停止所有服务
echo ==========================================

:: 1. 停止 Java 进程 (Spring Boot)
echo 正在停止所有 Java 服务...
taskkill /f /t /im java.exe

:: 2. 停止 Node 进程 (Vue Dev Server)
echo 正在停止前端服务...
taskkill /f /t /im node.exe

:: 3. 停止 Docker 容器
echo 正在停止基础设施容器...
docker-compose stop

echo ==========================================
echo [清理完毕]
echo ==========================================
pause
