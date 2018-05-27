package com.zero.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zero.common.utils.excel.ExportConfig;

import java.math.BigDecimal;
import java.util.Date;

@ExportConfig.SheetName
public class Order {
    @ExportConfig.ColumnName(value = "id", order = 0)
    private Integer id;
    @ExportConfig.ColumnName(value = "订单数量", order = 2)
    private Integer orderNum;

    @ExportConfig.ColumnName(value = "产品名称", order = 1)
    private String productName;

    @ExportConfig.ColumnName(value = "单位", order = 3)
    private String productUnit;

    @ExportConfig.ColumnName(value = "交付时间", order = 4, dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deliverDate;

    @ExportConfig.ColumnName(value = "单价", order = 5)
    private BigDecimal unitPrice;

    @ExportConfig.ColumnName(value = "总价", order = 6)
    private BigDecimal totalPrice;

    @ExportConfig.ColumnName(value = "合作公司", order = 7)
    private String cooperationCompany;

    @ExportConfig.ColumnName(value = "合作人", order = 8)
    private String contactName;

    @ExportConfig.ColumnName(value = "联系电话", order = 9)
    private String contactPhone;

    @ExportConfig.ColumnName(value = "下单时间", order = 10, dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date orderTime;

    private Integer sampleId;

    @ExportConfig.ColumnName(value = "状态", order = 11, codeName = "com.zero.common.enmu.OrderStatus")
    private Integer status;

    private Integer isDeleted;

    @ExportConfig.ColumnName(value = "创建时间", order = 12, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private Date updateTime;

    private Integer creater;

    private Integer modifier;

    private String remarks;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName == null ? null : productName.trim();
    }

    public String getProductUnit() {
        return productUnit;
    }

    public void setProductUnit(String productUnit) {
        this.productUnit = productUnit == null ? null : productUnit.trim();
    }

    public Date getDeliverDate() {
        return deliverDate;
    }

    public void setDeliverDate(Date deliverDate) {
        this.deliverDate = deliverDate;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCooperationCompany() {
        return cooperationCompany;
    }

    public void setCooperationCompany(String cooperationCompany) {
        this.cooperationCompany = cooperationCompany == null ? null : cooperationCompany.trim();
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName == null ? null : contactName.trim();
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone == null ? null : contactPhone.trim();
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

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }
}