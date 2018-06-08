package com.zero.model.verify;

import com.zero.model.Product;
import com.zero.model.ProductDetail;
import lombok.Data;

import java.util.List;

@Data
public class ProductDetails {

    private Product product;
    private List<ProductDetail> detailList;
}
