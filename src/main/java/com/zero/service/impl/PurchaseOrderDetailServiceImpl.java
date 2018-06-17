package com.zero.service.impl;

import com.zero.common.enmu.DeletedEnum;
import com.zero.mapper.PurchaseOrderDetailMapper;
import com.zero.model.PurchaseOrderDetail;
import com.zero.model.example.PurchaseOrderDetailExample;
import com.zero.service.IPurchaseOrderDetailService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PurchaseOrderDetailServiceImpl implements IPurchaseOrderDetailService {

    @Resource
    private PurchaseOrderDetailMapper purchaseOrderDetailMapper;

    @Override
    public List<PurchaseOrderDetail> getPurchaseOrderDetailByPurchaseId(int purchaseOrderId) {
        PurchaseOrderDetailExample example = new PurchaseOrderDetailExample();
        PurchaseOrderDetailExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        criteria.andPurchaseOrderIdEqualTo(purchaseOrderId);
        return purchaseOrderDetailMapper.selectByExample(example);
    }
}
