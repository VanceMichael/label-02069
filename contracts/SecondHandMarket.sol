// SPDX-License-Identifier: MIT
pragma solidity ^0.6.10;
pragma experimental ABIEncoderV2;

/**
 * @title SecondHandMarket
 * @dev 二手交易平台智能合约 - 用于商品和交易存证
 */
contract SecondHandMarket {
    
    // 商品存证结构
    struct ProductRecord {
        uint256 productId;
        uint256 sellerId;
        string title;
        uint256 price;
        uint256 timestamp;
        string dataHash;
    }
    
    // 交易存证结构
    struct TradeRecord {
        uint256 orderId;
        string orderNo;
        uint256 buyerId;
        uint256 sellerId;
        uint256 productId;
        uint256 amount;
        uint256 timestamp;
        string dataHash;
    }
    
    // 存储映射
    mapping(uint256 => ProductRecord) public productRecords;
    mapping(uint256 => TradeRecord) public tradeRecords;
    
    // 记录数量
    uint256 public productCount;
    uint256 public tradeCount;
    
    // 事件
    event ProductRecorded(uint256 indexed productId, string dataHash, uint256 timestamp);
    event TradeRecorded(uint256 indexed orderId, string orderNo, string dataHash, uint256 timestamp);
    
    /**
     * @dev 记录商品信息上链
     */
    function recordProduct(
        uint256 _productId,
        uint256 _sellerId,
        string memory _title,
        uint256 _price,
        string memory _dataHash
    ) public returns (bool) {
        productRecords[_productId] = ProductRecord({
            productId: _productId,
            sellerId: _sellerId,
            title: _title,
            price: _price,
            timestamp: block.timestamp,
            dataHash: _dataHash
        });
        
        productCount++;
        emit ProductRecorded(_productId, _dataHash, block.timestamp);
        return true;
    }
    
    /**
     * @dev 记录交易信息上链
     */
    function recordTrade(
        uint256 _orderId,
        string memory _orderNo,
        uint256 _buyerId,
        uint256 _sellerId,
        uint256 _productId,
        uint256 _amount,
        string memory _dataHash
    ) public returns (bool) {
        tradeRecords[_orderId] = TradeRecord({
            orderId: _orderId,
            orderNo: _orderNo,
            buyerId: _buyerId,
            sellerId: _sellerId,
            productId: _productId,
            amount: _amount,
            timestamp: block.timestamp,
            dataHash: _dataHash
        });
        
        tradeCount++;
        emit TradeRecorded(_orderId, _orderNo, _dataHash, block.timestamp);
        return true;
    }
    
    /**
     * @dev 查询商品存证
     */
    function getProductRecord(uint256 _productId) public view returns (
        uint256 productId,
        uint256 sellerId,
        string memory title,
        uint256 price,
        uint256 timestamp,
        string memory dataHash
    ) {
        ProductRecord memory record = productRecords[_productId];
        return (
            record.productId,
            record.sellerId,
            record.title,
            record.price,
            record.timestamp,
            record.dataHash
        );
    }
    
    /**
     * @dev 查询交易存证
     */
    function getTradeRecord(uint256 _orderId) public view returns (
        uint256 orderId,
        string memory orderNo,
        uint256 buyerId,
        uint256 sellerId,
        uint256 productId,
        uint256 amount,
        uint256 timestamp,
        string memory dataHash
    ) {
        TradeRecord memory record = tradeRecords[_orderId];
        return (
            record.orderId,
            record.orderNo,
            record.buyerId,
            record.sellerId,
            record.productId,
            record.amount,
            record.timestamp,
            record.dataHash
        );
    }
    
    /**
     * @dev 验证商品数据哈希
     */
    function verifyProductHash(uint256 _productId, string memory _dataHash) public view returns (bool) {
        return keccak256(bytes(productRecords[_productId].dataHash)) == keccak256(bytes(_dataHash));
    }
    
    /**
     * @dev 验证交易数据哈希
     */
    function verifyTradeHash(uint256 _orderId, string memory _dataHash) public view returns (bool) {
        return keccak256(bytes(tradeRecords[_orderId].dataHash)) == keccak256(bytes(_dataHash));
    }
}
