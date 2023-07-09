package com.zyh.choutuan_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyh.choutuan_take_out.entity.Category;

public interface CategoryService extends IService<Category> {
    public boolean removeCategory(Long id);
}
