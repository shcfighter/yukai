package com.zero.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zero.common.BusinessException;
import com.zero.common.enmu.DeletedEnum;
import com.zero.common.enmu.ProductStatus;
import com.zero.mapper.ProductDetailMapper;
import com.zero.mapper.ProductMapper;
import com.zero.model.*;
import com.zero.model.example.OrderDetailExample;
import com.zero.model.example.ProductDetailExample;
import com.zero.model.example.ProductExample;
import com.zero.service.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ProductServiceImpl implements IProductService {

    private static final Logger LOGGER = LogManager.getLogger(ProductServiceImpl.class);
    @Resource
    private ProductMapper productMapper;
    @Resource
    private ProductDetailMapper productDetailMapper;
    @Resource
    private IProductDetailService productDetailService;
    @Resource
    private IProductApplyService productApplyService;
    @Resource
    private IProductApplyDetailService productApplyDetailService;
    @Resource
    private IProductOutboundService productOutboundService;
    @Resource
    private IProductOutboundDetailService productOutboundDetailService;

    @Override
    public Product getProductById(int id) {
        return productMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Product> findProductPage(String productName, String sampleCode, String orderCode, Integer status, Integer pageNum, Integer pageSize) {
        ProductExample example = new ProductExample();
        if(Objects.nonNull(pageNum) && Objects.nonNull(pageSize)){
            example.setPage(pageNum);
            example.setLimit(pageSize);
        }
        ProductExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if (Objects.nonNull(status)) {
            criteria.andStatusEqualTo(status);
        }
        if(StringUtils.isNotEmpty(productName)){
            criteria.andProductNameLike("%" + productName + "%");
        }
        if(StringUtils.isNotEmpty(sampleCode)){
            criteria.andSampleCodeLike("%" + sampleCode + "%");
        }
        if(StringUtils.isNotEmpty(orderCode)){
            criteria.andOrderCodeLike("%" + orderCode + "%");
        }
        return Optional.ofNullable(productMapper.selectByExample(example)).orElse(Lists.newArrayList());
    }

    @Override
    public List<Map<String, Object>> findProductAndDetailList(String productName, String sampleCode, String orderCode, Integer status, Integer pageNum, Integer pageSize) {
        ProductExample example = new ProductExample();
        if(Objects.nonNull(pageNum) && Objects.nonNull(pageSize)){
            example.setPage(pageNum);
            example.setLimit(pageSize);
        }
        ProductExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if (Objects.nonNull(status)) {
            criteria.andStatusEqualTo(status);
        }
        if(StringUtils.isNotEmpty(productName)){
            criteria.andProductNameLike("%" + productName + "%");
        }
        if(StringUtils.isNotEmpty(sampleCode)){
            criteria.andSampleCodeLike("%" + sampleCode + "%");
        }
        if(StringUtils.isNotEmpty(orderCode)){
            criteria.andOrderCodeLike("%" + orderCode + "%");
        }
        return Optional.ofNullable(productMapper.findProductList(example)).orElse(Lists.newArrayList());
    }

    @Override
    public long findProductRowNum(String productName, String sampleCode, String orderCode, Integer status) {
        ProductExample example = new ProductExample();
        ProductExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if (Objects.nonNull(status)) {
            criteria.andStatusEqualTo(status);
        }
        if(StringUtils.isNotEmpty(productName)){
            criteria.andProductNameLike("%" + productName + "%");
        }
        if(StringUtils.isNotEmpty(sampleCode)){
            criteria.andSampleCodeLike("%" + sampleCode + "%");
        }
        if(StringUtils.isNotEmpty(orderCode)){
            criteria.andOrderCodeLike("%" + orderCode + "%");
        }
        return productMapper.countByExample(example);
    }

    @Override
    public Product findProductByOrderCode(String orderCode) {
        ProductExample example = new ProductExample();
        ProductExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        criteria.andOrderCodeEqualTo(orderCode);
        List<Product> list = productMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.get(0);
    }

    @Transactional
    @Override
    public int inbound(int productId, int loginId, String loginName) {
        ProductApply productApply = productApplyService.getProductApplyById(productId);
        List<ProductApplyDetail> applyDetailList = productApplyDetailService.getProductApplyDetailByProductId(productId);
        if(Objects.isNull(productApply) && CollectionUtils.isEmpty(applyDetailList)){
            LOGGER.info("成品入库申请单【{}】不存在！", productId);
            return 0;
        }
        Product product = this.findProductByOrderCode(productApply.getOrderCode());
        if(Objects.isNull(product)){
            product = new Product();
            product.setOrderId(productApply.getOrderId());
            product.setOrderCode(productApply.getOrderCode());
            product.setSampleCode(productApply.getSampleCode());
            product.setProductName(productApply.getProductName());
            product.setPhotoUrl(productApply.getPhotoUrl());
            product.setCreateTime(new Date());
            product.setCreater(loginId);
            product.setIsDeleted(DeletedEnum.NO.getKey());
            if(productMapper.insertSelective(product) <= 0){
                LOGGER.info("新增成品库存失败！");
                return 0;
            }
            int totalNum = 0;
            List<ProductDetail> details = Lists.newArrayList();
            for(ProductApplyDetail detail : applyDetailList){
                ProductDetail productDetail = new ProductDetail();
                productDetail.setProductId(product.getId());
                productDetail.setOrderNum(detail.getOrderNum());
                productDetail.setColor(detail.getColor());
                productDetail.setSizeType(detail.getSizeType());
                productDetail.setWarehouseNum(detail.getWarehouseNum());
                totalNum += productDetail.getWarehouseNum();
                details.add(productDetail);
            }
            if(productDetailMapper.insertBatch(details, loginId) < applyDetailList.size()){
                LOGGER.info("新增成品库存详情失败");
                throw new BusinessException("新增成品库存详情失败");
            }
            return 1;
        }

        List<ProductDetail> productDetails = productDetailService.getProductDetailByProductId(product.getId());
        List<ProductDetail> details = Lists.newArrayList();
        for(ProductApplyDetail detail : applyDetailList){
            ProductDetail updateProductDetail = new ProductDetail();
            boolean isUpdate = false;
            for (ProductDetail productDetail: productDetails) {
                if(StringUtils.equals(detail.getSizeType(), productDetail.getSizeType()) && StringUtils.equals(detail.getColor(), productDetail.getColor())){
                    updateProductDetail = productDetail;
                    updateProductDetail.setOrderNum(productDetail.getOrderNum() + detail.getOrderNum());
                    updateProductDetail.setWarehouseNum(productDetail.getWarehouseNum() + detail.getWarehouseNum());
                    isUpdate = true;
                    break;
                }
            }
            if(!isUpdate) {
                updateProductDetail.setProductId(product.getId());
                updateProductDetail.setOrderNum(detail.getOrderNum());
                updateProductDetail.setColor(detail.getColor());
                updateProductDetail.setSizeType(detail.getSizeType());
                updateProductDetail.setWarehouseNum(detail.getWarehouseNum());
            }
            details.add(updateProductDetail);
        }
        ProductDetailExample example = new ProductDetailExample();
        ProductDetailExample.Criteria criteria = example.createCriteria();
        criteria.andProductIdEqualTo(product.getId());
        productDetailMapper.deleteByExample(example);

        if(productDetailMapper.insertBatch(details, loginId) < applyDetailList.size()){
            LOGGER.info("修改-新增成品库存详情失败");
            throw new BusinessException("修改-新增成品库存详情失败");
        }
        if(productApplyService.updateStatus(productId, ProductStatus.FINISHED.getKey(), loginId, ProductStatus.AUDIT.getKey()) <= 0){
            LOGGER.info("更改成品入库申请单失败！");
            throw new BusinessException("更改成品入库申请单失败！");
        }

        return 1;
    }

    @Transactional
    @Override
    public Map<String, Object> outbound(int outboundId, int loginId, String loginName) {
        Map<String, Object> result = Maps.newHashMap();
        ProductOutbound productOutbound = productOutboundService.getProductOutboundById(outboundId);
        List<ProductOutboundDetail> outboundDetailList = productOutboundDetailService.getProductOutboundDetailByProductId(outboundId);
        if(Objects.isNull(productOutbound) || CollectionUtils.isEmpty(outboundDetailList)){
            result.put("success", 0);
            result.put("message", "成品出货申请单或详情不存在！");
            return result;
        }
        Product product = this.findProductByOrderCode(productOutbound.getOrderCode());
        if(Objects.isNull(product)){
            result.put("success", 0);
            result.put("message", "成品库存不足！");
            return result;
        }
        List<ProductDetail> productDetailList = productDetailService.getProductDetailByProductId(product.getId());
        if(Objects.isNull(product) || CollectionUtils.isEmpty(productDetailList)){
            result.put("success", 0);
            result.put("message", "成品库存不足！");
            return result;
        }
        for (ProductOutboundDetail outboundDetail: outboundDetailList) {
            boolean isSuccess = false;
            ProductDetail updateProductDetail = null;
            for (ProductDetail productDetail: productDetailList){
                if(StringUtils.equals(outboundDetail.getSizeType(), productDetail.getSizeType())
                        && productDetail.getWarehouseNum() >= outboundDetail.getWarehouseNum()){
                    productDetail.setOrderNum(productDetail.getOrderNum() - outboundDetail.getOrderNum());
                    productDetail.setWarehouseNum(productDetail.getWarehouseNum() - outboundDetail.getWarehouseNum());
                    productDetail.setUpdateTime(new Date());
                    updateProductDetail = productDetail;
                    isSuccess = true;
                    continue;
                }
            }
            if(!isSuccess){
                LOGGER.info("{}{}库存不足！", outboundDetail.getColor(), outboundDetail.getWarehouseNum());
                throw new BusinessException(outboundDetail.getColor() + outboundDetail.getWarehouseNum() + "库存不足！");
            }
            if(productDetailMapper.updateByPrimaryKeySelective(updateProductDetail) <= 0){
                LOGGER.info("{}{}更改库存失败！", outboundDetail.getColor(), outboundDetail.getWarehouseNum());
                throw new BusinessException(outboundDetail.getColor() + outboundDetail.getWarehouseNum() + "更改库存失败！");
            }
        }
        if(productOutboundService.updateStatus(outboundId, ProductStatus.FINISHED.getKey(), loginId,
                ProductStatus.FINISHED.getKey()) <= 0){
            LOGGER.info("更改成品出库申请【{}】单状态失败！", outboundId);
            throw new BusinessException("更改成品出库申请【"  +outboundId + "】单状态失败！");
        }

        result.put("success", 1);
        result.put("message", "成品出货成功！");
        return result;
    }

}
