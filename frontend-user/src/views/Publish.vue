<template>
  <div class="publish-page fade-in-up">
    <div class="publish-container card-base">
      <h1 class="page-title serif-title">发布商品</h1>
      <p class="page-desc">填写商品信息，发布后将自动上链存证</p>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" label-position="top" class="publish-form">
        <div class="form-section">
          <h3>基本信息</h3>
          <el-form-item label="商品标题" prop="title">
            <el-input v-model="form.title" placeholder="请输入商品标题" maxlength="100" show-word-limit />
          </el-form-item>
          <div class="form-row">
            <el-form-item label="商品分类" prop="category">
              <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%">
                <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.name" />
              </el-select>
            </el-form-item>
            <el-form-item label="商品价格" prop="price">
              <el-input-number v-model="form.price" :min="0.01" :precision="2" :controls="false" placeholder="价格" style="width: 100%" />
            </el-form-item>
            <el-form-item label="原价（选填）">
              <el-input-number v-model="form.originalPrice" :min="0" :precision="2" :controls="false" placeholder="原价" style="width: 100%" />
            </el-form-item>
          </div>
        </div>

        <div class="form-section">
          <h3>商品详情</h3>
          <el-form-item label="成色">
            <div class="condition-row">
              <el-slider v-model="form.conditionLevel" :min="1" :max="10" show-stops style="flex:1" />
              <span class="condition-text">{{ form.conditionLevel }}成新</span>
            </div>
          </el-form-item>
          <el-form-item label="商品图片" prop="images">
            <el-upload
              v-model:file-list="fileList"
              :http-request="handleUploadImage"
              :before-upload="beforeUpload"
              :on-preview="handlePreview"
              :on-remove="handleRemove"
              list-type="picture-card"
              :limit="6"
              accept="image/*"
            >
              <el-icon :size="28"><Plus /></el-icon>
              <template #tip>
                <div class="upload-tip">支持 jpg/png/gif，单张不超过 5MB，最多 6 张</div>
              </template>
            </el-upload>
          </el-form-item>
          <el-form-item label="商品描述">
            <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请详细描述商品信息" maxlength="1000" show-word-limit />
          </el-form-item>
        </div>

        <div class="chain-notice">
          <el-icon><Cpu /></el-icon>
          <span>商品发布后将自动上链存证，确保信息不可篡改</span>
        </div>

        <div class="form-actions">
          <el-button size="large" @click="$router.back()">取消</el-button>
          <el-button type="primary" size="large" :loading="submitting" @click="handleSubmit">发布商品</el-button>
        </div>
      </el-form>
    </div>

    <!-- 图片预览 -->
    <el-image-viewer v-if="previewVisible" :url-list="[previewUrl]" @close="previewVisible = false" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Cpu, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElImageViewer } from 'element-plus'
import { publishProduct, getCategoryList } from '@/api/product'
import { uploadFile } from '@/api/user'

const router = useRouter()
const formRef = ref()
const submitting = ref(false)
const form = reactive({ title: '', category: '', price: null, originalPrice: null, conditionLevel: 9, images: '', description: '' })
const categories = ref([])
const fileList = ref([])
const uploadedUrls = ref([])  // 手动维护已上传的 URL 列表
const uidToUrlMap = ref({})  // uid 到 URL 的映射
const previewVisible = ref(false)
const previewUrl = ref('')

const rules = {
  title: [{ required: true, message: '请输入商品标题', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

function beforeUpload(file) {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) { ElMessage.error('只能上传图片文件'); return false }
  if (!isLt5M) { ElMessage.error('图片大小不能超过 5MB'); return false }
  return true
}

async function handleUploadImage(options) {
  try {
    console.log('=== 开始上传图片 ===')
    console.log('文件名:', options.file.name)
    console.log('文件 uid:', options.file.uid)
    
    const res = await uploadFile(options.file)
    const url = res.data
    console.log('上传接口返回:', res)
    console.log('提取的URL:', url)
    
    // 保存 uid 到 URL 的映射
    uidToUrlMap.value[options.file.uid] = url
    
    // 上传成功，立即保存 URL
    uploadedUrls.value.push(url)
    form.images = uploadedUrls.value.join(',')
    
    console.log('上传成功后状态:')
    console.log('  - uidToUrlMap:', JSON.stringify(uidToUrlMap.value))
    console.log('  - uploadedUrls:', JSON.stringify(uploadedUrls.value))
    console.log('  - form.images:', form.images)
    console.log('  - fileList长度:', fileList.value.length)
    console.log('===================\n')
    
    options.onSuccess(res)
  } catch (e) {
    console.error('上传失败:', e)
    options.onError(e)
    ElMessage.error('图片上传失败')
  }
}

function handleRemove(uploadFile) {
  console.log('=== handleRemove 被调用 ===')
  console.log('传入的 uploadFile.uid:', uploadFile.uid)
  console.log('传入的 uploadFile.name:', uploadFile.name)
  
  console.log('删除前状态:')
  console.log('  - uidToUrlMap:', JSON.stringify(uidToUrlMap.value))
  console.log('  - uploadedUrls:', JSON.stringify(uploadedUrls.value))
  console.log('  - form.images:', form.images)
  
  // 通过 uid 从映射中获取 URL
  const url = uidToUrlMap.value[uploadFile.uid]
  console.log('从 uidToUrlMap 获取的URL:', url)
  
  if (url) {
    const index = uploadedUrls.value.indexOf(url)
    console.log('在 uploadedUrls 中的索引:', index)
    
    if (index > -1) {
      uploadedUrls.value.splice(index, 1)
      form.images = uploadedUrls.value.join(',')
      // 同时从映射中删除
      delete uidToUrlMap.value[uploadFile.uid]
      console.log('删除成功！')
    } else {
      console.warn('警告：URL 在 uploadedUrls 中未找到！')
    }
  } else {
    console.warn('警告：无法从 uidToUrlMap 获取 URL！uid:', uploadFile.uid)
  }
  
  console.log('删除后状态:')
  console.log('  - uidToUrlMap:', JSON.stringify(uidToUrlMap.value))
  console.log('  - uploadedUrls:', JSON.stringify(uploadedUrls.value))
  console.log('  - form.images:', form.images)
  console.log('===================\n')
}

function handlePreview(file) {
  previewUrl.value = file.response?.data || file.url
  previewVisible.value = true
}

async function handleSubmit() {
  console.log('=== 准备提交表单 ===')
  console.log('当前 form 对象:', JSON.stringify(form, null, 2))
  console.log('uploadedUrls:', JSON.stringify(uploadedUrls.value))
  console.log('fileList:', fileList.value.map(f => ({ name: f.name, status: f.status, url: f.response?.data })))
  
  await formRef.value.validate()
  if (!form.images) {
    console.warn('验证失败：form.images 为空')
    ElMessage.warning('请至少上传一张商品图片')
    return
  }
  
  console.log('验证通过，准备调用 API')
  console.log('提交的 images 字段:', form.images)
  console.log('===================\n')
  
  submitting.value = true
  try {
    await publishProduct(form)
    ElMessage.success('商品发布成功，已上链存证')
    router.push('/my-products')
  } finally { submitting.value = false }
}

onMounted(async () => {
  try {
    const res = await getCategoryList()
    categories.value = res.data
  } catch (e) {
    console.error('加载分类失败', e)
  }
})
</script>

<style lang="scss" scoped>
.publish-page { max-width: 720px; margin: 0 auto; padding: 32px; }

.publish-container { padding: 36px; }

.page-title { font-size: 24px; color: #0A2E36; margin-bottom: 4px; }
.page-desc { color: #9CA3AF; margin-bottom: 32px; }

.form-section {
  margin-bottom: 28px; padding-bottom: 28px; border-bottom: 1px solid #F0F0F0;
  h3 { font-size: 16px; font-weight: 600; color: #0A2E36; margin-bottom: 20px; }
}

.form-row { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 16px; }

.condition-row { display: flex; align-items: center; gap: 16px; width: 100%; }
.condition-text { font-weight: 600; color: #0A2E36; white-space: nowrap; }

.upload-tip { font-size: 12px; color: #9CA3AF; margin-top: 6px; }

.chain-notice {
  display: flex; align-items: center; gap: 8px;
  padding: 14px 18px; background: linear-gradient(135deg, #EEF2FF, #F5F3FF);
  border-radius: 12px; color: #6366F1; font-size: 14px; font-weight: 500;
  margin-bottom: 24px;
}

.form-actions { display: flex; justify-content: flex-end; gap: 12px; }
</style>
