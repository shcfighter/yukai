package com.zero.service.impl;

import com.google.common.collect.Lists;
import com.zero.common.enmu.DeletedEnum;
import com.zero.common.enmu.ProductStatus;
import com.zero.common.utils.BeanUtils;
import com.zero.mapper.ProductDetailMapper;
import com.zero.mapper.ProductMapper;
import com.zero.model.Product;
import com.zero.model.ProductDetail;
import com.zero.model.example.ProductDetailExample;
import com.zero.model.example.ProductExample;
import com.zero.model.verify.ProductDetails;
import com.zero.service.IProductService;
import com.zero.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProductServiceImpl implements IProductService {

    private static final Logger LOGGER = LogManager.getLogger(ProductServiceImpl.class);
    @Resource
    private ProductMapper productMapper;
    @Resource
    private ProductDetailMapper productDetailMapper;
    @Resource
    private IUserService userService;

    @Transactional
    @Override
    public int insert(ProductDetails productDetails, int loginId) {
        Product product = productDetails.getProduct();
        product.setCreater(loginId);
        product.setStatus(ProductStatus.SAVE.getKey());
        product.setBillingUser(userService.getUserName(loginId));
        product.setCreateTime(new Date());
        if(productMapper.insertSelective(product) <= 0){
            LOGGER.error("新增成品入库申请失败！");
            return 0;
        }
        List<ProductDetail> detailList = productDetails.getDetailList();
        detailList.forEach(d -> d.setProductId(product.getId()));
        productDetailMapper.insertBatch(detailList, loginId);

        return 1;
    }

    @Transactional
    @Override
    public int update(ProductDetails productDetails, int loginId) {
        Product product = productDetails.getProduct();
        Product productDb = this.getProductById(product.getId());
        if(Objects.isNull(productDb)){
            return 0;
        }
        if(ProductStatus.SAVE.getKey() != productDb.getStatus().intValue()){
            return -1;
        }
        BeanUtils.copyProperties(product, productDb);
        productDb.setUpdateTime(new Date());
        productDb.setModifier(loginId);
        if(productMapper.updateByPrimaryKeySelective(productDb) <= 0){
            LOGGER.error("修改成品入库申请失败！");
            return 0;
        }
        ProductDetailExample detailExample = new ProductDetailExample();
        ProductDetailExample.Criteria criteria = detailExample.createCriteria();
        criteria.andProductIdEqualTo(productDb.getId());
        productDetailMapper.deleteByExample(detailExample);
        List<ProductDetail> detailList = productDetails.getDetailList();
        detailList.forEach(d -> d.setProductId(productDb.getId()));
        productDetailMapper.insertBatch(detailList, loginId);

        return 1;
    }

    @Transactional
    @Override
    public int delete(int id, int loginId) {
        Product product = this.getProductById(id);
        if(Objects.isNull(product)){
            return 0;
        }
        if(ProductStatus.SAVE.getKey() != product.getStatus().intValue()){
            return -1;
        }
        if(productMapper.updateByPrimaryKeySelective(product) <= 0){
            return 0;
        }
        product.setIsDeleted(DeletedEnum.YES.getKey());
        product.setUpdateTime(new Date());
        product.setModifier(loginId);
        ProductDetailExample example = new ProductDetailExample();
        ProductDetailExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.YES.getKey());
        ProductDetail detail = new ProductDetail();
        detail.setProductId(product.getId());
        productDetailMapper.updateByExampleSelective(detail, example);

        return 1;
    }

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
    public List<Product> findProductAndDetailList(String productName, String sampleCode, String orderCode, Integer status, Integer pageNum, Integer pageSize) {
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
        return Optional.ofNullable(productMapper.findProductAndDetailList(example)).orElse(Lists.newArrayList());
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

}
