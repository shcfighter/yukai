package com.zero.web;

import com.zero.common.Result;
import com.zero.mapper.ColorMapper;
import com.zero.model.Color;
import com.zero.model.example.ColorExample;
import com.zero.service.IColorService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/color")
public class ColorController {
	private static final Logger LOGGER = LogManager.getLogger(ColorController.class);

	@Resource
	private IColorService colorService;
	@Resource
	private ColorMapper colorMapper;

	@GetMapping("/findColor")
	public Result<List<Color>> findColor(String colorName, String colorCode){
		if(StringUtils.isEmpty(colorName)){
			return Result.resultSuccess(colorService.findColor());
		}
		ColorExample example = new ColorExample();
		ColorExample.Criteria criteria = example.createCriteria();
		if(StringUtils.isNotEmpty(colorName)){
			criteria.andColorNameLike("%" + colorName + "%");
		}
		if(StringUtils.isNotEmpty(colorCode)) {
			criteria.andColorCodeLike("%" + colorCode + "%");
		}
		return Result.resultSuccess(colorMapper.selectByExample(example));
	}

	@PostMapping("/insertColor")
	public Result<String> insertColor(@RequestBody Color color){
		colorMapper.insertSelective(color);
		return Result.resultSuccess("颜色增加成功！");
	}

	@PostMapping("/batchDelete")
	public Result<String> batchDelete(@RequestParam("ids[]") List<Integer> ids){
		if(CollectionUtils.isEmpty(ids)){
			return Result.resultFailure("删除颜色信息失败，未选中订单！");
		}
		AtomicInteger success = new AtomicInteger(0);
		ids.forEach(id -> {
			if(colorMapper.deleteByPrimaryKey(id) > 0){
				success.getAndAdd(1);
			}
		});
		return Result.resultSuccess("成功删除【" + success.get() + "】条颜色信息！");
	}

}
