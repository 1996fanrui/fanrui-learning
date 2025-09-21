# n8n + PostgreSQL + NocoDB + MinIO 完整部署

## 服务概述

本配置提供了一个完整的工作流自动化和数据管理解决方案，包含：

- **PostgreSQL**: 主数据库服务（端口 5432）
- **NocoDB**: 数据库可视化和无代码应用平台（端口 8080）
- **n8n**: 工作流自动化平台（端口 5678）
- **MinIO**: 对象存储服务（端口 9000/9001）

## 数据持久化说明

### 保留的现有数据
- ✅ **PostgreSQL**: 使用现有的 `pgsql_postgres_data` 卷
- ✅ **n8n**: 使用现有的 `./n8n_data` 目录，保留所有工作流和配置

### 新建的数据
- 🆕 **NocoDB**: 使用新的 `nocodb_data` 卷，存储 NocoDB 元数据
- 🆕 **MinIO**: 使用新的 `./minio_data` 目录，存储对象数据

## 快速启动

### 启动完整服务栈
```bash
# 启动所有服务
docker-compose -f docker-compose-full.yml up -d

# 停止所有服务
docker-compose -f docker-compose-full.yml down

# 重启服务
docker-compose -f docker-compose-full.yml restart

# 查看服务状态
docker-compose -f docker-compose-full.yml ps
```

## 服务访问信息

### Web 界面
| 服务 | 地址 | 说明 |
|------|------|------|
| n8n | http://localhost:5678 | 工作流自动化平台 |
| NocoDB | http://localhost:8080 | 数据库可视化管理 |
| MinIO Console | http://localhost:9001 | 对象存储管理界面 |

```
python3 main.py --name n8n --port 5678 \
--name minio.api --port 9000 \
--name minio --port 9001 \
--name nocodb --port 8080

http://n8n.local:5678
http://minio.api.local:9000
http://minio.local:9001
http://nocodb.local:8080
```

### 数据库连接
- **PostgreSQL**: `localhost:5432`
  - 用户名: `root`
  - 密码: `123456`
  - 主要数据库: `nocodb_db` (NocoDB元数据)

### API 端点
- **MinIO S3 API**: `localhost:9000`
- **n8n Webhook**: `localhost:5678`

## 服务间通信

所有服务都位于同一个 `app_network` 网络中，可以通过服务名相互通信：

- n8n 连接 PostgreSQL: `postgres:5432`
- NocoDB 连接 PostgreSQL: `postgres:5432` (宿主机模式)
- n8n 连接 MinIO: `minio:9000`

## 配置详情

### n8n 配置
- 时区: `Asia/Shanghai`
- 数据持久化: `./n8n_data`

### NocoDB 配置
- 管理员邮箱: `admin@example.com`
- 管理员密码: `admin123`
- 数据库: PostgreSQL (`nocodb_db`)

### MinIO 配置
- 根用户: `test`
- 根密码: `12345678`
- 数据目录: `./minio_data`

## 故障排除

### 常用命令
```bash
# 查看所有服务状态
docker-compose -f docker-compose-full.yml ps

# 查看特定服务日志
docker-compose -f docker-compose-full.yml logs [service_name]

# 查看实时日志
docker-compose -f docker-compose-full.yml logs -f [service_name]

# 重启特定服务
docker-compose -f docker-compose-full.yml restart [service_name]
```

### 端口冲突检查
```bash
# 检查端口占用
lsof -i :5432  # PostgreSQL
lsof -i :8080  # NocoDB
lsof -i :5678  # n8n
lsof -i :9000  # MinIO API
lsof -i :9001  # MinIO Console
```

### 数据库连接测试
```bash
# 测试 PostgreSQL 连接
docker exec pgsql psql -U root -c "\l"

# 测试 n8n 数据库连接
docker exec n8n wget -q -O - postgres:5432 || echo "Connection failed"
```

### 服务依赖问题
所有服务都配置了健康检查和依赖关系：
- PostgreSQL 启动后，NocoDB 和 n8n 才会启动
- 如果服务启动失败，检查日志确认依赖服务是否正常

## 备份建议

### 定期备份命令
```bash
# 备份 PostgreSQL 数据
docker exec pgsql pg_dump -U root nocodb_db > backup.sql

# 备份 n8n 数据
tar -czf n8n_backup.tar.gz n8n_data/

# 备份 MinIO 数据
tar -czf minio_backup.tar.gz minio_data/
```

## 版本信息

- PostgreSQL: 13.13
- NocoDB: latest
- n8n: latest
- MinIO: latest

## 注意事项

1. **首次启动**: NocoDB 会自动创建 `nocodb_db` 数据库和表结构
2. **数据安全**: 生产环境请修改默认密码
3. **资源使用**: 完整服务栈需要较多内存，建议 4GB+ RAM
4. **网络配置**: 确保端口 5432, 5678, 8080, 9000, 9001 未被占用