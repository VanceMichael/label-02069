# 构建阶段
FROM --platform=$BUILDPLATFORM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

# 运行阶段
FROM --platform=$TARGETPLATFORM nginx:alpine

ENV TZ=Asia/Shanghai

COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
