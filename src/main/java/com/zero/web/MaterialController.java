package com.zero.web;

import com.zero.common.Result;
import com.zero.common.utils.SessionUtils;
import com.zero.model.SampleMaterial;
import com.zero.service.IMaterialService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/material/")
public class MaterialController {

    private static final Logger LOGGER = LogManager.getLogger(MaterialController.class);

    @Resource
    private IMaterialService materialService;

    @GetMapping("delete/{id}")
    public Result<String> delete(HttpServletRequest request, @PathVariable("id") int id){
        if(materialService.delete(id, SessionUtils.getCurrentUserId(request)) <= 0){
            return Result.resultFailure("删除原材料失败！");
        }
        return Result.resultSuccess("删除原材料成功！");
    }

    @GetMapping("getMaterial/{id}")
    public Result<SampleMaterial> getMaterialById(@PathVariable("id") int id){
        return Result.resultSuccess(materialService.getMaterialById(id));
    }

    @GetMapping("findMaterial/{sampleId}")
    public Result<List<SampleMaterial>> findMaterial(@PathVariable("sampleId") int sampleId){
        return Result.resultSuccess(materialService.findMaterial(sampleId));
    }
}
