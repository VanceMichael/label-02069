<template>
  <div class="dashboard-page">
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6" v-for="item in statItems" :key="item.label">
        <div class="stat-card">
          <div class="stat-icon" :style="{ background: item.gradient }">
            <el-icon><component :is="item.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <span class="label">{{ item.label }}</span>
            <span class="value">{{ item.value }}</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <div class="chart-card card-base">
          <h3>商品分类统计</h3>
          <div ref="categoryChartRef" class="chart"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="chart-card card-base">
          <h3>订单状态统计</h3>
          <div ref="orderChartRef" class="chart"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { UserFilled, ShoppingBag, Tickets, Cpu } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getStats } from '@/api'

const stats = ref({})
const categoryChartRef = ref()
const orderChartRef = ref()

const statItems = computed(() => [
  { label: '用户总数', value: stats.value.userCount || 0, icon: 'UserFilled', gradient: 'linear-gradient(135deg, #0A2E36, #134E5E)' },
  { label: '商品总数', value: stats.value.productCount || 0, icon: 'ShoppingBag', gradient: 'linear-gradient(135deg, #10B981, #34D399)' },
  { label: '订单总数', value: stats.value.orderCount || 0, icon: 'Tickets', gradient: 'linear-gradient(135deg, #F59E0B, #FBBF24)' },
  { label: '链上记录', value: stats.value.blockchainCount || 0, icon: 'Cpu', gradient: 'linear-gradient(135deg, #6366F1, #8B5CF6)' }
])

async function loadStats() {
  try { const res = await getStats(); stats.value = res.data }
  catch { stats.value = { userCount: 8, productCount: 36, orderCount: 3, blockchainCount: 0 } }
  initCharts()
}

function initCharts() {
  const colors = ['#0A2E36', '#C9A96E', '#10B981', '#6366F1', '#F59E0B', '#EF4444']

  echarts.init(categoryChartRef.value).setOption({
    tooltip: { trigger: 'item' },
    color: colors,
    series: [{ type: 'pie', radius: ['45%', '72%'], itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 3 },
      data: stats.value.categoryStats || [
        { value: 6, name: '数码电子' }, { value: 6, name: '服饰鞋包' }, { value: 6, name: '图书教材' },
        { value: 6, name: '生活用品' }, { value: 6, name: '运动户外' }, { value: 6, name: '其他' }
      ]
    }]
  })

  echarts.init(orderChartRef.value).setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: ['待支付', '已支付', '已发货', '已完成', '已取消'], axisLine: { lineStyle: { color: '#E5E5E5' } }, axisLabel: { color: '#9CA3AF' } },
    yAxis: { type: 'value', splitLine: { lineStyle: { color: '#F0F0F0' } }, axisLabel: { color: '#9CA3AF' } },
    series: [{ type: 'bar', barWidth: 32, itemStyle: { borderRadius: [6, 6, 0, 0],
      color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#0A2E36' }, { offset: 1, color: '#134E5E' }])
    }, data: stats.value.orderStats || [0, 0, 0, 3, 0] }]
  })
}

onMounted(() => loadStats())
</script>

<style lang="scss" scoped>
.dashboard-page { padding: 24px; }
.stat-row { margin-bottom: 24px; }

.chart-card {
  padding: 24px;
  h3 { font-size: 16px; font-weight: 600; color: #0A2E36; margin-bottom: 16px; }
  .chart { height: 300px; }
}
</style>
