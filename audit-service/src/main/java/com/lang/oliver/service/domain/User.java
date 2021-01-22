package com.lang.oliver.service.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.persistence.Id;

@Data
public class User {

    @Id
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String address;


}
