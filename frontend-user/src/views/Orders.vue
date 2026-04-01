<template>
  <div class="orders-page fade-in-up">
    <div class="page-container card-base">
      <h1 class="page-title serif-title">我的订单</h1>

      <div class="filter-bar">
        <div class="type-tabs">
          <div v-for="tab in typeTabs" :key="tab.value" class="tab-item" :class="{ active: typeFilter === tab.value }" @click="selectType(tab.value)">
            {{ tab.label }}
          </div>
        </div>
        <div class="status-select-wrapper">
          <select v-model="statusFilter" class="status-select" @change="handleStatusChange">
            <option :value="null">全部状态</option>
            <option :value="0">⏳ 待支付</option>
            <option :value="1">💰 已支付</option>
            <option :value="2">🚚 已发货</option>
            <option :value="3">✅ 已完成</option>
            <option :value="4">❌ 已取消</option>
          </select>
        </div>
      </div>

      <div v-if="loading"><el-skeleton :rows="5" animated /></div>

      <div v-else-if="orders.length === 0" class="empty-state">
        <el-icon :size="48" color="#D1D5DB"><Tickets /></el-icon>
        <p>暂无订单</p>
      </div>

      <div v-else class="order-list">
        <div v-for="order in orders" :key="order.id" class="order-card">
          <div class="order-top">
            <div class="order-meta">
              <span class="order-no">{{ order.orderNo }}</span>
              <span class="order-time">{{ order.createTime }}</span>
            </div>
            <el-tag :type="getStatusType(order.status)" size="small">{{ order.statusText }}</el-tag>
          </div>

          <div class="order-body">
            <img :src="order.productImage || defaultImage" class="order-img" />
            <div class="order-info">
              <h3>{{ order.productTitle }}</h3>
              <span class="price-text">¥{{ order.amount }}</span>
            </div>
            <div class="order-parties">
              <div><span class="party-label">买家</span> {{ order.buyerName }}</div>
              <div><span class="party-label">卖家</span> {{ order.sellerName }}</div>
            </div>
          </div>

          <div v-if="order.blockchainHash" class="order-chain">
            <span class="blockchain-badge"><el-icon><Cpu /></el-icon> 交易已存证</span>
            <code>{{ order.blockchainHash }}</code>
            <button class="btn-verify" @click="verifyOrder(order)">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
              验证
            </button>
          </div>

          <div class="order-actions">
            <template v-if="isBuyer(order)">
              <el-button v-if="order.status === 0" type="primary" @click="handlePay(order)">支付</el-button>
              <el-button v-if="order.status === 0" @click="handleCancel(order)">取消</el-button>
              <el-button v-if="order.status === 2" type="primary" @click="handleConfirm(order)">确认收货</el-button>
            </template>
            <template v-if="isSeller(order)">
              <el-button v-if="order.status === 1" type="primary" @click="handleShip(order)">发货</el-button>
            </template>
          </div>
        </div>
      </div>

      <div v-if="total > 0" class="pagination">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" @change="loadOrders" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Tickets, Cpu } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderList, payOrder, shipOrder, confirmOrder, cancelOrder, verifyOrder as verifyOrderApi } from '@/api/order'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const orders = ref([])
const loading = ref(false)
const typeFilter = ref('')
const statusFilter = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const typeTabs = [
  { label: '全部', value: '' },
  { label: '我买的', value: 'buy' },
  { label: '我卖的', value: 'sell' }
]

function selectType(v) { typeFilter.value = v; currentPage.value = 1; loadOrders() }
function handleStatusChange() { currentPage.value = 1; loadOrders() }

const defaultImage = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 80 80"%3E%3Crect fill="%23F8F7F5" width="80" height="80"/%3E%3C/svg%3E'
function getStatusType(s) { return { 0: 'warning', 1: 'primary', 2: 'primary', 3: 'success', 4: 'info' }[s] || 'info' }
function isBuyer(o) { return o.buyerId === userStore.userInfo?.id }
function isSeller(o) { return o.sellerId === userStore.userInfo?.id }

async function loadOrders() {
  loading.value = true
  try {
    const res = await getOrderList({ type: typeFilter.value, status: statusFilter.value, current: currentPage.value, size: pageSize.value })
    orders.value = res.data.records; total.value = res.data.total
  } finally { loading.value = false }
  userStore.fetchUserInfo()
}

async function handlePay(o) {
  await ElMessageBox.confirm(`确定支付 ¥${o.amount} 吗？`, '确认支付')
  await payOrder(o.id); ElMessage.success('支付成功'); loadOrders(); userStore.fetchUserInfo()
}
async function handleCancel(o) {
  await ElMessageBox.confirm('确定取消该订单吗？', '提示')
  await cancelOrder(o.id); ElMessage.success('订单已取消'); loadOrders()
}
async function handleShip(o) {
  await ElMessageBox.confirm('确定发货吗？', '确认发货')
  await shipOrder(o.id); ElMessage.success('已发货'); loadOrders()
}
async function handleConfirm(o) {
  await ElMessageBox.confirm('确认收货后，交易将完成并上链存证', '确认收货')
  await confirmOrder(o.id); ElMessage.success('交易完成，已上链存证'); loadOrders(); userStore.fetchUserInfo()
}
async function verifyOrder(o) {
  const res = await verifyOrderApi(o.id)
  ElMessage[res.data ? 'success' : 'warning'](res.data ? '交易数据验证通过' : '数据验证失败')
}

onMounted(() => loadOrders())
</script>

<style lang="scss" scoped>
.orders-page { max-width: 900px; margin: 0 auto; padding: 32px; }
.page-container { padding: 28px; }
.page-title { font-size: 22px; color: #0A2E36; margin-bottom: 24px; }

.filter-bar { display: flex; gap: 16px; margin-bottom: 24px; align-items: center; }

.type-tabs {
  display: inline-flex; background: #F5F5F4; border-radius: 10px; padding: 3px;
  .tab-item {
    padding: 7px 20px; border-radius: 8px;
    font-size: 14px; font-weight: 500; color: #6B7280;
    cursor: pointer; transition: all 0.2s; user-select: none; white-space: nowrap;
    &:hover { color: #0A2E36; }
    &.active {
      background: #0A2E36; color: #fff;
      box-shadow: 0 1px 3px rgba(10, 46, 54, 0.2);
    }
  }
}

.status-select-wrapper {
  .status-select {
    appearance: none;
    -webkit-appearance: none;
    padding: 7px 32px 7px 14px;
    font-size: 14px; font-weight: 500; color: #4A4A4A;
    background: #F5F5F4 url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='%239CA3AF' stroke-width='2.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'%3E%3C/polyline%3E%3C/svg%3E") no-repeat right 10px center;
    border: 1.5px solid transparent; border-radius: 10px;
    cursor: pointer; outline: none;
    transition: all 0.2s;
    font-family: inherit;
    &:hover { border-color: #E5E5E5; }
    &:focus { border-color: #0A2E36; background-color: #fff; }
  }
}

.order-list {
  .order-card {
    border: 1px solid #F0F0F0; border-radius: 16px; margin-bottom: 16px; overflow: hidden;
    transition: box-shadow 0.2s;
    &:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.05); }

    .order-top {
      display: flex; justify-content: space-between; align-items: center;
      padding: 14px 20px; background: #FAFAF9;
      .order-meta { display: flex; gap: 16px; font-size: 13px; color: #6B7280; .order-no { font-family: "DM Sans", monospace; } }
    }

    .order-body {
      display: flex; gap: 16px; padding: 20px; align-items: center;
      .order-img { width: 72px; height: 72px; object-fit: cover; border-radius: 10px; flex-shrink: 0; }
      .order-info { flex: 1; h3 { font-size: 15px; font-weight: 500; margin-bottom: 6px; } }
      .order-parties { text-align: right; font-size: 13px; color: #6B7280; .party-label { color: #9CA3AF; margin-right: 4px; } }
    }

    .order-chain {
      display: flex; align-items: center; gap: 12px; padding: 12px 20px;
      background: linear-gradient(135deg, #EEF2FF, #F5F3FF); font-size: 12px;
      code { flex: 1; font-family: "DM Sans", monospace; color: #6B7280; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
      .btn-verify {
        display: inline-flex; align-items: center; gap: 4px; flex-shrink: 0;
        padding: 4px 12px; border-radius: 8px;
        font-size: 12px; font-weight: 600; cursor: pointer;
        color: #6366F1; background: rgba(99, 102, 241, 0.1);
        border: 1px solid rgba(99, 102, 241, 0.2);
        transition: all 0.2s; font-family: inherit;
        &:hover { background: #6366F1; color: #fff; border-color: #6366F1; }
      }
    }

    .order-actions {
      display: flex; justify-content: flex-end; gap: 8px; padding: 14px 20px; border-top: 1px solid #F0F0F0;
      &:empty { display: none; }
    }
  }
}

.empty-state { text-align: center; padding: 60px 20px; color: #9CA3AF; p { margin-top: 12px; } }
.pagination { margin-top: 24px; display: flex; justify-content: center; }
</style>
