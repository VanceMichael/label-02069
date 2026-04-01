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
        <p class="brand-slogan">创建账号，开启可信交易之旅</p>
      </div>
    </div>

    <div class="login-right">
      <div class="form-container">
        <h2>创建账号</h2>
        <p class="form-subtitle">填写以下信息完成注册</p>

        <el-form ref="formRef" :model="form" :rules="rules" class="register-form" hide-required-asterisk>
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="用户名（3-20位）" size="large" :prefix-icon="UserFilled" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="密码（6-20位）" size="large" :prefix-icon="Lock" show-password />
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" size="large" :prefix-icon="Lock" show-password />
          </el-form-item>
          <el-form-item prop="nickname">
            <el-input v-model="form.nickname" placeholder="昵称（选填）" size="large" :prefix-icon="Avatar" />
          </el-form-item>
          <el-form-item prop="phone">
            <el-input v-model="form.phone" placeholder="手机号（选填）" size="large" :prefix-icon="Iphone" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" :loading="loading" class="submit-btn" @click="handleRegister">注 册</el-button>
          </el-form-item>
        </el-form>

        <div class="form-footer">
          <span>已有账号？</span>
          <router-link to="/login">立即登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { UserFilled, Lock, Avatar, Iphone } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { register } from '@/api/user'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '', confirmPassword: '', nickname: '', phone: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }, { min: 3, max: 20, message: '3-20位', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, max: 20, message: '6-20位', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: (_, v, cb) => v !== form.password ? cb(new Error('两次密码不一致')) : cb(), trigger: 'blur' }
  ]
}

async function handleRegister() {
  await formRef.value.validate()
  loading.value = true
  try {
    await register({ username: form.username, password: form.password, nickname: form.nickname, phone: form.phone })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } finally { loading.value = false }
}
</script>

<style lang="scss" scoped>
.login-page { min-height: 100vh; display: flex; }

.login-left {
  flex: 1;
  background: linear-gradient(160deg, #0A2E36 0%, #134E5E 50%, #1A6B7A 100%);
  display: flex; align-items: center; justify-content: center; padding: 60px;
  position: relative; overflow: hidden;
  &::before {
    content: ''; position: absolute; width: 500px; height: 500px; border-radius: 50%;
    background: radial-gradient(circle, rgba(#C9A96E, 0.08) 0%, transparent 70%);
    top: -100px; right: -100px;
  }
}

.brand-area {
  color: white; max-width: 400px; position: relative; z-index: 1;
  .brand-logo { margin-bottom: 24px; }
  .brand-title { font-family: "Noto Serif SC", serif; font-size: 42px; font-weight: 700; margin-bottom: 12px; color: #C9A96E; }
  .brand-slogan { font-size: 16px; opacity: 0.8; line-height: 1.6; }
}

.login-right {
  width: 480px; display: flex; align-items: center; justify-content: center; padding: 60px; background: white;
}

.form-container {
  width: 100%; max-width: 360px;
  h2 { font-family: "Noto Serif SC", serif; font-size: 28px; font-weight: 700; color: #1A1A1A; margin-bottom: 8px; }
  .form-subtitle { color: #9CA3AF; margin-bottom: 32px; }
}

.submit-btn { width: 100%; height: 48px; font-size: 16px; }

.form-footer {
  text-align: center; margin-top: 24px; color: #9CA3AF; font-size: 14px;
  a { color: #C9A96E; font-weight: 500; margin-left: 4px; }
}
</style>
