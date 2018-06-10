package com.zero.model.verify;

import com.zero.model.MaterialOutbound;
import com.zero.model.OutboundMaterial;
import lombok.Data;

import java.util.List;

@Data
public class MaterialOutboundDetails {
    private MaterialOutbound materialOutbound;
    private List<OutboundMaterial> materialList;
}
