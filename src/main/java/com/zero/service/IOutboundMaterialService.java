package com.zero.service;

import com.zero.model.OutboundMaterial;

import java.util.List;

public interface IOutboundMaterialService {

    List<OutboundMaterial> getOutboundMaterialByOutboundId(int outboundId);
}
