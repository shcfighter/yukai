package com.zero.service;

import com.zero.model.Sample;
import com.zero.model.SampleDetail;
import com.zero.model.SampleMaterial;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ISampleService {

    int insertOrUpdate(Sample sample, List<SampleDetail> list, int loginId);

    List<Sample> findSamplePage(String style, String sampleCode, Integer status, Integer page, Integer pageSize);

    long findSampleRowNum(String style, String sampleCode, Integer status);

    Sample findSampleById(int id);

    int deleteSample(int id, int loginId);
}
