package com.zero.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class Order {
    private Integer id;

    private Integer sampleId;

    private String orderCode;

    private String deliveryNumber;

    private String poNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date orderTime;

    private Integer type;

    private String sampleCode;

    private String needle;

    private String style;

    private Integer status;

    private String material;

    private String company;

    private String brand;

    private String photoUrl;

    private Integer isDeleted;

    private Date createTime;

    private Date updateTime;

    private Integer creater;

    private Integer modifier;

    private String remarks;

    private Integer winningBid;

    private Integer totalNum;

    private List<OrderDetail> details;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode == null ? null : orderCode.trim();
    }

    public String getDeliveryNumber() {
        return deliveryNumber;
    }

    public void setDeliveryNumber(String deliveryNumber) {
        this.deliveryNumber = deliveryNumber == null ? null : deliveryNumber.trim();
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber == null ? null : poNumber.trim();
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getSampleCode() {
        return sampleCode;
    }

    public void setSampleCode(String sampleCode) {
        this.sampleCode = sampleCode == null ? null : sampleCode.trim();
    }

    public String getNeedle() {
        return needle;
    }

    public void setNeedle(String needle) {
        this.needle = needle == null ? null : needle.trim();
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style == null ? null : style.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material == null ? null : material.trim();
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company == null ? null : company.trim();
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand == null ? null : brand.trim();
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl == null ? null : photoUrl.trim();
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

    public Integer getWinningBid() {
        return winningBid;
    }

    public void setWinningBid(Integer winningBid) {
        this.winningBid = winningBid;
    }

    public List<OrderDetail> getDetails() {
        return details;
    }

    public void setDetails(List<OrderDetail> details) {
        this.details = details;
    }

    public Integer getTotalNum() {
        if(CollectionUtils.isEmpty(this.getDetails())){
            return 0;
        }
        return Optional.ofNullable(this.getDetails()).orElse(Lists.newArrayList()).stream()
            .map(OrderDetail::getNum).reduce((a, b) -> a + b).get();
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", sampleId=" + sampleId +
                ", orderCode='" + orderCode + '\'' +
                ", deliveryNumber='" + deliveryNumber + '\'' +
                ", poNumber='" + poNumber + '\'' +
                ", orderTime=" + orderTime +
                ", type=" + type +
                ", sampleCode='" + sampleCode + '\'' +
                ", needle='" + needle + '\'' +
                ", style='" + style + '\'' +
                ", status=" + status +
                ", material='" + material + '\'' +
                ", company='" + company + '\'' +
                ", brand='" + brand + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", isDeleted=" + isDeleted +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", creater=" + creater +
                ", modifier=" + modifier +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}