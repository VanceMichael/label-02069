<template>
  <div class="page-container">
    <div class="page-header">
      <h1>分类管理</h1>
      <el-button type="primary" @click="openDialog()"><el-icon><CirclePlus /></el-icon>新增分类</el-button>
    </div>

    <div class="table-container">
      <el-table :data="categories" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" width="150" />
        <el-table-column prop="icon" label="图标" width="120">
          <template #default="{ row }"><el-icon v-if="row.icon"><component :is="row.icon" /></el-icon></template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <div class="action-group">
              <button class="action-btn primary" @click="openDialog(row)">编辑</button>
              <button class="action-btn danger" @click="handleDelete(row)">删除</button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="editId ? '编辑分类' : '新增分类'" width="480px" align-center>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name"><el-input v-model="form.name" placeholder="请输入分类名称" /></el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-select v-model="form.icon" placeholder="请选择图标">
            <el-option v-for="icon in icons" :key="icon" :label="icon" :value="icon">
              <div style="display:flex;align-items:center;gap:8px"><el-icon><component :is="icon" /></el-icon><span>{{ icon }}</span></div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio><el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { CirclePlus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCategoryList, addCategory, updateCategory, deleteCategory } from '@/api'

const categories = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const editId = ref(null)
const formRef = ref()
const icons = ['Monitor', 'ShoppingBag', 'Reading', 'House', 'Football', 'MoreFilled', 'Iphone', 'Camera', 'Bicycle']
const form = reactive({ name: '', icon: '', sort: 0, status: 1 })
const rules = { name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }] }

async function loadCategories() {
  loading.value = true
  try { const res = await getCategoryList(); categories.value = res.data } finally { loading.value = false }
}

function openDialog(row) {
  editId.value = row?.id || null; form.name = row?.name || ''; form.icon = row?.icon || ''; form.sort = row?.sort || 0; form.status = row?.status ?? 1
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate(); submitting.value = true
  try {
    editId.value ? await updateCategory(editId.value, form) : await addCategory(form)
    ElMessage.success('保存成功'); dialogVisible.value = false; loadCategories()
  } finally { submitting.value = false }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除分类 ${row.name} 吗？`, '提示')
  await deleteCategory(row.id); ElMessage.success('删除成功'); loadCategories()
}

onMounted(() => loadCategories())
</script>
