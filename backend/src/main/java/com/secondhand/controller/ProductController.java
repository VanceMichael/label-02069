package com.secondhand.controller;

import com.secondhand.annotation.OperationLogger;
import com.secondhand.common.PageResult;
import com.secondhand.common.Result;
import com.secondhand.dto.ProductPublishDTO;
import com.secondhand.dto.ProductQueryDTO;
import com.secondhand.service.ProductService;
import com.secondhand.vo.ProductVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商品控制器
 */
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    /**
     * 发布商品
     */
    @PostMapping("/publish")
    @OperationLogger("发布商品")
    public Result<ProductVO> publish(@Valid @RequestBody ProductPublishDTO dto) {
        return Result.success(productService.publish(dto));
    }
    
    /**
     * 商品列表
     */
    @GetMapping("/list")
    public Result<PageResult<ProductVO>> list(ProductQueryDTO dto) {
        return Result.success(productService.list(dto));
    }
    
    /**
     * 商品详情
     */
    @GetMapping("/detail/{id}")
    public Result<ProductVO> detail(@PathVariable Long id) {
        return Result.success(productService.getById(id));
    }
    
    /**
     * 我发布的商品
     */
    @GetMapping("/my")
    public Result<PageResult<ProductVO>> myProducts(ProductQueryDTO dto) {
        return Result.success(productService.myProducts(dto));
    }
    
    /**
     * 更新商品
     */
    @PutMapping("/{id}")
    @OperationLogger("更新商品")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ProductPublishDTO dto) {
        productService.update(id, dto);
        return Result.success();
    }
    
    /**
     * 下架商品
     */
    @DeleteMapping("/{id}")
    @OperationLogger("下架商品")
    public Result<Void> offline(@PathVariable Long id) {
        productService.offline(id);
        return Result.success();
    }
    
    /**
     * 验证商品链上数据
     */
    @GetMapping("/verify/{id}")
    public Result<Boolean> verify(@PathVariable Long id) {
        return Result.success(productService.verifyBlockchain(id));
    }
}
