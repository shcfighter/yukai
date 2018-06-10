package com.zero.model.verify;

import com.zero.model.ProductOutbound;
import com.zero.model.ProductOutboundDetail;
import lombok.Data;

import java.util.List;

@Data
public class ProductOutboundDetails {

    private ProductOutbound productOutbound;
    private List<ProductOutboundDetail> outboundDetailList;
}
