package com.zero.service.impl;

import com.zero.mapper.ColorMapper;
import com.zero.model.Color;
import com.zero.service.IColorService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ColorServiceImpl implements IColorService {

    @Resource
    private ColorMapper colorMapper;

    @Override
    public List<Color> findColor() {
        return colorMapper.selectByExample(null);
    }
}
