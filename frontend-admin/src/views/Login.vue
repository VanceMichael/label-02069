<template>
  <div class="login-page">
    <div class="login-card">
      <div class="card-header">
        <svg width="44" height="44" viewBox="0 0 48 48" fill="none">
          <rect rx="12" width="48" height="48" fill="#0A2E36"/>
          <path d="M24 12L14 18v12l10 6 10-6V18L24 12z" stroke="#C9A96E" stroke-width="2" fill="none"/>
          <circle cx="24" cy="24" r="4" fill="#C9A96E"/>
        </svg>
        <h1>管理后台</h1>
        <p>链信 · 区块链二手交易平台</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" hide-required-asterisk>
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="管理员账号" :prefix-icon="UserFilled" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" :prefix-icon="Lock" size="large" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" class="login-btn" @click="handleLogin">登 录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { UserFilled, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await userStore.login(form)
    if (res.data.user.role !== 'ADMIN') { ElMessage.error('无管理员权限'); userStore.logout(); return }
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } finally { loading.value = false }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  display: flex; align-items: center; justify-content: center;
  background: linear-gradient(160deg, #0A2E36 0%, #0D3B45 40%, #134E5E 100%);
  position: relative;
  &::before {
    content: ''; position: absolute; inset: 0;
    background: radial-gradient(ellipse at 30% 20%, rgba(#C9A96E, 0.06) 0%, transparent 50%),
                radial-gradient(ellipse at 70% 80%, rgba(#6366F1, 0.04) 0%, transparent 50%);
  }
}

.login-card {
  width: 400px; padding: 44px; background: white; border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.25); position: relative; z-index: 1;
}

.card-header {
  text-align: center; margin-bottom: 36px;
  h1 { font-family: "Noto Serif SC", serif; font-size: 24px; font-weight: 700; color: #0A2E36; margin: 16px 0 6px; }
  p { color: #9CA3AF; font-size: 13px; }
}

.login-btn { width: 100%; height: 48px; font-size: 16px; }
</style>
