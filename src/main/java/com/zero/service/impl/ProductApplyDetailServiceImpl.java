package com.zero.service.impl;

import com.zero.common.enmu.DeletedEnum;
import com.zero.mapper.ProductApplyDetailMapper;
import com.zero.model.ProductApplyDetail;
import com.zero.model.example.ProductApplyDetailExample;
import com.zero.service.IProductApplyDetailService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ProductApplyDetailServiceImpl implements IProductApplyDetailService {

    @Resource
    private ProductApplyDetailMapper productApplyDetailMapper;

    @Override
    public List<ProductApplyDetail> getProductApplyDetailByProductId(int productId) {
        ProductApplyDetailExample example = new ProductApplyDetailExample();
        ProductApplyDetailExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        criteria.andProductIdEqualTo(productId);
        return productApplyDetailMapper.selectByExample(example);
    }
}
