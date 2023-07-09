package com.zyh.choutuan_take_out.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


/**
 * 自定义元数据处理器
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        if(metaObject.hasSetter("createTime")){
            metaObject.setValue("createTime", LocalDateTime.now());
        }

        if(metaObject.hasSetter("createUser")){
            metaObject.setValue("createUser", BaseContext.getCurrentId());
        }
        if(metaObject.hasSetter("updateTime")){
            metaObject.setValue("updateTime", LocalDateTime.now());
        }

        if(metaObject.hasSetter("updateUser")){
            metaObject.setValue("updateUser", BaseContext.getCurrentId());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if(metaObject.hasSetter("updateTime")){
            metaObject.setValue("updateTime", LocalDateTime.now());
        }

        if(metaObject.hasSetter("updateUser")){
            metaObject.setValue("updateUser", BaseContext.getCurrentId());
        }

    }
}
