package com.zero.service;

import com.zero.model.SampleDetail;

import java.util.List;

public interface ISampleDetailService {

    List<SampleDetail> findSampleDetail(int sampleId);

    int insertSampleDetail(SampleDetail detail, int loginId);

    int deleteSampleDetail(int id, int loginId);

    SampleDetail getSampleDetailById(int id);
}
