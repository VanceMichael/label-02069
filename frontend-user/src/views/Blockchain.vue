<template>
  <div class="blockchain-page fade-in-up">
    <div class="page-container">
      <div class="page-header">
        <div class="header-icon">
          <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
            <rect rx="12" width="48" height="48" fill="url(#chain-grad)"/>
            <path d="M24 14L16 19v10l8 5 8-5V19L24 14z" stroke="white" stroke-width="1.5" fill="none"/>
            <circle cx="24" cy="24" r="3" fill="white"/>
            <defs><linearGradient id="chain-grad" x1="0" y1="0" x2="48" y2="48"><stop stop-color="#6366F1"/><stop offset="1" stop-color="#8B5CF6"/></linearGradient></defs>
          </svg>
        </div>
        <h1 class="serif-title">链上存证</h1>
        <p class="page-desc">基于 FISCO BCOS 区块链的交易存证记录，数据不可篡改</p>
      </div>

      <div class="stats-row">
        <div class="stat-card product-stat">
          <span class="stat-num">{{ productCount }}</span>
          <span class="stat-label">商品存证</span>
        </div>
        <div class="stat-card trade-stat">
          <span class="stat-num">{{ tradeCount }}</span>
          <span class="stat-label">交易存证</span>
        </div>
      </div>

      <div class="records-section card-base">
        <h2>存证记录</h2>

        <div v-if="loading"><el-skeleton :rows="5" animated /></div>

        <div v-else-if="records.length === 0" class="empty-state">
          <el-icon :size="48" color="#D1D5DB"><Cpu /></el-icon>
          <p>暂无链上记录</p>
        </div>

        <div v-else class="record-list">
          <div v-for="record in records" :key="record.id" class="record-item">
            <div class="record-type-icon" :class="record.dataType.toLowerCase()">
              <el-icon v-if="record.dataType === 'PRODUCT'"><ShoppingBag /></el-icon>
              <el-icon v-else><Tickets /></el-icon>
            </div>
            <div class="record-content">
              <div class="record-top">
                <el-tag :type="record.dataType === 'PRODUCT' ? 'primary' : 'success'" size="small">{{ record.dataTypeText }}</el-tag>
                <span class="record-time">{{ record.createTime }}</span>
              </div>
              <div class="record-hashes">
                <div class="hash-row"><span class="hash-label">Tx Hash</span><code>{{ record.txHash }}</code></div>
                <div class="hash-row"><span class="hash-label">Data Hash</span><code>{{ record.dataHash }}</code></div>
              </div>
              <el-collapse>
                <el-collapse-item title="查看存证数据">
                  <pre class="data-json">{{ formatJson(record.dataContent) }}</pre>
                </el-collapse-item>
              </el-collapse>
            </div>
          </div>
        </div>

        <div v-if="total > 0" class="pagination">
          <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" @change="loadRecords" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ShoppingBag, Tickets, Cpu } from '@element-plus/icons-vue'
import { getBlockchainRecords } from '@/api/blockchain'

const records = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const productCount = computed(() => records.value.filter(r => r.dataType === 'PRODUCT').length)
const tradeCount = computed(() => records.value.filter(r => r.dataType === 'TRADE').length)

function formatJson(s) { try { return JSON.stringify(JSON.parse(s), null, 2) } catch { return s } }

async function loadRecords() {
  loading.value = true
  try {
    const res = await getBlockchainRecords({ current: currentPage.value, size: pageSize.value })
    records.value = res.data.records; total.value = res.data.total
  } finally { loading.value = false }
}

onMounted(() => loadRecords())
</script>

<style lang="scss" scoped>
.blockchain-page { max-width: 960px; margin: 0 auto; padding: 32px; }

.page-header {
  text-align: center; margin-bottom: 32px;
  .header-icon { margin-bottom: 16px; }
  h1 { font-size: 28px; color: #0A2E36; margin-bottom: 8px; }
  .page-desc { color: #9CA3AF; font-size: 15px; }
}

.stats-row {
  display: flex; gap: 20px; margin-bottom: 28px;
  .stat-card {
    flex: 1; padding: 24px; border-radius: 16px; text-align: center;
    .stat-num { display: block; font-family: "DM Sans"; font-size: 36px; font-weight: 700; margin-bottom: 4px; }
    .stat-label { font-size: 14px; }
    &.product-stat { background: linear-gradient(135deg, #EEF2FF, #E0E7FF); .stat-num { color: #6366F1; } .stat-label { color: #818CF8; } }
    &.trade-stat { background: linear-gradient(135deg, #ECFDF5, #D1FAE5); .stat-num { color: #10B981; } .stat-label { color: #34D399; } }
  }
}

.records-section {
  padding: 28px;
  h2 { font-size: 18px; font-weight: 600; margin-bottom: 20px; padding-bottom: 12px; border-bottom: 1px solid #F0F0F0; }
}

.record-list {
  .record-item {
    display: flex; gap: 16px; padding: 20px; border: 1px solid #F0F0F0; border-radius: 14px; margin-bottom: 14px;
    transition: box-shadow 0.2s;
    &:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.04); }

    .record-type-icon {
      flex-shrink: 0; width: 44px; height: 44px; border-radius: 12px;
      display: flex; align-items: center; justify-content: center;
      .el-icon { font-size: 22px; color: white; }
      &.product { background: linear-gradient(135deg, #6366F1, #818CF8); }
      &.trade { background: linear-gradient(135deg, #10B981, #34D399); }
    }

    .record-content {
      flex: 1; overflow: hidden;
      .record-top { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; .record-time { font-size: 12px; color: #9CA3AF; } }
      .record-hashes {
        margin-bottom: 12px;
        .hash-row { display: flex; gap: 8px; margin-bottom: 6px; font-size: 12px; }
        .hash-label { color: #9CA3AF; flex-shrink: 0; width: 70px; }
        code { font-family: "DM Sans", monospace; color: #6B7280; word-break: break-all; }
      }
      .data-json { background: #FAFAF9; padding: 12px; border-radius: 8px; font-size: 12px; font-family: "DM Sans", monospace; white-space: pre-wrap; word-break: break-all; }
    }
  }
}

.empty-state { text-align: center; padding: 60px 20px; color: #9CA3AF; p { margin-top: 12px; } }
.pagination { margin-top: 24px; display: flex; justify-content: center; }
</style>
