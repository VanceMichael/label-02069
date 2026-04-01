# 区块链二手买卖平台

基于 FISCO BCOS 区块链的二手交易平台，实现商品发布、交易流程、链上存证等核心功能。

## How to Run

### 方式一：Docker Compose（推荐）

```bash
# 首次启动（构建镜像 + 启动）
docker-compose up --build -d

# 重新部署（清除数据）
docker-compose down -v
docker-compose up --build -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

### 方式二：本地开发

1. **启动 MySQL**
```bash
mysql -u root -p < backend/src/main/resources/schema.sql
```

2. **启动后端**
```bash
cd backend
mvn spring-boot:run
```

3. **启动用户端前端**
```bash
cd frontend-user
npm install
npm run dev
```

4. **启动管理后台前端**
```bash
cd frontend-admin
npm install
npm run dev
```

## Services

| 服务 | 端口 | 说明 |
|------|------|------|
| 用户端 | 8081 | Vue3 用户前端 |
| 管理后台 | 8082 | Vue3 管理后台 |
| 后端 API | 8080 | Spring Boot |
| MySQL | 3306 | 数据库 |

## 测试账号

### 管理后台账号

| 用户名 | 密码 | 说明 |
|--------|------|------|
| admin | 123456 | 系统管理员 |

### 用户端账号

| 用户名 | 密码 | 说明 |
|--------|------|------|
| user1 | 123456 | 普通用户-张三 |
| user2 | 123456 | 普通用户-李四 |
| user3 | 123456 | 普通用户-王五 |
| user4 | 123456 | 普通用户-赵六 |
| seller1 | 123456 | 卖家-数码小王 |
| seller2 | 123456 | 卖家-服饰达人 |
| seller3 | 123456 | 卖家-书香阁主 |

## 题目内容

实现一个基于区块链的二手买卖平台， 前端vue3,后端java, 区块链使用区块链平台是Fisco Bcos ， 上链 用的是webase平台，本地集群。 不用特别复杂， 最核心功能得有，  本科区块链专业毕业生毕业设计

## 区块链集成配置（FISCO BCOS + WeBase）

系统默认以容错模式运行：WeBase 不可用时自动生成模拟哈希，业务功能不受影响。
如需启用真实链上功能，按以下步骤配置。

### 前置条件

1. **部署 FISCO BCOS 节点集群**
   - 参考官方文档：https://fisco-bcos-documentation.readthedocs.io/
   - 推荐使用 build_chain.sh 一键搭建 4 节点联盟链
   ```bash
   bash build_chain.sh -l 127.0.0.1:4 -p 30300,20200,8545
   bash nodes/127.0.0.1/start_all.sh
   ```

2. **部署 WeBase-Front**
   - 参考官方文档：https://webasedoc.readthedocs.io/zh_CN/latest/docs/WeBASE-Front/install.html
   - WeBase-Front 默认端口 5002
   - 确认 Front 已连接到 FISCO BCOS 节点
   ```bash
   # 验证 Front 是否正常
   curl http://localhost:5002/WeBASE-Front/1/web3/blockNumber
   ```

3. **部署智能合约**
   - 通过 WeBase-Front 控制台（http://localhost:5002）部署 `contracts/SecondHandMarket.sol`
   - 或使用 WeBase-Front API 部署：
   ```bash
   curl -X POST http://localhost:5002/WeBASE-Front/contract/deploy \
     -H "Content-Type: application/json" \
     -d '{
       "groupId": 1,
       "signUserId": "<your-user-address>",
       "contractName": "SecondHandMarket",
       "contractSource": "<base64编码的合约源码>",
       "contractAbi": <contracts/abi.json的内容>,
       "bytecodeBin": "<编译后的bytecode>"
     }'
   ```
   - 部署成功后记录返回的 **合约地址**

4. **获取用户地址**
   - 在 WeBase-Front 控制台 → 私钥管理 → 新建用户
   - 记录用户的 **signUserId** 和 **address**

### 配置步骤

修改 `backend/src/main/resources/application.yml` 或通过环境变量配置：

```yaml
webase:
  front-url: http://localhost:5002          # WeBase-Front 地址
  group-id: 1                                # 群组ID
  user-address: 0xYOUR_USER_ADDRESS          # WeBase 用户地址
  contract-address: 0xYOUR_CONTRACT_ADDRESS  # 部署后的合约地址
  contract-name: SecondHandMarket            # 合约名称
  abi-path: contracts/abi.json               # ABI文件路径（classpath下）
```

Docker Compose 方式通过环境变量配置：

```yaml
# docker-compose.yml 中 backend 服务
environment:
  WEBASE_FRONT_URL: http://your-webase-host:5002
  WEBASE_GROUP_ID: 1
  WEBASE_USER_ADDRESS: 0xYOUR_USER_ADDRESS
  WEBASE_CONTRACT_ADDRESS: 0xYOUR_CONTRACT_ADDRESS
```

### 合约 ABI

ABI 文件位于 `contracts/abi.json`，同时已复制到 `backend/src/main/resources/contracts/abi.json`。
后端启动时自动从 classpath 加载，无需手动操作。

如果合约有修改，需要：
1. 重新编译合约获取新 ABI
2. 替换 `contracts/abi.json` 和 `backend/src/main/resources/contracts/abi.json`
3. 重新部署合约并更新合约地址配置

### 验证链上功能

配置完成后：
1. 发布一个商品 → 查看商品详情页的"区块链存证信息"，Tx Hash 应为真实链上哈希
2. 完成一笔交易（支付→发货→确认收货）→ 订单显示"交易已存证"
3. 点击"验证数据"按钮 → 应显示"数据验证通过，链上数据完整"

## 权限控制

- JWT Token 中包含 `role` 字段（USER/ADMIN）
- `/api/admin/*` 接口在 JWT 拦截器中校验角色，仅 ADMIN 可访问
- 非管理员访问管理接口返回 403

## 核心功能

**用户端**
- 用户注册、登录（JWT认证）
- 商品浏览、搜索、分类筛选
- 商品详情、区块链存证验证
- 下单购买、订单管理
- 个人中心

**管理后台**
- 数据统计仪表盘
- 用户管理（启用/禁用）
- 分类管理（增删改查）
- 商品管理（下架/删除）
- 订单管理
- 链上记录查看
- 操作日志

**区块链功能**
- 商品发布自动上链存证
- 交易完成自动上链存证
- 链上数据查询验证
- 数据哈希校验（链上→本地→状态三级验证）

## 技术栈

- **前端**：Vue 3 + Vite + Element Plus + Pinia + Axios + ECharts
- **后端**：Java 17 + Spring Boot 3 + MyBatis-Plus + MySQL 8.0
- **区块链**：FISCO BCOS + WeBase + Solidity 0.6.10
- **部署**：Docker + Docker Compose

## 项目结构

```
blockchain-secondhand-market/
├── backend/                    # Spring Boot 后端
├── frontend-user/              # Vue3 用户端
├── frontend-admin/             # Vue3 管理后台
├── contracts/                  # FISCO BCOS 智能合约
│   ├── SecondHandMarket.sol    # 智能合约源码
│   └── abi.json                # 合约 ABI
├── docs/                       # 设计文档
│   └── project_design.md       # 项目设计文档（架构、ER图、接口清单）
├── docker-compose.yml
├── .gitignore
├── README.md
└── label-02069.md
```
# label-02069
