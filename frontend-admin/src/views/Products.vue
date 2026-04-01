<template>
  <div class="page-container">
    <div class="page-header"><h1>商品管理</h1></div>

    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="商品名称"><el-input v-model="searchForm.keyword" placeholder="请输入" clearable /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.category" placeholder="全部" clearable style="width: 160px">
            <el-option v-for="c in categoryList" :key="c.name" :label="c.name" :value="c.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 160px">
            <el-option label="在售" :value="1" /><el-option label="已售" :value="2" /><el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadProducts">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="products" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="商品" min-width="260">
          <template #default="{ row }">
            <div class="product-cell">
              <img :src="getImage(row.images)" class="product-img" />
              <div class="product-info">
                <span class="title">{{ row.title }}</span>
                <span class="price">¥{{ row.price }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" min-width="90" />
        <el-table-column prop="sellerName" label="卖家" min-width="90" />
        <el-table-column prop="viewCount" label="浏览" min-width="70" />
        <el-table-column prop="status" label="状态" min-width="80">
          <template #default="{ row }"><el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag></template>
        </el-table-column>
        <el-table-column label="区块链" min-width="90">
          <template #default="{ row }">
            <span v-if="row.blockchainHash" class="blockchain-badge"><el-icon><Cpu /></el-icon> 已存证</span>
            <el-tag v-else type="info" size="small">未上链</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" min-width="170" />
        <el-table-column label="操作" fixed="right" width="140">
          <template #default="{ row }">
            <div class="action-group">
              <button v-if="row.status === 1" class="action-btn warning" @click="handleOffline(row)">下架</button>
              <button class="action-btn danger" @click="handleDelete(row)">删除</button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" @change="loadProducts" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Cpu } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProductList, updateProductStatus, deleteProduct, getCategoryList } from '@/api'

const products = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchForm = reactive({ keyword: '', category: undefined, status: undefined })
const categoryList = ref([])

const defaultImg = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 60 60"%3E%3Crect fill="%23F8F7F5" width="60" height="60"/%3E%3C/svg%3E'
function getImage(images) { return images?.split(',')[0] || defaultImg }
function getStatusType(s) { return { 0: 'info', 1: 'success', 2: 'warning' }[s] }
function getStatusText(s) { return { 0: '下架', 1: '在售', 2: '已售' }[s] }

async function loadProducts() {
  loading.value = true
  try { const res = await getProductList({ ...searchForm, current: currentPage.value, size: pageSize.value }); products.value = res.data.records; total.value = res.data.total }
  finally { loading.value = false }
}

function resetSearch() { searchForm.keyword = ''; searchForm.category = undefined; searchForm.status = undefined; currentPage.value = 1; loadProducts() }

async function handleOffline(row) {
  await ElMessageBox.confirm(`确定下架商品 ${row.title} 吗？`, '提示')
  await updateProductStatus(row.id, 0); ElMessage.success('已下架'); loadProducts()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除商品 ${row.title} 吗？`, '提示')
  await deleteProduct(row.id); ElMessage.success('删除成功'); loadProducts()
}

onMounted(() => {
  loadProducts()
  getCategoryList().then(res => { categoryList.value = res.data })
})
</script>

<style lang="scss" scoped>
.product-cell {
  display: flex; align-items: center; gap: 12px;
  .product-img { width: 56px; height: 56px; object-fit: cover; border-radius: 10px; }
  .product-info { display: flex; flex-direction: column;
    .title { font-size: 14px; color: #1A1A1A; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 180px; }
    .price { font-family: "DM Sans"; font-size: 14px; color: #EF4444; font-weight: 600; }
  }
}
</style>
