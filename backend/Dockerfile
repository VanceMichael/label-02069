# 构建阶段
FROM --platform=$BUILDPLATFORM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# 复制pom.xml并下载依赖
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源码并构建
COPY src ./src
RUN mvn package -DskipTests -B

# 运行阶段
FROM --platform=$TARGETPLATFORM eclipse-temurin:17-jre

WORKDIR /app

# 设置字符集和时区环境变量
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
ENV TZ=Asia/Shanghai
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai"

# 从构建阶段复制jar包
COPY --from=builder /app/target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
