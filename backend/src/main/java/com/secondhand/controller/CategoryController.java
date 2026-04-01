package com.secondhand.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.secondhand.common.Result;
import com.secondhand.entity.Category;
import com.secondhand.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 公开分类接口（无需登录）
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryMapper categoryMapper;

    @GetMapping
    public Result<List<Category>> list() {
        List<Category> list = categoryMapper.selectList(
            new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort)
        );
        return Result.success(list);
    }
}
