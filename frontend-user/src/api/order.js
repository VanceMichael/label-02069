import request from './request'

// 创建订单
export function createOrder(data) {
  return request.post('/order/create', data)
}

// 订单列表
export function getOrderList(params) {
  return request.get('/order/list', { params })
}

// 订单详情
export function getOrderDetail(id) {
  return request.get(`/order/${id}`)
}

// 支付订单
export function payOrder(id) {
  return request.put(`/order/${id}/pay`)
}

// 发货
export function shipOrder(id) {
  return request.put(`/order/${id}/ship`)
}

// 确认收货
export function confirmOrder(id) {
  return request.put(`/order/${id}/confirm`)
}

// 取消订单
export function cancelOrder(id) {
  return request.put(`/order/${id}/cancel`)
}

// 验证交易链上数据
export function verifyOrder(id) {
  return request.get(`/order/verify/${id}`)
}
