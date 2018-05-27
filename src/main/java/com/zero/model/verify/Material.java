package com.zero.model.verify;

import com.zero.model.Sample;
import com.zero.model.SampleMaterial;
import lombok.Data;

import java.util.List;
@Data

public class Material {

    private Sample sample;
    private List<SampleMaterial> materialList;
    private String sampleUrls;
}
