package com.fuchuang.fuchuang.pojo;

import com.fuchuang.fuchuang.utils.MyUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ToString
@NoArgsConstructor
@Data
class Route {
    int carType;
    List<Integer> route = new ArrayList<>();
    public  Route(String route){
        this.route.clear();
        String[] tmp = new String[105];
        int vertexCntPlus2 = MyUtil.split(route, '-', tmp);

        carType = (int)tmp[0].charAt(0) - (int)('A');

        for(int j = 1; j < vertexCntPlus2 - 1; j++) {
            this.route.add(Integer.valueOf(tmp[j]));
        }
    }
}

