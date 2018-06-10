package com.zero.service.impl;

import com.zero.common.enmu.DeletedEnum;
import com.zero.mapper.ProductOutboundDetailMapper;
import com.zero.model.ProductOutboundDetail;
import com.zero.model.example.ProductOutboundDetailExample;
import com.zero.service.IProductOutboundDetailService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ProductOutboundDetailServiceImpl implements IProductOutboundDetailService {

    @Resource
    private ProductOutboundDetailMapper productOutboundDetailMapper;

    @Override
    public List<ProductOutboundDetail> getProductOutboundDetailByProductId(int outboundId) {
        ProductOutboundDetailExample example = new ProductOutboundDetailExample();
        ProductOutboundDetailExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        criteria.andOutboundIdEqualTo(outboundId);
        return productOutboundDetailMapper.selectByExample(example);
    }
}
