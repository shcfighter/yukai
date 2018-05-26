package com.zero.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zero.common.utils.excel.ExportConfig;

import java.math.BigDecimal;
import java.util.Date;

@ExportConfig.SheetName
public class Goods {
    @ExportConfig.ColumnName(value = "id", order = 0)
    private Integer id;

    @ExportConfig.ColumnName(value = "物品名称", order = 1)
    private String goodsName;

    @ExportConfig.ColumnName(value = "物品型号", order = 2)
    private String goodsModel;

    @ExportConfig.ColumnName(value = "类型", order = 3, codeName = "com.zero.common.enmu.GoodsType")
    private Integer type;

    @ExportConfig.ColumnName(value = "颜色", order = 4)
    private String color;

    @ExportConfig.ColumnName(value = "数量", order = 5)
    private Integer num;

    @ExportConfig.ColumnName(value = "单位", order = 6)
    private String unit;

    @ExportConfig.ColumnName(value = "批次号", order = 7)
    private String batchNo;

    @ExportConfig.ColumnName(value = "单价", order = 8)
    private BigDecimal unitPrice;

    @ExportConfig.ColumnName(value = "总价", order = 9)
    private BigDecimal totalPrice;

    @ExportConfig.ColumnName(value = "生产日期", order = 10)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date produceDate;

    @ExportConfig.ColumnName(value = "过期日期", order = 11)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expiredDate;

    private Integer isDeleted;

    @ExportConfig.ColumnName(value = "入库时间", order = 12, dateFormat = "yyyy-MM-dd HH:mm:ss")
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

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName == null ? null : goodsName.trim();
    }

    public String getGoodsModel() {
        return goodsModel;
    }

    public void setGoodsModel(String goodsModel) {
        this.goodsModel = goodsModel == null ? null : goodsModel.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color == null ? null : color.trim();
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit == null ? null : unit.trim();
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo == null ? null : batchNo.trim();
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

    public Date getProduceDate() {
        return produceDate;
    }

    public void setProduceDate(Date produceDate) {
        this.produceDate = produceDate;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
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
}