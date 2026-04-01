package com.secondhand.service;

import com.secondhand.common.PageResult;
import com.secondhand.entity.Product;
import com.secondhand.entity.Order;
import com.secondhand.vo.BlockchainRecordVO;

/**
 * 区块链服务接口
 */
public interface BlockchainService {
    
    /**
     * 商品上链
     */
    String recordProduct(Product product);
    
    /**
     * 交易上链
     */
    String recordTrade(Order order);
    
    /**
     * 查询链上记录
     */
    BlockchainRecordVO getByTxHash(String txHash);
    
    /**
     * 链上记录列表
     */
    PageResult<BlockchainRecordVO> list(Integer current, Integer size);
    
    /**
     * 验证商品数据
     */
    boolean verifyProduct(Long productId);
    
    /**
     * 验证交易数据
     */
    boolean verifyTrade(Long orderId);
}
