package com.zero.model.verify;

import com.zero.model.Sample;
import com.zero.model.SampleDetail;
import lombok.Data;

import java.util.List;
@Data
public class SampleDetails {

    private Sample sample;
    private List<SampleDetail> detailList;
    private String sampleUrls;
}
