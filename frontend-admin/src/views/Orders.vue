<template>
  <div class="page-container">
    <div class="page-header"><h1>订单管理</h1></div>

    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="订单号"><el-input v-model="searchForm.orderNo" placeholder="请输入" clearable /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 160px">
            <el-option label="待支付" :value="0" /><el-option label="已支付" :value="1" /><el-option label="已发货" :value="2" />
            <el-option label="已完成" :value="3" /><el-option label="已取消" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadOrders">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="orders" v-loading="loading" style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" min-width="190">
          <template #default="{ row }"><code class="order-code">{{ row.orderNo }}</code></template>
        </el-table-column>
        <el-table-column label="商品" min-width="200">
          <template #default="{ row }">
            <div class="product-cell">
              <img :src="row.productImage || defaultImg" class="product-img" />
              <span class="title">{{ row.productTitle }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" min-width="90">
          <template #default="{ row }"><span class="price-val">¥{{ row.amount }}</span></template>
        </el-table-column>
        <el-table-column prop="buyerName" label="买家" min-width="90" />
        <el-table-column prop="sellerName" label="卖家" min-width="90" />
        <el-table-column prop="status" label="状态" min-width="90">
          <template #default="{ row }"><el-tag :type="getStatusType(row.status)" size="small">{{ row.statusText }}</el-tag></template>
        </el-table-column>
        <el-table-column label="区块链" min-width="90">
          <template #default="{ row }">
            <span v-if="row.blockchainHash" class="blockchain-badge"><el-icon><Cpu /></el-icon> 已存证</span>
            <el-tag v-else type="info" size="small">未上链</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
      </el-table>
      <div class="pagination">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" @change="loadOrders" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Cpu } from '@element-plus/icons-vue'
import { getOrderList } from '@/api'

const orders = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchForm = reactive({ orderNo: '', status: undefined })

const defaultImg = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 50 50"%3E%3Crect fill="%23F8F7F5" width="50" height="50"/%3E%3C/svg%3E'
function getStatusType(s) { return { 0: 'warning', 1: 'primary', 2: 'primary', 3: 'success', 4: 'info' }[s] }

async function loadOrders() {
  loading.value = true
  try { const res = await getOrderList({ ...searchForm, current: currentPage.value, size: pageSize.value }); orders.value = res.data.records; total.value = res.data.total }
  finally { loading.value = false }
}

function resetSearch() { searchForm.orderNo = ''; searchForm.status = undefined; currentPage.value = 1; loadOrders() }

onMounted(() => loadOrders())
</script>

<style lang="scss" scoped>
.order-code { font-family: "DM Sans", monospace; font-size: 13px; color: #6B7280; }
.price-val { font-family: "DM Sans"; font-weight: 600; color: #EF4444; }
.product-cell {
  display: flex; align-items: center; gap: 8px;
  .product-img { width: 44px; height: 44px; object-fit: cover; border-radius: 8px; }
  .title { font-size: 13px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 120px; }
}
</style>
