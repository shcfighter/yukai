package com.zero.service;

import com.zero.model.Sample;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ISampleService {

    int insertOrUpdate(Sample sample, int loginId);

    Sample findSample(int orderId);

    List<Map<String, Object>> findSamplePage(String sampleName, String sampleCode, String company, Date beginDate, Date endDate, int page, int pageSize);

    long findSampleRowNum(String sampleName, String sampleCode, String company, Date beginDate, Date endDate);

    int updateStatus(int id, int status, int loginId);
}
