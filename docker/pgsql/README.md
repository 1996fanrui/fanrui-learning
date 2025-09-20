# PostgreSQL + NocoDB 部署

## 服务说明

### 基础 PostgreSQL 服务
- **PostgreSQL**: 数据库服务，端口 5432
- **数据持久化**: 使用 `pgsql_postgres_data` 卷保留数据

### PostgreSQL + NocoDB 集成
- **NocoDB**: 数据库可视化和无代码应用平台，端口 8080
- **功能**: 支持 PostgreSQL 数据管理，提供表单、看板等功能

## 快速启动

### 1. 基础 PostgreSQL 服务（只启动数据库）
```bash
# 启动 PostgreSQL
docker-compose up -d

# 停止服务
docker-compose down

# 重启服务
docker-compose restart
```

### 2. PostgreSQL + NocoDB 服务
```bash
# 启动 PostgreSQL 和 NocoDB
docker-compose -f docker-compose-nocodb.yml up -d

# 停止服务
docker-compose -f docker-compose-nocodb.yml down

# 重启服务
docker-compose -f docker-compose-nocodb.yml restart
```

### 通用命令
```bash
# 查看服务状态
docker-compose ps

# 查看指定服务日志
docker-compose logs [service_name]

# 查看实时日志
docker-compose logs -f [service_name]
```

## 访问服务

### Web 界面
- **NocoDB 界面**: http://localhost:8080
  - 管理员邮箱: admin@example.com
  - 管理员密码: admin123
  - 自动连接到 nocodb_db 数据库

### 数据库连接
- **PostgreSQL**: localhost:5432
  - 用户名: root
  - 密码: 123456
  - 数据库: postgres (默认), nocodb_db

## 数据库操作

```bash
# 进入 PostgreSQL 容器
docker exec -it pgsql /bin/bash

# 连接数据库
psql -h localhost -p 5432 -U root -W

# 列出所有数据库
\l

# 切换到指定数据库
\c database_name;

# 列出当前数据库的所有表
\dt

# 查看表结构
\d table_name
```

## 配置说明

### 数据持久化
- **PostgreSQL**: 使用 `pgsql_postgres_data` 卷保留数据
- **NocoDB**: 使用 `nocodb_data` 卷保留配置

### 服务配置
- **PostgreSQL**: 用户名: root, 密码: 123456
- **NocoDB**: 使用官方推荐配置连接到 PostgreSQL
- **健康检查**: 确保 PostgreSQL 完全启动后再启动其他服务

## 故障排除

### 通用故障排除
```bash
# 查看服务状态
docker-compose ps

# 查看指定配置文件的服务状态
docker-compose -f docker-compose-nocodb.yml ps

# 查看日志
docker-compose logs postgres
docker-compose logs nocodb

# 查看实时日志
docker-compose logs -f [service_name]
```

### 常见问题

#### 1. 端口冲突
```bash
# 检查端口占用
lsof -i :5432
lsof -i :8080
```

#### 2. 数据库连接问题
```bash
# 测试数据库连接
docker exec pgsql psql -U root -c "\l"

# 重启 PostgreSQL 服务
docker-compose restart postgres
```

#### 3. 服务启动失败
```bash
# 查看详细错误日志
docker-compose logs --tail=100 [service_name]

# 清理并重新启动
docker-compose down
docker-compose up -d
```