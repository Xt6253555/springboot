package com.service;

import com.dao.EnterpriseDao;
import com.pojo.Enterprise;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import util.IdWorker;

import javax.imageio.ImageIO;
import javax.persistence.criteria.Predicate;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Service
@Transactional
public class EnterpriseService {
    @Autowired
    private EnterpriseDao enterpriseDao;

    @Autowired
    private IdWorker idWorker;

    public List<Enterprise> hotList(String ishot) {
        return enterpriseDao.findByIshot(ishot);
    }

    //查询全部
    public List<Enterprise> findAll(){
        return enterpriseDao.findAll();
    }

    //根据ID查询
    public Enterprise findById(String id){
        return enterpriseDao.findById(id).get();
    }

    //增加标签
    public void add(Enterprise enterprise){
        enterprise.setId(idWorker.nextId()+"");//设置ID
        enterpriseDao.save(enterprise);
    }

    //修改标签
    public void update(Enterprise enterprise){
        enterpriseDao.save(enterprise);
    }

    //根据id删除标签
    public void deleteById(String id){
        enterpriseDao.deleteById(id);
    }

    //封装查询条件
    private Specification<Enterprise> searchMap(Enterprise enterprise){
        return (Specification<Enterprise>) (root,query,cb) ->{
            LinkedList<Predicate> list = new LinkedList<>();
            if(!StringUtils.isEmpty(enterprise.getName())){
                Predicate name = cb.like(root.get("name").as(String.class), "%" + enterprise.getName() + "%");
                list.add(name);
            }

            if(!StringUtils.isEmpty(enterprise.getAddress())){
                Predicate address = cb.like(root.get("address").as(String.class),  enterprise.getAddress());
                list.add(address);
            }
            Predicate[] predicates = new Predicate[list.size()];
            //集合转数组
            predicates = list.toArray(predicates);
            return cb.and(predicates);
        };

    }

    //查询条件方法
    public List<Enterprise> findSearch(Enterprise enterprise) {
        return enterpriseDao.findAll(searchMap(enterprise));
    }

    //分页查询
    public Page<Enterprise> pageQuery(int currentPage, int pageSize,Enterprise enterprise) {
        return enterpriseDao.findAll(searchMap(enterprise), PageRequest.of(currentPage-1,pageSize));
    }
    //图片上传
    //校验图片上传类型
    private static  final List<String> Types = Arrays.asList("image/gif","image/jpeg");
    //校验图片类型内容是否合法
    private static final Logger logger = LoggerFactory.getLogger(EnterpriseService.class);
    public String upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        //校验文本类型
        String  contentType =file.getContentType();
        //contains用法，是否包含括号的内容
        if(!Types.contains(contentType)){
            logger.info("文件类型不合法：{}"+originalFilename);
            return null;
        }
        //校验文件内容
        try {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if(bufferedImage == null){
                logger.info("文件内容不合法：{}"+originalFilename);
                return null;
            }
            //保存到服务器
            file.transferTo(new File("C:\\image\\"+originalFilename));

            //返回url，进行回显
            return "http://image.leyou.com/"+originalFilename;
        } catch (IOException e) {
            logger.info("服务器内部错误：{}"+originalFilename);
            e.printStackTrace();
        }
        return null;
    }
}
