import request from './request'

// 发布商品
export function publishProduct(data) {
  return request.post('/product/publish', data)
}

// 商品列表
export function getProductList(params) {
  return request.get('/product/list', { params })
}

// 商品详情
export function getProductDetail(id) {
  return request.get(`/product/detail/${id}`)
}

// 我的商品
export function getMyProducts(params) {
  return request.get('/product/my', { params })
}

// 更新商品
export function updateProduct(id, data) {
  return request.put(`/product/${id}`, data)
}

// 下架商品
export function offlineProduct(id) {
  return request.delete(`/product/${id}`)
}

// 验证商品链上数据
export function verifyProduct(id) {
  return request.get(`/product/verify/${id}`)
}

// 获取分类列表（公开接口）
export function getCategoryList() {
  return request.get('/categories')
}
