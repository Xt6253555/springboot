package com.controller;

import com.pojo.Enterprise;
import com.service.EnterpriseService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/enterprise")
public class EnterpriseController {

    @Autowired
    private EnterpriseService enterpriseService;

    @GetMapping("/search/hotlist")
    public Result hotList(){
        List<Enterprise> enterprises = enterpriseService.hotList("1");
        return new Result(true, StatusCode.OK, "查询成功",enterprises);
    }

    //查询全部
    @GetMapping
    public Result findAll(){
        return new Result(true, StatusCode.OK,"查询成功",enterpriseService.findAll());
    }

    //根据id查询
    @GetMapping("/{id}")
    public Result findById(@PathVariable("id") String id){
        return new Result(true, StatusCode.OK,"查询成功", enterpriseService.findById(id));
    }

    //增加标签
    @PostMapping
    @ResponseBody
    public Result add( Enterprise enterprise){
        enterpriseService.add(enterprise);
        return new Result(true, StatusCode.OK,"增加成功");
    }

    //修改标签
    @PutMapping("/{id}")
    @ResponseBody
    public Result update(@PathVariable("id") String id, Enterprise enterprise){
        enterpriseService.update(enterprise);
        return new Result(true, StatusCode.OK,"修改成功");
    }

    //删除标签
    @DeleteMapping("/{id}")
    public  Result deleteById(@PathVariable("id")String id){
        enterpriseService.deleteById(id);
        return new Result(true, StatusCode.OK,"删除成功");
    }

    //条件查询
    @PostMapping("/search")
    @ResponseBody
    public Result findSearch( Enterprise enterprise){
        List<Enterprise> list = enterpriseService.findSearch(enterprise);
        for (Enterprise s:list) {
            System.out.println(s.getAddress());
        }
        return new Result(true, StatusCode.OK,"查询成功",list);
    }

    //分页查询
    @PostMapping("/search/{page}/{size}")
    @ResponseBody
    public Result pageQuery(@PathVariable("page")int currentPage,
                            @PathVariable("size")int pageSize,
                            Enterprise enterprise){
        Page<Enterprise> pageData = enterpriseService.pageQuery(currentPage, pageSize,enterprise);
        return new Result(true, StatusCode.OK,"查询成功",
                new PageResult<>(pageData.getTotalElements(), pageData.getContent()));
    }

    //图片上传
    @PostMapping("/image")
    public Result uploadImage(@RequestParam("file")MultipartFile file){
        String url = this.enterpriseService.upload(file);
        if(StringUtils.isBlank(url)){
            return new Result(false, StatusCode.ERROR,"上传失败");
        }
        return new Result(true, StatusCode.OK,"上传成功",url);
    }

}
