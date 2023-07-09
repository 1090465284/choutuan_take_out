package com.zyh.choutuan_take_out.dto;


import com.zyh.choutuan_take_out.entity.Dish;
import com.zyh.choutuan_take_out.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
