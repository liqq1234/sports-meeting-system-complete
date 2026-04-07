# 高校体育运动会管理系统

> 基于 Spring Cloud Alibaba + Vue 2 的微服务架构体育运动会管理系统

## 项目简介

本系统为高校体育运动会提供完整的管理解决方案，涵盖运动会创建、项目管理、运动员报名、赛程编排、成绩管理、数据统计等核心功能。支持微服务架构和单体架构两种部署模式。

## 技术栈

### 后端
| 技术 | 说明 |
|------|------|
| Spring Boot 2.7 | 基础框架 |
| Spring Cloud 2021.0.8 | 微服务框架 |
| Spring Cloud Alibaba 2021.0.5.0 | 阿里云生态 |
| MyBatis Plus 3.5.3 | ORM 框架 |
| MySQL 8.0 | 关系数据库 |
| Redis 7.0 | 缓存与会话 |
| Nacos 2.2.3 | 服务注册与配置中心 |
| JWT (jjwt 0.9.1) | 身份认证 |
| Knife4j 4.1.0 | API 文档 |
| EasyExcel 3.2.1 | Excel 导入导出 |

### 前端
| 技术 | 说明 |
|------|------|
| Vue 2.6.14 | 渐进式框架 |
| Element UI 2.15.14 | UI 组件库 |
| Vue Router 3.5.1 | 路由管理 |
| Vuex 3.6.2 | 状态管理 |
| Axios 0.27.2 | HTTP 客户端 |
| ECharts 5.4.3 | 数据可视化 |

## 项目结构

```
sports-meeting-system/
├── frontend/                     # Vue 前端 (端口 9090)
├── backend/                      # 单体后端 (端口 8089)
├── ssms-gateway/                 # API 网关 (端口 8080)
├── ssms-auth/                    # 认证服务 (端口 8081)
├── ssms-sports-service/          # 核心业务服务 (端口 8082)
├── ssms-medical-service/         # 医疗服务 (端口 8084)
├── ssms-logistics-service/       # 物流服务 (端口 8083)
├── infra/
│   ├── mysql/init/              # 数据库初始化脚本
│   └── nacos/                   # Nacos 配置文件
├── docker-compose.yml           # Docker 编排配置
├── start-microservices.bat      # 微服务架构启动脚本
├── start-standalone.bat          # 单体架构启动脚本
└── stop-all.bat                  # 停止所有服务
```

## 快速启动

### 方式一：单体架构（推荐用于演示/开发）

1. **启动基础设施（MySQL + Redis）**
   ```bash
   docker-compose up -d mysql redis
   ```

2. **启动后端服务**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

3. **启动前端**
   ```bash
   cd frontend
   npm install
   npm run serve
   ```

4. **访问** http://localhost:9090

### 方式二：微服务架构（完整部署）

1. **双击 `start-microservices.bat`** 或手动执行：
   ```bash
   docker-compose up -d
   cd ssms-gateway && mvn spring-boot:run &
   cd ssms-auth && mvn spring-boot:run &
   cd ssms-sports-service && mvn spring-boot:run &
   cd ssms-medical-service && mvn spring-boot:run &
   cd ssms-logistics-service && mvn spring-boot:run &
   cd frontend && npm run serve
   ```

2. **访问** http://localhost:9090

## 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 裁判员 | referee01 | 123456 |
| 运动员 | 2024001 | 123456 |

## 功能模块

### 核心功能
- **运动会管理** - 创建/编辑运动会，设置报名时间，状态流转
- **比赛项目管理** - 田赛/径赛/趣味赛，性别限制，成绩类型
- **报名管理** - 运动员报名，审核（通过/驳回），批量审核
- **赛程管理** - 编排比赛时间/场地/分组，自动生成赛程
- **成绩管理** - 录入→确认→公布，排名计算，积分统计
- **场地管理** - 场地 CRUD，状态管理

### 辅助功能
- **通知公告** - 系统公告，WebSocket 实时推送
- **消息中心** - 个人消息，未读提醒
- **数据统计** - 报名统计，学院排名，TOP10 运动员
- **医疗保障** - 就诊记��管理
- **后勤物资** - 物资库管理，物资发放申请

## 系统架构图

```
                            ┌─────────────┐
                            │   Browser   │
                            └──────┬──────┘
                                   │ http://localhost:9090
                            ┌──────▼──────┐
                            │   Vue.js    │  (前端)
                            │  Element UI │
                            └──────┬──────┘
                                   │ /api/*
                            ┌──────▼──────┐
                            │   Gateway   │  (端口 8080)
                            │   JWT 鉴权   │
                            └──────┬──────┘
                                   │
          ┌────────┬────────┬──────┴──────┬────────┬────────┐
          │        │        │             │        │        │
    ┌─────▼────┐ ┌▼────────▼┐ ┌───────────▼┐ ┌──────▼──┐ ┌──▼──────┐
    │ ssms-auth│ │ ssms-    │ │ ssms-sports│ │ ssms-   │ │ ssms-   │
    │ :8081    │ │ gateway  │ │ service    │ │ medical │ │logistics│
    │ 用户认证  │ │ :8080    │ │ :8082      │ │ :8084   │ │ :8083   │
    └─────┬────┘ └─────┬────┘ └──────┬──────┘ └────┬───┘ └────┬──┘
          │            │             │             │          │
    ┌─────▼────────────▼─────────────▼─────────────▼──────────▼─────┐
    │                                                             │
    │    ┌─────────┐   ┌─────────┐   ┌─────────────┐              │
    │    │  MySQL  │   │  Redis  │   │   Nacos     │              │
    │    │  :3306  │   │  :6379  │   │   :8848     │              │
    │    └─────────┘   └─────────┘   └─────────────┘              │
    └──────────────────────────────────────────────────────────────┘
                              Docker Container
```

## 端口说明

| 服务 | 端口 | 说明 |
|------|------|------|
| 前端 | 9090 | Vue 开发服务器 |
| 后端（单体） | 8089 | 单体后端 API |
| API 网关 | 8080 | 微服务统一入口 |
| 认证服务 | 8081 | 用户认证/注册 |
| 核心业务服务 | 8082 | 运动会/项目/报名/成绩 |
| 物流服务 | 8083 | 物资管理 |
| 医疗保障服务 | 8084 | 就诊记录 |
| MySQL | 3306 | 数据库 |
| Redis | 6379 | 缓存 |
| Nacos | 8848 | 注册中心/配置中心 |

## 数据库说明

通过 Docker 启动时，所有数据库表会自动初始化：

- **nacos_config** - Nacos 配置中心表
- **ssms_auth** - 用户认证数据
- **ssms_sports** - 运动会核心业务数据
- **ssms_logistics** - 物资管理数据
- **ssms_medical** - 医疗记录数据

## 常见问题

### Q: Docker 启动后 MySQL 连接失败？
A: 等待约 30 秒让 MySQL 完全初始化，然后重试。

### Q: 微服务架构下登录失败？
A: 检查 Nacos 是否正常运行：http://localhost:8848/nacos

### Q: 前端页面空白？
A: 检查浏览器控制台是否有报错，确保后端 API 可访问。

### Q: 忘记密码怎么办？
A: 数据库中 `t_user` 表的默认密码 BCrypt 值为 `123456`。

## 开发说明

### 前端开发
```bash
cd frontend
npm install
npm run serve
```

### 后端开发（单体）
```bash
cd backend
mvn spring-boot:run
```

### 后端开发（微服务）
```bash
cd ssms-auth
mvn spring-boot:run

# 新开窗口
cd ssms-sports-service
mvn spring-boot:run
```

## License

MIT