package com.secondhand.controller;

import com.secondhand.common.PageResult;
import com.secondhand.common.Result;
import com.secondhand.service.BlockchainService;
import com.secondhand.vo.BlockchainRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 区块链控制器
 */
@RestController
@RequestMapping("/api/blockchain")
@RequiredArgsConstructor
public class BlockchainController {
    
    private final BlockchainService blockchainService;
    
    /**
     * 查询链上记录
     */
    @GetMapping("/record/{txHash}")
    public Result<BlockchainRecordVO> getRecord(@PathVariable String txHash) {
        return Result.success(blockchainService.getByTxHash(txHash));
    }
    
    /**
     * 链上记录列表
     */
    @GetMapping("/records")
    public Result<PageResult<BlockchainRecordVO>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(blockchainService.list(current, size));
    }
}
