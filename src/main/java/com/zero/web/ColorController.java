package com.zero.web;

import com.zero.common.Result;
import com.zero.model.Color;
import com.zero.service.IColorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/color")
public class ColorController {
	private static final Logger LOGGER = LogManager.getLogger(ColorController.class);

	@Resource
	private IColorService colorService;

	@GetMapping("/findColor")
	public Result<List<Color>> findColor(){
		return Result.resultSuccess(colorService.findColor());
	}

}
