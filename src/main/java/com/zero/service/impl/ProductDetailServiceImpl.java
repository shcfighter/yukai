package com.zero.service.impl;

import com.zero.common.enmu.DeletedEnum;
import com.zero.mapper.ProductDetailMapper;
import com.zero.model.ProductDetail;
import com.zero.model.example.ProductDetailExample;
import com.zero.service.IProductDetailService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ProductDetailServiceImpl implements IProductDetailService {

    @Resource
    private ProductDetailMapper productDetailMapper;

    @Override
    public List<ProductDetail> getProductDetailByProductId(int productId) {
        ProductDetailExample example = new ProductDetailExample();
        ProductDetailExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        criteria.andProductIdEqualTo(productId);
        return productDetailMapper.selectByExample(example);
    }

    @Override
    public long selectTotal() {
        return productDetailMapper.selectTotal();
    }
}
