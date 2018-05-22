package com.zero.service;

import com.zero.model.SampleMaterial;

import java.util.List;

public interface IMaterialService {

    SampleMaterial getMaterialById(int id);

    int delete(int id, int loginId);

    int insert(SampleMaterial sampleMaterial, int loginId);

    int update(SampleMaterial sampleMaterial, int loginId);

    int insertBatch(List<SampleMaterial> list, int loginId);

    List<SampleMaterial> findMaterial(int sampleId);
}
