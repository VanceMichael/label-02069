<template>
  <el-container class="layout-container">
    <el-aside width="240px" class="layout-aside">
      <div class="sidebar-logo">
        <svg width="28" height="28" viewBox="0 0 48 48" fill="none">
          <rect rx="12" width="48" height="48" fill="#C9A96E"/>
          <path d="M24 12L14 18v12l10 6 10-6V18L24 12z" stroke="white" stroke-width="2" fill="none"/>
          <circle cx="24" cy="24" r="4" fill="white"/>
        </svg>
        <span>链信管理</span>
      </div>

      <nav class="sidebar-nav">
        <router-link v-for="item in menuItems" :key="item.path" :to="item.path" class="nav-item" :class="{ active: activeMenu === item.path }">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.title }}</span>
        </router-link>
      </nav>
    </el-aside>

    <el-container>
      <el-header class="layout-header">
        <div class="header-left">
          <h2>{{ currentTitle }}</h2>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="user-trigger">
              <el-avatar :size="32">{{ userStore.userInfo?.nickname?.charAt(0) }}</el-avatar>
              <span>{{ userStore.userInfo?.nickname }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout" :icon="Right">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowDown, Right, Odometer, UserFilled, Grid, ShoppingBag, Tickets, Cpu, Document } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const menuItems = [
  { path: '/dashboard', title: '控制台', icon: 'Odometer' },
  { path: '/users', title: '用户管理', icon: 'UserFilled' },
  { path: '/categories', title: '分类管理', icon: 'Grid' },
  { path: '/products', title: '商品管理', icon: 'ShoppingBag' },
  { path: '/orders', title: '订单管理', icon: 'Tickets' },
  { path: '/blockchain', title: '链上记录', icon: 'Cpu' },
  { path: '/logs', title: '操作日志', icon: 'Document' }
]

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta.title || '')

function handleCommand(cmd) {
  if (cmd === 'logout') {
    ElMessageBox.confirm('确定退出登录？', '提示').then(() => { userStore.logout(); router.push('/login') })
  }
}
</script>

<style lang="scss" scoped>
.layout-container { height: 100vh; }

.layout-aside {
  background: #0A2E36; display: flex; flex-direction: column; overflow: hidden;
}

.sidebar-logo {
  height: 64px; display: flex; align-items: center; justify-content: center; gap: 10px;
  border-bottom: 1px solid rgba(255,255,255,0.08);
  span { font-family: "Noto Serif SC", serif; font-size: 17px; font-weight: 700; color: #C9A96E; }
}

.sidebar-nav {
  flex: 1; padding: 12px; overflow-y: auto;
  .nav-item {
    display: flex; align-items: center; gap: 10px;
    padding: 12px 16px; border-radius: 10px; margin-bottom: 4px;
    color: rgba(255,255,255,0.55); font-size: 14px; font-weight: 500;
    text-decoration: none; transition: all 0.2s;
    .el-icon { font-size: 18px; }
    &:hover { background: rgba(255,255,255,0.06); color: rgba(255,255,255,0.85); }
    &.active { background: rgba(#C9A96E, 0.15); color: #C9A96E; }
  }
}

.layout-header {
  height: 64px; background: white; display: flex; justify-content: space-between; align-items: center;
  padding: 0 28px; border-bottom: 1px solid #F0F0F0;
  .header-left h2 { font-size: 17px; font-weight: 600; color: #0A2E36; }
}

.user-trigger {
  display: flex; align-items: center; gap: 8px; cursor: pointer;
  padding: 6px 10px; border-radius: 10px; transition: background 0.2s;
  &:hover { background: #F8F7F5; }
  span { font-size: 14px; color: #4A4A4A; }
}

.layout-main { background: #F5F5F4; overflow-y: auto; }
</style>
