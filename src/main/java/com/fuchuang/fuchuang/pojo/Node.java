package com.fuchuang.fuchuang.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author orangeboy
 * @version 1.0
 * @date 2020/5/19 23:52
 */
@Data
public class Node implements Serializable {
    private String name;
    private double need;

}
