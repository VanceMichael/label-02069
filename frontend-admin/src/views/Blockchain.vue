<template>
  <div class="page-container">
    <div class="page-header"><h1>链上记录</h1></div>

    <div class="table-container">
      <el-table :data="records" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="dataType" label="类型" width="120">
          <template #default="{ row }"><el-tag :type="row.dataType === 'PRODUCT' ? 'primary' : 'success'" size="small">{{ row.dataTypeText }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="refId" label="关联ID" width="100" />
        <el-table-column prop="txHash" label="交易哈希">
          <template #default="{ row }"><code class="hash-code">{{ row.txHash }}</code></template>
        </el-table-column>
        <el-table-column prop="dataHash" label="数据哈希" width="200">
          <template #default="{ row }"><code class="hash-code">{{ row.dataHash?.substring(0, 20) }}...</code></template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '成功' : '失败' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createTime" label="上链时间" width="180" />
      </el-table>
      <div class="pagination">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" @change="loadRecords" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getBlockchainRecords } from '@/api'

const records = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

async function loadRecords() {
  loading.value = true
  try { const res = await getBlockchainRecords({ current: currentPage.value, size: pageSize.value }); records.value = res.data.records; total.value = res.data.total }
  finally { loading.value = false }
}

onMounted(() => loadRecords())
</script>

<style lang="scss" scoped>
.hash-code { font-family: "DM Sans", monospace; font-size: 12px; color: #6B7280; }
</style>
