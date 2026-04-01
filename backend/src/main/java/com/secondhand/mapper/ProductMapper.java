package com.secondhand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品Mapper
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
