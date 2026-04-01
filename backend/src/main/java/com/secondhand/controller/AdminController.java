package com.secondhand.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.secondhand.annotation.OperationLogger;
import com.secondhand.common.BusinessException;
import com.secondhand.common.PageResult;
import com.secondhand.common.Result;
import com.secondhand.entity.*;
import com.secondhand.mapper.*;
import com.secondhand.util.UserContext;
import com.secondhand.vo.OrderVO;
import com.secondhand.vo.ProductVO;
import com.secondhand.vo.UserVO;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理后台控制器
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final UserMapper userMapper;
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;
    private final CategoryMapper categoryMapper;
    private final BlockchainRecordMapper blockchainRecordMapper;
    private final OperationLogMapper operationLogMapper;
    
    /**
     * 获取统计数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        checkAdmin();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userMapper.selectCount(null));
        stats.put("productCount", productMapper.selectCount(null));
        stats.put("orderCount", orderMapper.selectCount(null));
        stats.put("blockchainCount", blockchainRecordMapper.selectCount(null));
        
        // 分类统计
        List<Map<String, Object>> categoryStats = new ArrayList<>();
        List<Category> categories = categoryMapper.selectList(null);
        for (Category cat : categories) {
            Long count = productMapper.selectCount(
                new LambdaQueryWrapper<Product>().eq(Product::getCategoryId, cat.getId())
            );
            Map<String, Object> item = new HashMap<>();
            item.put("name", cat.getName());
            item.put("value", count);
            categoryStats.add(item);
        }
        stats.put("categoryStats", categoryStats);
        
        // 订单状态统计
        List<Long> orderStats = new ArrayList<>();
        for (int i = 0; i <= 4; i++) {
            int status = i;
            Long count = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>().eq(Order::getStatus, status)
            );
            orderStats.add(count);
        }
        stats.put("orderStats", orderStats);
        
        return Result.success(stats);
    }
    
    // ==================== 用户管理 ====================
    
    @GetMapping("/users")
    public Result<PageResult<UserVO>> getUserList(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        checkAdmin();
        
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(username)) {
            wrapper.like(User::getUsername, username);
        }
        if (StrUtil.isNotBlank(role)) {
            wrapper.eq(User::getRole, role);
        }
        wrapper.orderByDesc(User::getCreateTime);
        
        Page<User> result = userMapper.selectPage(page, wrapper);
        
        List<UserVO> records = result.getRecords().stream().map(u -> {
            UserVO vo = new UserVO();
            BeanUtil.copyProperties(u, vo);
            return vo;
        }).collect(Collectors.toList());
        
        return Result.success(PageResult.of(records, result.getTotal(), result.getCurrent(), result.getSize()));
    }
    
    @PutMapping("/users/{id}/status")
    @OperationLogger("更新用户状态")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        checkAdmin();
        
        User user = new User();
        user.setId(id);
        user.setStatus(body.get("status"));
        userMapper.updateById(user);
        
        return Result.success();
    }
    
    // ==================== 分类管理 ====================
    
    @GetMapping("/categories")
    public Result<List<Category>> getCategoryList() {
        checkAdmin();
        
        List<Category> list = categoryMapper.selectList(
            new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort)
        );
        return Result.success(list);
    }
    
    @PostMapping("/categories")
    @OperationLogger("新增分类")
    public Result<Void> addCategory(@RequestBody Category category) {
        checkAdmin();
        categoryMapper.insert(category);
        return Result.success();
    }
    
    @PutMapping("/categories/{id}")
    @OperationLogger("更新分类")
    public Result<Void> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        checkAdmin();
        category.setId(id);
        categoryMapper.updateById(category);
        return Result.success();
    }
    
    @DeleteMapping("/categories/{id}")
    @OperationLogger("删除分类")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        checkAdmin();
        categoryMapper.deleteById(id);
        return Result.success();
    }
    
    // ==================== 商品管理 ====================
    
    @GetMapping("/products")
    public Result<PageResult<ProductVO>> getProductList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        checkAdmin();
        
        Page<Product> page = new Page<>(current, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(Product::getTitle, keyword);
        }
        if (StrUtil.isNotBlank(category)) {
            wrapper.eq(Product::getCategory, category);
        }
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }
        wrapper.orderByDesc(Product::getCreateTime);
        
        Page<Product> result = productMapper.selectPage(page, wrapper);
        
        List<ProductVO> records = result.getRecords().stream().map(p -> {
            ProductVO vo = new ProductVO();
            BeanUtil.copyProperties(p, vo);
            User seller = userMapper.selectById(p.getSellerId());
            if (seller != null) {
                vo.setSellerName(seller.getNickname());
            }
            return vo;
        }).collect(Collectors.toList());
        
        return Result.success(PageResult.of(records, result.getTotal(), result.getCurrent(), result.getSize()));
    }
    
    @PutMapping("/products/{id}/status")
    @OperationLogger("更新商品状态")
    public Result<Void> updateProductStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        checkAdmin();
        
        Product product = new Product();
        product.setId(id);
        product.setStatus(body.get("status"));
        productMapper.updateById(product);
        
        return Result.success();
    }
    
    @DeleteMapping("/products/{id}")
    @OperationLogger("删除商品")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        checkAdmin();
        productMapper.deleteById(id);
        return Result.success();
    }
    
    // ==================== 订单管理 ====================
    
    @GetMapping("/orders")
    public Result<PageResult<OrderVO>> getOrderList(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        checkAdmin();
        
        Page<Order> page = new Page<>(current, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(orderNo)) {
            wrapper.like(Order::getOrderNo, orderNo);
        }
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        
        Page<Order> result = orderMapper.selectPage(page, wrapper);
        
        List<OrderVO> records = result.getRecords().stream().map(o -> {
            OrderVO vo = new OrderVO();
            BeanUtil.copyProperties(o, vo);
            vo.setStatusText(getOrderStatusText(o.getStatus()));
            
            User buyer = userMapper.selectById(o.getBuyerId());
            User seller = userMapper.selectById(o.getSellerId());
            if (buyer != null) vo.setBuyerName(buyer.getNickname());
            if (seller != null) vo.setSellerName(seller.getNickname());
            
            return vo;
        }).collect(Collectors.toList());
        
        return Result.success(PageResult.of(records, result.getTotal(), result.getCurrent(), result.getSize()));
    }
    
    // ==================== 操作日志 ====================
    
    @GetMapping("/logs")
    public Result<PageResult<OperationLog>> getOperationLogs(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String operation,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        checkAdmin();
        
        Page<OperationLog> page = new Page<>(current, size);
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(username)) {
            wrapper.like(OperationLog::getUsername, username);
        }
        if (StrUtil.isNotBlank(operation)) {
            wrapper.like(OperationLog::getOperation, operation);
        }
        wrapper.orderByDesc(OperationLog::getCreateTime);
        
        Page<OperationLog> result = operationLogMapper.selectPage(page, wrapper);
        
        return Result.success(PageResult.of(
            result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize()
        ));
    }
    
    private void checkAdmin() {
        String role = UserContext.getRole();
        if (!"ADMIN".equals(role)) {
            throw new BusinessException(403, "\u65e0\u6743\u8bbf\u95ee\uff0c\u4ec5\u7ba1\u7406\u5458\u53ef\u64cd\u4f5c");
        }
    }
    
    private String getOrderStatusText(Integer status) {
        return switch (status) {
            case 0 -> "待支付";
            case 1 -> "已支付";
            case 2 -> "已发货";
            case 3 -> "已完成";
            case 4 -> "已取消";
            default -> "未知";
        };
    }
}
