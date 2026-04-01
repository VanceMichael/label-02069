import request from './request'

// 用户相关
export const login = data => request.post('/user/login', data)
export const getUserInfo = () => request.get('/user/info')

// 管理接口
export const getStats = () => request.get('/admin/stats')
export const getUserList = params => request.get('/admin/users', { params })
export const updateUserStatus = (id, status) => request.put(`/admin/users/${id}/status`, { status })

export const getCategoryList = () => request.get('/admin/categories')
export const addCategory = data => request.post('/admin/categories', data)
export const updateCategory = (id, data) => request.put(`/admin/categories/${id}`, data)
export const deleteCategory = id => request.delete(`/admin/categories/${id}`)

export const getProductList = params => request.get('/admin/products', { params })
export const updateProductStatus = (id, status) => request.put(`/admin/products/${id}/status`, { status })
export const deleteProduct = id => request.delete(`/admin/products/${id}`)

export const getOrderList = params => request.get('/admin/orders', { params })

export const getBlockchainRecords = params => request.get('/blockchain/records', { params })

export const getOperationLogs = params => request.get('/admin/logs', { params })
