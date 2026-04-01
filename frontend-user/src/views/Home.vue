<template>
  <div class="home-page fade-in-up">
    <!-- Hero 区域 -->
    <section class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title serif-title">发现好物，链上可信</h1>
        <p class="hero-desc">每一件商品都经过区块链存证，交易透明可追溯</p>
        <div class="hero-search">
          <el-input v-model="searchKeyword" placeholder="搜索你想要的宝贝..." size="large" clearable @keyup.enter="handleSearch">
            <template #suffix>
              <el-button type="primary" circle @click="handleSearch"><el-icon><Search /></el-icon></el-button>
            </template>
          </el-input>
        </div>
      </div>
    </section>

    <!-- 分类导航 -->
    <section class="category-section">
      <div class="section-inner">
        <div class="category-grid">
          <div v-for="cat in categories" :key="cat.value" class="category-card" :class="{ active: selectedCategory === cat.value }" @click="selectCategory(cat.value)">
            <span class="cat-emoji">{{ cat.emoji }}</span>
            <span class="cat-name">{{ cat.label }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 商品列表 -->
    <section class="product-section" ref="productSectionRef">
      <div class="section-inner">
        <div class="section-header">
          <h2 class="serif-title">全部好物</h2>
          <span class="total-count">{{ total }} 件在售</span>
        </div>

        <div v-if="initialLoading" class="loading-state"><el-skeleton :rows="3" animated /></div>

        <template v-else>
          <div v-if="products.length === 0 && !loading" class="empty-state">
            <el-icon :size="48" color="#D1D5DB"><ShoppingBag /></el-icon>
            <p>暂无商品</p>
          </div>

          <div v-else class="product-grid-wrapper">
            <div class="product-grid" :class="{ 'is-loading': loading }">
              <div v-for="product in products" :key="product.id" class="product-card" @click="goDetail(product.id)">
                <div class="card-image">
                  <img :src="getFirstImage(product.images)" :alt="product.title" loading="lazy" />
                  <div v-if="product.blockchainHash" class="blockchain-badge">
                    <el-icon><Cpu /></el-icon> 已存证
                  </div>
                </div>
                <div class="card-body">
                  <h3 class="card-title">{{ product.title }}</h3>
                  <div class="card-price">
                    <span class="price-text">¥{{ product.price }}</span>
                    <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
                  </div>
                  <div class="card-footer">
                    <div class="seller-info">
                      <el-avatar :size="22" :src="product.sellerAvatar">{{ product.sellerName?.charAt(0) }}</el-avatar>
                      <span>{{ product.sellerName }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </template>

        <div v-if="total > 0" class="pagination">
          <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total" :page-sizes="[12, 24, 36]" layout="total, sizes, prev, pager, next" @change="handlePageChange" />
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { Search, ShoppingBag, Cpu } from '@element-plus/icons-vue'
import { getProductList, getCategoryList } from '@/api/product'

const router = useRouter()
const productSectionRef = ref(null)
const searchKeyword = ref('')
const selectedCategory = ref('')
const products = ref([])
const loading = ref(false)
const initialLoading = ref(true)
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

const categories = ref([
  { label: '全部', value: '', emoji: '🏠' }
])

const emojiMap = {
  '\u6570\u7801\u7535\u5b50': '📱',
  '\u670d\u9970\u978b\u5305': '👗',
  '\u56fe\u4e66\u6559\u6750': '📚',
  '\u751f\u6d3b\u7528\u54c1': '🏡',
  '\u8fd0\u52a8\u6237\u5916': '⚽',
  '\u5176\u4ed6': '🎁'
}

async function loadCategories() {
  try {
    const res = await getCategoryList()
    const list = res.data.map(cat => ({
      label: cat.name,
      value: cat.name,
      emoji: emojiMap[cat.name] || '📦'
    }))
    categories.value = [{ label: '全部', value: '', emoji: '🏠' }, ...list]
  } catch (e) {
    console.error('加载分类失败', e)
  }
}

const defaultImage = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 200 200"%3E%3Crect fill="%23F8F7F5" width="200" height="200"/%3E%3Ctext x="50%25" y="50%25" dominant-baseline="middle" text-anchor="middle" fill="%23D1D5DB" font-size="14"%3E暂无图片%3C/text%3E%3C/svg%3E'

function getFirstImage(images) {
  if (!images) return defaultImage
  return images.split(',')[0] || defaultImage
}

async function loadProducts() {
  loading.value = true
  try {
    const res = await getProductList({ keyword: searchKeyword.value, category: selectedCategory.value, current: currentPage.value, size: pageSize.value })
    products.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
    initialLoading.value = false
  }
}

function handleSearch() { currentPage.value = 1; loadProducts() }
function selectCategory(v) { selectedCategory.value = v; currentPage.value = 1; loadProducts() }
function handlePageChange() {
  loadProducts()
  nextTick(() => {
    if (productSectionRef.value) {
      const top = productSectionRef.value.getBoundingClientRect().top + window.scrollY - 80
      window.scrollTo({ top, behavior: 'smooth' })
    }
  })
}
function goDetail(id) { router.push(`/product/${id}`) }

onMounted(() => {
  loadCategories()
  loadProducts()
})
</script>

<style lang="scss" scoped>
.home-page { max-width: 1280px; margin: 0 auto; padding: 0 32px; }

// Hero
.hero-section {
  padding: 64px 0 48px;
  text-align: center;
  .hero-title { font-size: 36px; color: #0A2E36; margin-bottom: 12px; }
  .hero-desc { color: #9CA3AF; font-size: 16px; margin-bottom: 36px; }
  .hero-search { max-width: 560px; margin: 0 auto; }
}

// 分类
.category-section { padding-bottom: 40px; }
.category-grid {
  display: flex; gap: 12px; justify-content: center; flex-wrap: wrap;
  .category-card {
    display: flex; flex-direction: column; align-items: center; gap: 8px;
    padding: 16px 24px; border-radius: 16px; cursor: pointer;
    background: white; border: 1.5px solid #F0F0F0;
    transition: all 0.25s; min-width: 100px;
    .cat-emoji { font-size: 24px; }
    .cat-name { font-size: 13px; font-weight: 500; color: #4A4A4A; }
    &:hover { border-color: #0A2E36; transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,0.06); }
    &.active { background: #0A2E36; border-color: #0A2E36;
      .cat-name { color: white; }
    }
  }
}

// 商品
.product-section { padding-bottom: 60px; }
.section-header {
  display: flex; align-items: baseline; gap: 12px; margin-bottom: 24px;
  h2 { font-size: 24px; color: #0A2E36; }
  .total-count { font-size: 14px; color: #9CA3AF; }
}

.product-grid-wrapper { position: relative; }
.product-grid {
  display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 24px;
  transition: opacity 0.2s ease;
  &.is-loading { opacity: 0.5; pointer-events: none; }
}

.product-card {
  background: white; border-radius: 16px; overflow: hidden;
  border: 1px solid #F0F0F0; cursor: pointer;
  transition: all 0.3s ease;
  &:hover { transform: translateY(-4px); box-shadow: 0 8px 30px rgba(0,0,0,0.08); }

  .card-image {
    position: relative; height: 220px; overflow: hidden; background: #F8F7F5;
    img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.4s; }
    &:hover img { transform: scale(1.05); }
    .blockchain-badge { position: absolute; top: 12px; right: 12px; }
  }

  .card-body {
    padding: 16px;
    .card-title {
      font-size: 15px; font-weight: 500; color: #1A1A1A;
      margin-bottom: 10px; line-height: 1.4;
      overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
    }
    .card-price {
      display: flex; align-items: baseline; gap: 8px; margin-bottom: 12px;
      .price-text { font-family: "DM Sans", sans-serif; font-size: 20px; font-weight: 700; color: #EF4444; }
      .original-price { font-size: 12px; color: #D1D5DB; text-decoration: line-through; }
    }
    .card-footer {
      .seller-info {
        display: flex; align-items: center; gap: 6px;
        font-size: 12px; color: #9CA3AF;
      }
    }
  }
}

.empty-state { text-align: center; padding: 80px 20px; color: #9CA3AF; p { margin-top: 12px; } }
.loading-state { padding: 40px; }
.pagination { margin-top: 32px; display: flex; justify-content: center; }
</style>
