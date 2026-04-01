import request from './request'

// 查询链上记录
export function getBlockchainRecord(txHash) {
  return request.get(`/blockchain/record/${txHash}`)
}

// 链上记录列表
export function getBlockchainRecords(params) {
  return request.get('/blockchain/records', { params })
}
