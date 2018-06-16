package com.zero.service;

import com.zero.model.Sample;
import com.zero.model.SampleDetail;

import java.util.List;

public interface ISampleService {

    int insertOrUpdate(Sample sample, List<SampleDetail> list, int loginId);

    List<Sample> findSamplePage(String style, String sampleCode, Integer status, Integer page, Integer pageSize);

    long findSampleRowNum(String style, String sampleCode, Integer status);

    Sample findSampleById(int id);

    int deleteSample(int id, int loginId);

    int updateToFinished(int id, int newStatus, int oldStatus, int loginId);

    boolean isExsitSampleCode(String sampleCode, Integer id);
}
