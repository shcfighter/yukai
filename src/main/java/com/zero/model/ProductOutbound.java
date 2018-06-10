package com.zero.model;

import java.util.Date;
import java.util.List;

public class ProductOutbound {
    private Integer id;

    private String poNumber;

    private String deliveryNumber;

    private String sampleCode;

    private Integer orderId;

    private String orderCode;

    private String productName;

    private String photoUrl;

    private String company;

    private String warehouseUser;

    private Date warehouseDate;

    private Integer status;

    private Integer isDeleted;

    private Date createTime;

    private Date updateTime;

    private Integer creater;

    private Integer modifier;

    private String remarks;

    private List<ProductOutboundDetail> detailList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber == null ? null : poNumber.trim();
    }

    public String getDeliveryNumber() {
        return deliveryNumber;
    }

    public void setDeliveryNumber(String deliveryNumber) {
        this.deliveryNumber = deliveryNumber == null ? null : deliveryNumber.trim();
    }

    public String getSampleCode() {
        return sampleCode;
    }

    public void setSampleCode(String sampleCode) {
        this.sampleCode = sampleCode == null ? null : sampleCode.trim();
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode == null ? null : orderCode.trim();
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName == null ? null : productName.trim();
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl == null ? null : photoUrl.trim();
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company == null ? null : company.trim();
    }

    public String getWarehouseUser() {
        return warehouseUser;
    }

    public void setWarehouseUser(String warehouseUser) {
        this.warehouseUser = warehouseUser == null ? null : warehouseUser.trim();
    }

    public Date getWarehouseDate() {
        return warehouseDate;
    }

    public void setWarehouseDate(Date warehouseDate) {
        this.warehouseDate = warehouseDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getCreater() {
        return creater;
    }

    public void setCreater(Integer creater) {
        this.creater = creater;
    }

    public Integer getModifier() {
        return modifier;
    }

    public void setModifier(Integer modifier) {
        this.modifier = modifier;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }

    public List<ProductOutboundDetail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<ProductOutboundDetail> detailList) {
        this.detailList = detailList;
    }
}