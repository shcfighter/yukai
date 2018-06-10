package com.zero.service.impl;

import com.zero.common.enmu.DeletedEnum;
import com.zero.mapper.OutboundMaterialMapper;
import com.zero.model.OutboundMaterial;
import com.zero.model.example.OutboundMaterialExample;
import com.zero.service.IOutboundMaterialService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class OutBoundMaterialServiceImpl implements IOutboundMaterialService {

    @Resource
    private OutboundMaterialMapper outboundMaterialMapper;

    @Override
    public List<OutboundMaterial> getOutboundMaterialByOutboundId(int outboundId) {
        OutboundMaterialExample example = new OutboundMaterialExample();
        OutboundMaterialExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        criteria.andOutboundIdEqualTo(outboundId);
        return outboundMaterialMapper.selectByExample(example);
    }
}
