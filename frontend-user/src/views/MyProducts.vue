<template>
  <div class="my-products-page fade-in-up">
    <div class="page-container card-base">
      <div class="page-header">
        <div>
          <h1 class="serif-title">我的商品</h1>
          <p class="page-desc">管理您发布的所有商品</p>
        </div>
        <el-button type="primary" @click="$router.push('/publish')"><el-icon><CirclePlus /></el-icon>发布商品</el-button>
      </div>

      <div class="filter-bar">
        <div class="status-tabs">
          <div v-for="tab in statusTabs" :key="tab.value" class="tab-item" :class="{ active: statusFilter === tab.value }" @click="selectStatus(tab.value)">
            <span v-if="tab.dot" class="status-dot" :class="tab.dot"></span>{{ tab.label }}
          </div>
        </div>
      </div>

      <div v-if="loading" class="loading-state"><el-skeleton :rows="5" animated /></div>

      <div v-else-if="products.length === 0" class="empty-state">
        <el-icon :size="48" color="#D1D5DB"><ShoppingBag /></el-icon>
        <p>暂无商品</p>
        <el-button type="primary" @click="$router.push('/publish')">去发布</el-button>
      </div>

      <div v-else class="product-list">
        <div v-for="product in products" :key="product.id" class="product-item">
          <div class="item-image" @click="$router.push(`/product/${product.id}`)">
            <img :src="getFirstImage(product.images)" />
          </div>
          <div class="item-info">
            <h3 @click="$router.push(`/product/${product.id}`)">{{ product.title }}</h3>
            <div class="item-meta">
              <span class="price-text">¥{{ product.price }}</span>
              <el-tag :type="getStatusType(product.status)" size="small">{{ product.statusText }}</el-tag>
              <span v-if="product.blockchainHash" class="blockchain-badge"><el-icon><Cpu /></el-icon> 已存证</span>
            </div>
            <div class="item-stats">
              <span><el-icon><View /></el-icon> {{ product.viewCount }} 浏览</span>
              <span>{{ product.createTime }}</span>
            </div>
          </div>
          <div class="item-actions">
            <el-button v-if="product.status === 1" class="btn-offline" size="small" @click="handleOffline(product)">
              <el-icon><SoldOut /></el-icon>下架
            </el-button>
            <span v-else-if="product.status === 0" class="status-label off">已下架</span>
            <span v-else-if="product.status === 2" class="status-label sold">已售出</span>
          </div>
        </div>
      </div>

      <div v-if="total > 0" class="pagination">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" @change="loadProducts" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { CirclePlus, ShoppingBag, View, Cpu, SoldOut } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyProducts, offlineProduct } from '@/api/product'

const products = ref([])
const loading = ref(false)
const statusFilter = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const statusTabs = [
  { label: '全部', value: null, dot: null },
  { label: '在售', value: 1, dot: 'on' },
  { label: '已售', value: 2, dot: 'sold' },
  { label: '已下架', value: 0, dot: 'off' }
]

function selectStatus(v) { statusFilter.value = v; currentPage.value = 1; loadProducts() }

const defaultImage = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"%3E%3Crect fill="%23F8F7F5" width="100" height="100"/%3E%3C/svg%3E'
function getFirstImage(images) { return images?.split(',')[0] || defaultImage }
function getStatusType(s) { return { 0: 'info', 1: 'success', 2: 'warning' }[s] || 'info' }

async function loadProducts() {
  loading.value = true
  try {
    const res = await getMyProducts({ status: statusFilter.value, current: currentPage.value, size: pageSize.value })
    products.value = res.data.records; total.value = res.data.total
  } finally { loading.value = false }
}

async function handleOffline(p) {
  await ElMessageBox.confirm('确定要下架该商品吗？', '提示')
  await offlineProduct(p.id)
  ElMessage.success('商品已下架')
  loadProducts()
}

onMounted(() => loadProducts())
</script>

<style lang="scss" scoped>
.my-products-page { max-width: 960px; margin: 0 auto; padding: 32px; }
.page-container { padding: 28px; }

.page-header {
  display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 24px;
  h1 { font-size: 22px; color: #0A2E36; }
  .page-desc { color: #9CA3AF; font-size: 14px; margin-top: 4px; }
}

.filter-bar { margin-bottom: 24px; }

.status-tabs {
  display: inline-flex; background: #F5F5F4; border-radius: 10px; padding: 3px;
  .tab-item {
    display: flex; align-items: center; gap: 5px;
    padding: 7px 20px; border-radius: 8px;
    font-size: 14px; font-weight: 500; color: #6B7280;
    cursor: pointer; transition: all 0.2s; user-select: none;
    white-space: nowrap;
    &:hover { color: #0A2E36; }
    &.active {
      background: #0A2E36; color: #fff;
      box-shadow: 0 1px 3px rgba(10, 46, 54, 0.2);
      .status-dot { border-color: rgba(255,255,255,0.4); }
    }
  }
}

.status-dot {
  display: inline-block; width: 7px; height: 7px; border-radius: 50%;
  border: 1px solid transparent;
  &.on { background: #10B981; }
  &.sold { background: #F59E0B; }
  &.off { background: #D1D5DB; }
}

.product-list {
  .product-item {
    display: flex; gap: 16px; padding: 20px 0; border-bottom: 1px solid #F0F0F0;
    &:last-child { border-bottom: none; }

    .item-image {
      flex-shrink: 0; width: 100px; height: 100px; border-radius: 12px; overflow: hidden; cursor: pointer; background: #F8F7F5;
      img { width: 100%; height: 100%; object-fit: cover; }
    }
    .item-info {
      flex: 1;
      h3 { font-size: 15px; font-weight: 500; color: #1A1A1A; margin-bottom: 8px; cursor: pointer; &:hover { color: #0A2E36; } }
      .item-meta { display: flex; align-items: center; gap: 10px; margin-bottom: 8px; }
      .item-stats { display: flex; gap: 16px; font-size: 12px; color: #9CA3AF; span { display: flex; align-items: center; gap: 4px; } }
    }
    .item-actions {
      display: flex; align-items: center; flex-shrink: 0;
      .btn-offline {
        border: 1px solid #FCA5A5 !important;
        background: #FEF2F2 !important;
        color: #EF4444 !important;
        border-radius: 10px !important;
        font-weight: 500;
        padding: 6px 16px !important;
        transition: all 0.2s;
        &:hover {
          background: #EF4444 !important;
          color: #fff !important;
          border-color: #EF4444 !important;
          transform: translateY(-1px);
          box-shadow: 0 3px 8px rgba(239, 68, 68, 0.25);
        }
      }
      .status-label {
        font-size: 13px; font-weight: 500; padding: 4px 14px; border-radius: 8px;
        &.off { color: #9CA3AF; background: #F5F5F4; }
        &.sold { color: #F59E0B; background: #FFFBEB; }
      }
    }
  }
}

.empty-state { text-align: center; padding: 60px 20px; color: #9CA3AF; p { margin: 12px 0; } }
.pagination { margin-top: 24px; display: flex; justify-content: center; }
</style>
