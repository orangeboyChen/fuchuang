package com.fuchuang.fuchuang.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fuchuang.fuchuang.pojo.Node;
import com.fuchuang.fuchuang.pojo.Truck;
import com.fuchuang.fuchuang.service.C2JService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author orangeboy
 * @version 1.0
 * @date 2020/5/16 19:08
 */
@RestController
@CrossOrigin
public class FuchuangController {

    @Autowired
    private C2JService c2JService;

    @PostMapping(value = "/", produces = "application/json; charset=UTF-8")
    public HashMap<String, Object> get(@RequestBody JSONObject jsonObject){


        //Nodes
        System.out.println(jsonObject.getString("node"));

        List<Node> node1 = JSONArray.parseArray(jsonObject.getJSONArray("node").toJSONString(), Node.class);
        Node[] nodes = node1.toArray(new Node[0]);

        //Graph(Path)
        JSONArray graphJSON = jsonObject.getJSONArray("graph");
        int length = graphJSON.size();
        int[][] graph = new int[length][length];
        for (int i = 0; i < length; i++) {
            for(int j = 0; j < length; j++){
                graph[i][j] = graphJSON.getJSONArray(i).getIntValue(j);
            }
        }

        //Trucks
        List<Truck> truck1 = JSONArray.parseArray(jsonObject.getJSONArray("truck").toJSONString(), Truck.class);
        Truck[] trucks = truck1.toArray(new Truck[0]);


        //Load
        int load = jsonObject.getIntValue("load");


        System.out.println();
        return null;
    }
}
