package com.zero.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class Plan {
    private Integer id;

    private Integer orderId;

    private String sampleCode;

    private String orderCode;

    private String productName;

    private String photoUrl;

    private Integer deptId;

    private String deptName;

    private String productionProcesses;

    private Integer billingId;

    private String billingUser;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date billingDate;

    private Integer auditId;

    private String auditUser;

    private Date auditDate;

    private Integer num;

    private Integer status;

    private Integer isDeleted;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;

    private Date updateTime;

    private Integer creater;

    private Integer modifier;

    private String remarks;

    private List<PlanDetail> detailList;

    private List<PlanMaterial> materialList;

    private Integer totalNum;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getSampleCode() {
        return sampleCode;
    }

    public void setSampleCode(String sampleCode) {
        this.sampleCode = sampleCode == null ? null : sampleCode.trim();
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

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName == null ? null : deptName.trim();
    }

    public String getProductionProcesses() {
        return productionProcesses;
    }

    public void setProductionProcesses(String productionProcesses) {
        this.productionProcesses = productionProcesses == null ? null : productionProcesses.trim();
    }

    public Integer getBillingId() {
        return billingId;
    }

    public void setBillingId(Integer billingId) {
        this.billingId = billingId;
    }

    public String getBillingUser() {
        return billingUser;
    }

    public void setBillingUser(String billingUser) {
        this.billingUser = billingUser == null ? null : billingUser.trim();
    }

    public Date getBillingDate() {
        return billingDate;
    }

    public void setBillingDate(Date billingDate) {
        this.billingDate = billingDate;
    }

    public Integer getAuditId() {
        return auditId;
    }

    public void setAuditId(Integer auditId) {
        this.auditId = auditId;
    }

    public String getAuditUser() {
        return auditUser;
    }

    public void setAuditUser(String auditUser) {
        this.auditUser = auditUser == null ? null : auditUser.trim();
    }

    public Date getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(Date auditDate) {
        this.auditDate = auditDate;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
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

    public List<PlanDetail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<PlanDetail> detailList) {
        this.detailList = detailList;
    }

    public List<PlanMaterial> getMaterialList() {
        return materialList;
    }

    public void setMaterialList(List<PlanMaterial> materialList) {
        this.materialList = materialList;
    }

    public Integer getTotalNum() {
        if(CollectionUtils.isEmpty(this.getDetailList())){
            return 0;
        }
        return Optional.ofNullable(this.getDetailList()).orElse(Lists.newArrayList()).stream()
                .map(PlanDetail::getOrderNum).reduce((a, b) -> a + b).get();
    }
}