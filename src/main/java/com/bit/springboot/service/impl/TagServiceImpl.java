package com.bit.springboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bit.springboot.mapper.TagMapper;
import com.bit.springboot.model.entity.Tag;
import com.bit.springboot.service.TagService;
import org.springframework.stereotype.Service;

/**
* @author 86173
* @description 针对表【tag(标签)】的数据库操作Service实现
* @createDate 2024-05-21 11:53:11
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService {

}




