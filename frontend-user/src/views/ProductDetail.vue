<template>
  <div class="detail-page fade-in-up">
    <div v-if="loading" class="loading-state"><el-skeleton :rows="10" animated /></div>

    <template v-else-if="product">
      <div class="detail-main card-base">
        <div class="detail-gallery">
          <div class="main-image">
            <img :src="currentImage" :alt="product.title" />
          </div>
          <div v-if="imageList.length > 1" class="thumb-list">
            <div v-for="(img, i) in imageList" :key="i" class="thumb" :class="{ active: currentImage === img }" @click="currentImage = img">
              <img :src="img" />
            </div>
          </div>
        </div>

        <div class="detail-info">
          <div class="info-top">
            <span v-if="product.blockchainHash" class="blockchain-badge"><el-icon><Cpu /></el-icon> 链上存证</span>
            <el-tag v-if="product.category" type="info" size="small">{{ product.category }}</el-tag>
          </div>

          <h1 class="product-title">{{ product.title }}</h1>

          <div class="price-block">
            <span class="current-price">¥{{ product.price }}</span>
            <span v-if="product.originalPrice" class="original-price">原价 ¥{{ product.originalPrice }}</span>
          </div>

          <div class="meta-grid">
            <div class="meta-item">
              <span class="meta-label">成色</span>
              <span class="meta-value">{{ product.conditionLevel || 9 }}成新</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">浏览</span>
              <span class="meta-value">{{ product.viewCount }} 次</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">发布</span>
              <span class="meta-value">{{ product.createTime }}</span>
            </div>
          </div>

          <!-- 区块链存证 -->
          <div v-if="product.blockchainHash" class="chain-card">
            <div class="chain-header">
              <span class="chain-title"><el-icon><Cpu /></el-icon> 区块链存证信息</span>
              <button class="btn-verify" :disabled="verifying" @click="handleVerify">
                <svg v-if="!verifying" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
                <span v-if="verifying" class="verify-spinner"></span>
                {{ verifying ? '验证中...' : '验证数据' }}
              </button>
            </div>
            <div class="chain-row">
              <span class="chain-label">Tx Hash</span>
              <code class="chain-value">{{ product.blockchainHash }}</code>
            </div>
          </div>

          <div class="action-bar">
            <el-button type="primary" size="large" class="buy-btn" :disabled="product.status !== 1 || isSeller" @click="handleBuy">
              {{ product.status === 1 ? (isSeller ? '这是您的商品' : '立即购买') : '商品已下架' }}
            </el-button>
          </div>

          <div class="seller-card">
            <el-avatar :size="44" :src="product.sellerAvatar">{{ product.sellerName?.charAt(0) }}</el-avatar>
            <div class="seller-detail">
              <strong>{{ product.sellerName }}</strong>
              <span>卖家</span>
            </div>
          </div>
        </div>
      </div>

      <div class="desc-section card-base">
        <h2>商品描述</h2>
        <p class="desc-content">{{ product.description || '暂无描述' }}</p>
      </div>
    </template>

    <!-- 购买弹窗 -->
    <el-dialog v-model="buyDialogVisible" title="确认购买" width="420px" align-center>
      <div class="buy-preview">
        <img :src="currentImage" />
        <div><p class="buy-title">{{ product?.title }}</p><p class="buy-price">¥{{ product?.price }}</p></div>
      </div>
      <el-input v-model="orderRemark" type="textarea" placeholder="给卖家留言（选填）" :rows="3" />
      <template #footer>
        <el-button @click="buyDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="confirmBuy">确认下单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Cpu } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getProductDetail, verifyProduct } from '@/api/product'
import { createOrder } from '@/api/order'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const product = ref(null)
const loading = ref(true)
const currentImage = ref('')
const verifying = ref(false)
const buyDialogVisible = ref(false)
const orderRemark = ref('')
const submitting = ref(false)

const defaultImage = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 400"%3E%3Crect fill="%23F8F7F5" width="400" height="400"/%3E%3Ctext x="50%25" y="50%25" dominant-baseline="middle" text-anchor="middle" fill="%23D1D5DB" font-size="16"%3E暂无图片%3C/text%3E%3C/svg%3E'

const imageList = computed(() => {
  if (!product.value?.images) return [defaultImage]
  const list = product.value.images.split(',').filter(Boolean)
  return list.length > 0 ? list : [defaultImage]
})

const isSeller = computed(() => userStore.userInfo?.id === product.value?.sellerId)

async function loadProduct() {
  loading.value = true
  try {
    const res = await getProductDetail(route.params.id)
    product.value = res.data
    currentImage.value = imageList.value[0]
  } finally { loading.value = false }
}

async function handleVerify() {
  verifying.value = true
  try {
    const res = await verifyProduct(product.value.id)
    ElMessage[res.data ? 'success' : 'warning'](res.data ? '数据验证通过，链上数据完整' : '数据验证失败')
  } finally { verifying.value = false }
}

function handleBuy() {
  if (!userStore.isLoggedIn) { ElMessage.warning('请先登录'); router.push({ name: 'Login', query: { redirect: route.fullPath } }); return }
  buyDialogVisible.value = true
}

async function confirmBuy() {
  submitting.value = true
  try {
    await createOrder({ productId: product.value.id, remark: orderRemark.value })
    ElMessage.success('下单成功')
    buyDialogVisible.value = false
    router.push('/orders')
  } finally { submitting.value = false }
}

onMounted(() => loadProduct())
</script>

<style lang="scss" scoped>
.detail-page { max-width: 1100px; margin: 0 auto; padding: 32px; }

.detail-main {
  display: flex; gap: 40px; padding: 32px;
}

.detail-gallery {
  flex-shrink: 0; width: 420px;
  .main-image {
    width: 420px; height: 420px; border-radius: 12px; overflow: hidden; background: #F8F7F5;
    img { width: 100%; height: 100%; object-fit: cover; }
  }
  .thumb-list {
    display: flex; gap: 8px; margin-top: 12px;
    .thumb {
      width: 56px; height: 56px; border-radius: 8px; overflow: hidden;
      border: 2px solid transparent; cursor: pointer; transition: all 0.2s;
      &.active { border-color: #0A2E36; }
      img { width: 100%; height: 100%; object-fit: cover; }
    }
  }
}

.detail-info {
  flex: 1;
  .info-top { display: flex; gap: 8px; margin-bottom: 12px; }
  .product-title { font-size: 22px; font-weight: 600; color: #1A1A1A; line-height: 1.4; margin-bottom: 16px; }
  .price-block {
    background: linear-gradient(135deg, #FEF2F2, #FFF7ED); padding: 16px 20px; border-radius: 12px; margin-bottom: 20px;
    .current-price { font-family: "DM Sans"; font-size: 30px; font-weight: 700; color: #EF4444; }
    .original-price { margin-left: 12px; font-size: 14px; color: #D1D5DB; text-decoration: line-through; }
  }
}

.meta-grid {
  display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; margin-bottom: 20px;
  .meta-item {
    padding: 12px; background: #FAFAF9; border-radius: 10px;
    .meta-label { display: block; font-size: 12px; color: #9CA3AF; margin-bottom: 4px; }
    .meta-value { font-size: 14px; font-weight: 500; color: #1A1A1A; }
  }
}

.chain-card {
  background: linear-gradient(135deg, #EEF2FF, #F5F3FF); padding: 16px 20px; border-radius: 12px; margin-bottom: 20px;
  .chain-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
  .chain-title { display: flex; align-items: center; gap: 6px; font-weight: 600; color: #6366F1; font-size: 14px; }
  .chain-row { display: flex; gap: 8px; font-size: 12px; }
  .chain-label { color: #9CA3AF; flex-shrink: 0; }
  .chain-value { font-family: "DM Sans", monospace; color: #4A4A4A; word-break: break-all; }
  .btn-verify {
    display: inline-flex; align-items: center; gap: 5px;
    padding: 5px 14px; border-radius: 8px;
    font-size: 13px; font-weight: 600; cursor: pointer;
    color: #6366F1; background: rgba(99, 102, 241, 0.1);
    border: 1px solid rgba(99, 102, 241, 0.2);
    transition: all 0.2s; font-family: inherit;
    &:hover { background: #6366F1; color: #fff; border-color: #6366F1; }
    &:disabled { opacity: 0.6; cursor: not-allowed; }
  }
  .verify-spinner {
    display: inline-block; width: 12px; height: 12px;
    border: 2px solid currentColor; border-top-color: transparent;
    border-radius: 50%; animation: spin 0.6s linear infinite;
  }
}

.action-bar { margin-bottom: 20px; .buy-btn { width: 200px; height: 48px; font-size: 16px; } }

.seller-card {
  display: flex; align-items: center; gap: 12px; padding: 16px; background: #FAFAF9; border-radius: 12px;
  .seller-detail { strong { display: block; font-size: 15px; } span { font-size: 12px; color: #9CA3AF; } }
}

.desc-section {
  margin-top: 24px; padding: 28px;
  h2 { font-size: 17px; font-weight: 600; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid #F0F0F0; }
  .desc-content { color: #4A4A4A; line-height: 1.8; white-space: pre-wrap; }
}

.buy-preview {
  display: flex; gap: 12px; padding: 16px; background: #FAFAF9; border-radius: 12px; margin-bottom: 16px;
  img { width: 80px; height: 80px; object-fit: cover; border-radius: 8px; }
  .buy-title { font-size: 14px; color: #1A1A1A; margin-bottom: 8px; }
  .buy-price { font-family: "DM Sans"; font-size: 20px; font-weight: 700; color: #EF4444; }
}

@keyframes spin { to { transform: rotate(360deg); } }
</style>
