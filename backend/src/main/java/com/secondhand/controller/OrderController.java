package com.secondhand.controller;

import com.secondhand.annotation.OperationLogger;
import com.secondhand.common.PageResult;
import com.secondhand.common.Result;
import com.secondhand.dto.OrderCreateDTO;
import com.secondhand.dto.OrderQueryDTO;
import com.secondhand.service.OrderService;
import com.secondhand.vo.OrderVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    /**
     * 创建订单
     */
    @PostMapping("/create")
    @OperationLogger("创建订单")
    public Result<OrderVO> create(@Valid @RequestBody OrderCreateDTO dto) {
        return Result.success(orderService.create(dto));
    }
    
    /**
     * 订单列表
     */
    @GetMapping("/list")
    public Result<PageResult<OrderVO>> list(OrderQueryDTO dto) {
        return Result.success(orderService.list(dto));
    }
    
    /**
     * 订单详情
     */
    @GetMapping("/{id}")
    public Result<OrderVO> detail(@PathVariable Long id) {
        return Result.success(orderService.getById(id));
    }
    
    /**
     * 支付订单
     */
    @PutMapping("/{id}/pay")
    @OperationLogger("支付订单")
    public Result<Void> pay(@PathVariable Long id) {
        orderService.pay(id);
        return Result.success();
    }
    
    /**
     * 发货
     */
    @PutMapping("/{id}/ship")
    @OperationLogger("订单发货")
    public Result<Void> ship(@PathVariable Long id) {
        orderService.ship(id);
        return Result.success();
    }
    
    /**
     * 确认收货
     */
    @PutMapping("/{id}/confirm")
    @OperationLogger("确认收货")
    public Result<Void> confirm(@PathVariable Long id) {
        orderService.confirm(id);
        return Result.success();
    }
    
    /**
     * 取消订单
     */
    @PutMapping("/{id}/cancel")
    @OperationLogger("取消订单")
    public Result<Void> cancel(@PathVariable Long id) {
        orderService.cancel(id);
        return Result.success();
    }
    
    /**
     * 验证交易链上数据
     */
    @GetMapping("/verify/{id}")
    public Result<Boolean> verify(@PathVariable Long id) {
        return Result.success(orderService.verifyBlockchain(id));
    }
}
