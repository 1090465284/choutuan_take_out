package com.zyh.choutuan_take_out.common;

import com.zyh.choutuan_take_out.entity.Employee;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class R<T> {
    private Integer code;

    private String msg;

    private T data;

    private Map map = new HashMap();

    public static <T> R<T> success(T t){
        R<T> r = new R<>();
        r.setCode(1);
        r.setData(t);
        return r;
    }

    public static <T> R<T> error(String msg){
        R<T> r = new R<>();
        r.setCode(0);
        r.setMsg(msg);
        return r;
    }

    public R<T> add(String key, Object value){
        this.map.put(key, value);
        return this;
    }
}
