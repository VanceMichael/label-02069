<template>
  <div class="page-container">
    <div class="page-header"><h1>用户管理</h1></div>

    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="用户名"><el-input v-model="searchForm.username" placeholder="请输入" clearable /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="searchForm.role" placeholder="全部" clearable style="width: 160px">
            <el-option label="普通用户" value="USER" /><el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadUsers">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="users" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="用户名" min-width="110" />
        <el-table-column prop="nickname" label="昵称" min-width="100" />
        <el-table-column prop="phone" label="手机号" min-width="130" />
        <el-table-column prop="balance" label="余额" min-width="100">
          <template #default="{ row }"><span class="price-val">¥{{ row.balance }}</span></template>
        </el-table-column>
        <el-table-column prop="role" label="角色" min-width="90">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'info'" size="small">{{ row.role === 'ADMIN' ? '管理员' : '用户' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" min-width="170" />
        <el-table-column label="操作" fixed="right" width="100">
          <template #default="{ row }">
            <button v-if="row.role !== 'ADMIN'" class="action-btn" :class="row.status === 1 ? 'danger' : 'success'" @click="toggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" @change="loadUsers" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, updateUserStatus } from '@/api'

const users = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchForm = reactive({ username: '', role: undefined })

async function loadUsers() {
  loading.value = true
  try { const res = await getUserList({ ...searchForm, current: currentPage.value, size: pageSize.value }); users.value = res.data.records; total.value = res.data.total }
  finally { loading.value = false }
}

function resetSearch() { searchForm.username = ''; searchForm.role = undefined; currentPage.value = 1; loadUsers() }

async function toggleStatus(row) {
  const s = row.status === 1 ? 0 : 1
  await ElMessageBox.confirm(`确定${s === 1 ? '启用' : '禁用'}用户 ${row.username} 吗？`, '提示')
  await updateUserStatus(row.id, s); ElMessage.success('操作成功'); loadUsers()
}

onMounted(() => loadUsers())
</script>

<style lang="scss" scoped>
.price-val { font-family: "DM Sans"; font-weight: 600; color: #EF4444; }
</style>
