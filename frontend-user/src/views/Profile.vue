<template>
  <div class="profile-page fade-in-up">
    <div class="profile-container">
      <div class="profile-hero">
        <div class="hero-bg"></div>
        <div class="hero-content">
          <el-avatar :size="88" :src="userStore.userInfo?.avatar">{{ userStore.userInfo?.nickname?.charAt(0) || 'U' }}</el-avatar>
          <h1>{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</h1>
          <p>@{{ userStore.userInfo?.username }}</p>
        </div>
      </div>

      <div class="balance-card">
        <div class="balance-left">
          <span class="balance-label">账户余额</span>
          <span class="balance-value">¥{{ userStore.userInfo?.balance || '0.00' }}</span>
        </div>
        <el-button type="primary" disabled>充值</el-button>
      </div>

      <div class="info-section card-base">
        <h2>个人信息</h2>
        <el-form :model="form" label-width="80px" label-position="top">
          <el-form-item label="用户名">
            <el-input :value="userStore.userInfo?.username" disabled />
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="form.nickname" placeholder="请输入昵称" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="form.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="头像">
            <div class="avatar-upload-row">
              <el-avatar :size="48" :src="form.avatar">{{ form.nickname?.charAt(0) || 'U' }}</el-avatar>
              <el-button size="small" @click="triggerAvatarUpload">更换头像</el-button>
              <span v-if="avatarUploading" class="upload-status">上传中...</span>
              <input ref="avatarInputRef" type="file" accept="image/*" style="display:none" @change="handleAvatarChange" />
            </div>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="saving" @click="handleSave">保存修改</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="account-section card-base">
        <h2>账号信息</h2>
        <div class="account-grid">
          <div class="account-item"><span class="label">注册时间</span><span class="value">{{ userStore.userInfo?.createTime }}</span></div>
          <div class="account-item"><span class="label">用户ID</span><span class="value">{{ userStore.userInfo?.id }}</span></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { updateUser, uploadFile } from '@/api/user'

const userStore = useUserStore()
const saving = ref(false)
const avatarUploading = ref(false)
const avatarInputRef = ref()
const form = reactive({ nickname: '', phone: '', avatar: '' })

function initForm() {
  if (userStore.userInfo) {
    form.nickname = userStore.userInfo.nickname || ''
    form.phone = userStore.userInfo.phone || ''
    form.avatar = userStore.userInfo.avatar || ''
  }
}

function triggerAvatarUpload() {
  avatarInputRef.value?.click()
}

async function handleAvatarChange(e) {
  const file = e.target.files?.[0]
  if (!file) return

  if (!file.type.startsWith('image/')) {
    ElMessage.error('只能上传图片文件')
    return
  }
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 5MB')
    return
  }

  avatarUploading.value = true
  try {
    const res = await uploadFile(file)
    form.avatar = res.data
    ElMessage.success('头像上传成功')
  } catch {
    ElMessage.error('头像上传失败')
  } finally {
    avatarUploading.value = false
    e.target.value = ''
  }
}

async function handleSave() {
  saving.value = true
  try {
    await updateUser(form)
    await userStore.fetchUserInfo()
    ElMessage.success('保存成功')
  } finally { saving.value = false }
}

onMounted(() => initForm())
</script>

<style lang="scss" scoped>
.profile-page { max-width: 600px; margin: 0 auto; padding: 32px; }

.profile-hero {
  position: relative; border-radius: 20px; overflow: hidden; margin-bottom: 24px;
  .hero-bg { height: 120px; background: linear-gradient(135deg, #0A2E36, #134E5E, #1A6B7A); }
  .hero-content {
    text-align: center; margin-top: -44px; padding-bottom: 24px; position: relative;
    .el-avatar { border: 4px solid white; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
    h1 { font-size: 22px; font-weight: 600; color: #1A1A1A; margin-top: 12px; }
    p { color: #9CA3AF; font-size: 14px; }
  }
}

.avatar-upload-row {
  display: flex; align-items: center; gap: 12px;
  .upload-status { font-size: 12px; color: #C9A96E; }
}

.balance-card {
  display: flex; justify-content: space-between; align-items: center;
  padding: 24px; background: linear-gradient(135deg, #0A2E36, #134E5E);
  border-radius: 16px; margin-bottom: 24px;
  .balance-label { display: block; color: rgba(255,255,255,0.7); font-size: 14px; margin-bottom: 4px; }
  .balance-value { font-family: "DM Sans"; font-size: 32px; font-weight: 700; color: #C9A96E; }
}

.info-section, .account-section {
  padding: 28px; margin-bottom: 20px;
  h2 { font-size: 17px; font-weight: 600; margin-bottom: 20px; padding-bottom: 12px; border-bottom: 1px solid #F0F0F0; }
}

.account-grid {
  .account-item {
    display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid #F8F7F5;
    &:last-child { border-bottom: none; }
    .label { color: #9CA3AF; }
    .value { color: #4A4A4A; font-weight: 500; }
  }
}
</style>
