<template>
  <el-container class="layout-container">
    <el-header class="layout-header">
      <div class="header-inner">
        <router-link to="/home" class="logo">
          <svg width="32" height="32" viewBox="0 0 48 48" fill="none">
            <rect rx="12" width="48" height="48" fill="#0A2E36"/>
            <path d="M24 12L14 18v12l10 6 10-6V18L24 12z" stroke="#C9A96E" stroke-width="2" fill="none"/>
            <circle cx="24" cy="24" r="4" fill="#C9A96E"/>
          </svg>
          <span class="logo-text">链信</span>
        </router-link>

        <nav class="nav-links">
          <router-link to="/home" class="nav-item" :class="{ active: isActive('/home') }">
            <el-icon><House /></el-icon>发现
          </router-link>
          <router-link to="/blockchain" class="nav-item" :class="{ active: isActive('/blockchain') }">
            <el-icon><Cpu /></el-icon>链上存证
          </router-link>
        </nav>

        <div class="header-actions">
          <template v-if="userStore.isLoggedIn">
            <el-button type="primary" @click="$router.push('/publish')">
              <el-icon><CirclePlus /></el-icon>发布
            </el-button>
            <div class="user-dropdown" ref="dropdownRef">
              <div class="user-trigger" @click="toggleDropdown">
                <el-avatar :size="34" :src="userStore.userInfo?.avatar">
                  {{ userStore.userInfo?.nickname?.charAt(0) || 'U' }}
                </el-avatar>
              </div>
              <Transition name="dropdown-fade">
                <div v-if="dropdownVisible" class="user-popover">
                  <div class="popover-header">
                    <el-avatar :size="42" :src="userStore.userInfo?.avatar">
                      {{ userStore.userInfo?.nickname?.charAt(0) || 'U' }}
                    </el-avatar>
                    <div class="popover-user">
                      <strong>{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</strong>
                      <span>@{{ userStore.userInfo?.username }}</span>
                    </div>
                  </div>
                  <div class="popover-balance">
                    <span class="balance-label">余额</span>
                    <span class="balance-value">¥{{ userStore.userInfo?.balance ?? '0.00' }}</span>
                  </div>
                  <div class="popover-menu">
                    <div class="menu-item" @click="goTo('profile')">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                      个人中心
                    </div>
                    <div class="menu-item" @click="goTo('my-products')">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/><path d="M16 10a4 4 0 0 1-8 0"/></svg>
                      我的商品
                    </div>
                    <div class="menu-item" @click="goTo('orders')">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="7" width="20" height="14" rx="2" ry="2"/><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/></svg>
                      我的订单
                    </div>
                  </div>
                  <div class="popover-footer">
                    <div class="menu-item logout" @click="handleLogout">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
                      退出登录
                    </div>
                  </div>
                </div>
              </Transition>
            </div>
          </template>
          <template v-else>
            <el-button @click="$router.push('/login')">登录</el-button>
            <el-button type="primary" @click="$router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </el-header>

    <el-main class="layout-main">
      <router-view />
    </el-main>

    <footer class="layout-footer">
      <div class="footer-inner">
        <span>链信 · 区块链二手交易平台</span>
        <span>基于 FISCO BCOS 联盟链</span>
      </div>
    </footer>
  </el-container>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { House, Cpu, CirclePlus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const dropdownVisible = ref(false)
const dropdownRef = ref(null)

const isActive = (path) => route.path.startsWith(path)

function toggleDropdown() { dropdownVisible.value = !dropdownVisible.value }

function goTo(path) {
  dropdownVisible.value = false
  router.push(`/${path}`)
}

function handleLogout() {
  dropdownVisible.value = false
  ElMessageBox.confirm('确定要退出登录吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消' }).then(() => {
    userStore.logout()
    ElMessage.success('已退出')
    router.push('/home')
  })
}

function onClickOutside(e) {
  if (dropdownRef.value && !dropdownRef.value.contains(e.target)) {
    dropdownVisible.value = false
  }
}

onMounted(() => document.addEventListener('click', onClickOutside))
onBeforeUnmount(() => document.removeEventListener('click', onClickOutside))
</script>

<style lang="scss" scoped>
.layout-container { min-height: 100vh; display: flex; flex-direction: column; }

.layout-header {
  height: 64px;
  background: rgba(255,255,255,0.85);
  backdrop-filter: blur(20px) saturate(180%);
  border-bottom: 1px solid #F0F0F0;
  position: sticky; top: 0; z-index: 100;
  padding: 0;
}

.header-inner {
  max-width: 1280px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 32px;
  gap: 40px;
}

.logo {
  display: flex; align-items: center; gap: 10px; text-decoration: none;
  .logo-text {
    font-family: "Noto Serif SC", serif;
    font-size: 20px; font-weight: 700; color: #0A2E36;
  }
}

.nav-links {
  display: flex; gap: 8px; flex: 1;
  .nav-item {
    display: flex; align-items: center; gap: 6px;
    padding: 8px 16px; border-radius: 10px;
    color: #6B7280; font-size: 14px; font-weight: 500;
    text-decoration: none; transition: all 0.2s;
    &:hover { background: #F8F7F5; color: #0A2E36; }
    &.active { background: #0A2E36; color: white; }
  }
}

.header-actions {
  display: flex; align-items: center; gap: 12px;
}

.user-trigger {
  cursor: pointer; padding: 2px;
  border-radius: 50%; transition: all 0.2s;
  &:hover { box-shadow: 0 0 0 3px rgba(#0A2E36, 0.1); }
}

.user-dropdown { position: relative; }

.user-popover {
  position: absolute; top: calc(100% + 10px); right: 0;
  width: 260px; background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 30px rgba(0,0,0,0.10), 0 1px 3px rgba(0,0,0,0.04);
  z-index: 200; overflow: hidden;

  .popover-header {
    display: flex; align-items: center; gap: 12px;
    padding: 20px 20px 14px;
    .popover-user {
      strong { display: block; font-size: 15px; font-weight: 600; color: #1A1A1A; }
      span { font-size: 12px; color: #9CA3AF; }
    }
  }

  .popover-balance {
    margin: 0 16px 12px;
    padding: 10px 14px;
    background: linear-gradient(135deg, #F8F7F5, #F0EDE8);
    border-radius: 10px;
    display: flex; justify-content: space-between; align-items: center;
    .balance-label { font-size: 12px; color: #9CA3AF; }
    .balance-value { font-family: "DM Sans", sans-serif; font-size: 16px; font-weight: 700; color: #0A2E36; }
  }

  .popover-menu {
    padding: 4px 8px;
  }

  .popover-footer {
    padding: 4px 8px 8px;
    border-top: 1px solid #F0F0F0;
    margin-top: 2px;
  }

  .menu-item {
    display: flex; align-items: center; gap: 10px;
    padding: 10px 12px; border-radius: 10px;
    font-size: 14px; font-weight: 500; color: #4A4A4A;
    cursor: pointer; transition: all 0.15s;
    svg { color: #9CA3AF; transition: color 0.15s; }
    &:hover { background: #F8F7F5; color: #0A2E36; svg { color: #0A2E36; } }
    &.logout { color: #EF4444; svg { color: #EF4444; }
      &:hover { background: #FEF2F2; }
    }
  }
}

.dropdown-fade-enter-active { transition: all 0.2s ease-out; }
.dropdown-fade-leave-active { transition: all 0.15s ease-in; }
.dropdown-fade-enter-from { opacity: 0; transform: translateY(-6px) scale(0.97); }
.dropdown-fade-leave-to { opacity: 0; transform: translateY(-4px) scale(0.98); }

.layout-main { flex: 1; padding: 0; background: #FAFAF9; }

.layout-footer {
  background: #0A2E36; padding: 24px 0;
  .footer-inner {
    max-width: 1280px; margin: 0 auto; padding: 0 32px;
    display: flex; justify-content: space-between;
    color: rgba(255,255,255,0.5); font-size: 13px;
  }
}
</style>
