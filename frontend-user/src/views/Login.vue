<template>
  <div class="login-page">
    <div class="login-left">
      <div class="brand-area">
        <div class="brand-logo">
          <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
            <rect rx="12" width="48" height="48" fill="#0A2E36"/>
            <path d="M24 12L14 18v12l10 6 10-6V18L24 12z" stroke="#C9A96E" stroke-width="2" fill="none"/>
            <circle cx="24" cy="24" r="4" fill="#C9A96E"/>
          </svg>
        </div>
        <h1 class="brand-title">链信</h1>
        <p class="brand-slogan">区块链赋能的可信二手交易平台</p>
        <div class="brand-features">
          <div class="feature-item">
            <span class="feature-icon">🔗</span>
            <span>商品信息链上存证，不可篡改</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">🛡️</span>
            <span>交易全程可追溯，安全可信</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">✨</span>
            <span>基于 FISCO BCOS 联盟链技术</span>
          </div>
        </div>
      </div>
    </div>

    <div class="login-right">
      <div class="form-container">
        <h2>欢迎回来</h2>
        <p class="form-subtitle">登录您的账号继续交易</p>

        <el-form ref="formRef" :model="form" :rules="rules" class="login-form" hide-required-asterisk>
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="用户名" size="large" :prefix-icon="UserFilled" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="密码" size="large" :prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" :loading="loading" class="submit-btn" @click="handleLogin">登 录</el-button>
          </el-form-item>
        </el-form>

        <div class="form-footer">
          <span>还没有账号？</span>
          <router-link to="/register">立即注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { UserFilled, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  await formRef.value.validate()
  loading.value = true
  try {
    await userStore.login(form)
    ElMessage.success('登录成功')
    router.push(route.query.redirect || '/home')
  } finally { loading.value = false }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  display: flex;
}

.login-left {
  flex: 1;
  background: linear-gradient(160deg, #0A2E36 0%, #134E5E 50%, #1A6B7A 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    width: 500px; height: 500px;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(#C9A96E, 0.08) 0%, transparent 70%);
    top: -100px; right: -100px;
  }
}

.brand-area {
  color: white;
  max-width: 400px;
  position: relative;
  z-index: 1;

  .brand-logo { margin-bottom: 24px; }
  .brand-title {
    font-family: "Noto Serif SC", serif;
    font-size: 42px;
    font-weight: 700;
    margin-bottom: 12px;
    color: #C9A96E;
  }
  .brand-slogan {
    font-size: 16px;
    opacity: 0.8;
    margin-bottom: 48px;
    line-height: 1.6;
  }
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 20px;

  .feature-item {
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 14px;
    opacity: 0.85;
    .feature-icon { font-size: 20px; }
  }
}

.login-right {
  width: 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  background: white;
}

.form-container {
  width: 100%;
  max-width: 360px;

  h2 {
    font-family: "Noto Serif SC", serif;
    font-size: 28px;
    font-weight: 700;
    color: #1A1A1A;
    margin-bottom: 8px;
  }
  .form-subtitle {
    color: #9CA3AF;
    margin-bottom: 36px;
  }
}

.submit-btn { width: 100%; height: 48px; font-size: 16px; }

.form-footer {
  text-align: center;
  margin-top: 24px;
  color: #9CA3AF;
  font-size: 14px;
  a { color: #C9A96E; font-weight: 500; margin-left: 4px; }
}
</style>
