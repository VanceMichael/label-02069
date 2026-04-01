<template>
  <div class="page-container">
    <div class="page-header"><h1>操作日志</h1></div>

    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="用户名"><el-input v-model="searchForm.username" placeholder="请输入" clearable /></el-form-item>
        <el-form-item label="操作类型"><el-input v-model="searchForm.operation" placeholder="请输入" clearable /></el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadLogs">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="logs" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="用户" min-width="90" />
        <el-table-column prop="operation" label="操作" min-width="110" />
        <el-table-column prop="method" label="请求" min-width="200">
          <template #default="{ row }"><code class="method-code">{{ row.method }}</code></template>
        </el-table-column>
        <el-table-column prop="ip" label="IP" min-width="130" />
        <el-table-column prop="result" label="结果" min-width="80">
          <template #default="{ row }"><el-tag :type="row.result === 1 ? 'success' : 'danger'" size="small">{{ row.result === 1 ? '成功' : '失败' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="costTime" label="耗时" min-width="90">
          <template #default="{ row }"><span class="time-val">{{ row.costTime }}ms</span></template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" min-width="170" />
      </el-table>
      <div class="pagination">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" @change="loadLogs" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getOperationLogs } from '@/api'

const logs = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchForm = reactive({ username: '', operation: '' })

async function loadLogs() {
  loading.value = true
  try { const res = await getOperationLogs({ ...searchForm, current: currentPage.value, size: pageSize.value }); logs.value = res.data.records; total.value = res.data.total }
  finally { loading.value = false }
}

function resetSearch() { searchForm.username = ''; searchForm.operation = ''; currentPage.value = 1; loadLogs() }

onMounted(() => loadLogs())
</script>

<style lang="scss" scoped>
.method-code { font-family: "DM Sans", monospace; font-size: 12px; color: #6B7280; }
.time-val { font-family: "DM Sans"; font-size: 13px; color: #4A4A4A; }
</style>
