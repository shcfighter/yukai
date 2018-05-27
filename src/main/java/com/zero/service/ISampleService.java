package com.zero.service;

import com.zero.model.Sample;
import com.zero.model.SampleMaterial;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ISampleService {

    int insertOrUpdate(Sample sample, List<SampleMaterial> list, int loginId);

    List<Sample> findSamplePage(String sampleName, String sampleCode, Integer status, int page, int pageSize);

    long findSampleRowNum(String sampleName, String sampleCode, Integer status);

    Sample findSampleById(int id);

    int deleteSample(int id, int loginId);
}
