package com.fuchuang.fuchuang.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fuchuang.fuchuang.pojo.Node;
import com.fuchuang.fuchuang.pojo.Result;
import com.fuchuang.fuchuang.pojo.Truck;
import com.fuchuang.fuchuang.service.C2JService;
import com.fuchuang.fuchuang.utils.MyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @author orangeboy
 * @version 1.0
 * @date 2020/5/16 19:08
 */
@RestController
@CrossOrigin
public class FuChuangController {

    /**
     * 前后端接口
     */
    private static final String NODE = "node";
    private static final String GRAPH = "graph";
    private static final String TRUCK = "truck";
    private static final String AFFECT_FULL_LOAD = "affectFullLoad";
    private static final String AFFECT_DISTANCE = "affectDistance";
    private static final String AFFECT_COST = "affectCost";
    public static final String FIX_TIME_COST = "fixTimeCost";
    public static final String CAR_V = "carVel";


    @Autowired
    private C2JService c2JService;

    @Autowired
    private MyUtil myUtil;

    @PostMapping(value = "/", produces = "application/json; charset=UTF-8")
    public MyUtil.ApiVO<Result> get(@RequestBody JSONObject jsonObject) throws InterruptedException, ExecutionException, TimeoutException {

        System.out.println(jsonObject);

        //获取节点
        List<Node> node1 = JSONArray.parseArray(jsonObject.getJSONArray(NODE).toJSONString(), Node.class);
        Node[] nodes = node1.toArray(new Node[0]);

        //获取图
        JSONArray graphJSON = jsonObject.getJSONArray(GRAPH);
        int length = graphJSON.size();
        int[][] graph = new int[length][length];
        for (int i = 0; i < length; i++) {
            for(int j = 0; j < length; j++){
                graph[i][j] = graphJSON.getJSONArray(i).getIntValue(j);
            }
        }

        //获取车辆信息
        List<Truck> truck1 = JSONArray.parseArray(jsonObject.getJSONArray(TRUCK).toJSONString(), Truck.class);
        Truck[] trucks = truck1.toArray(new Truck[0]);



        //需求
        int[] demand = new int[nodes.length];
        for (int i = 0; i < demand.length; i++) {
            demand[i] = nodes[i].getNeed();
        }


        //车辆三要素
        int[] truckFee = new int[trucks.length];
        int[] truckDistance = new int[trucks.length];
        int[] truckLoad = new int[trucks.length];
        for (int i = 0; i < trucks.length; i++) {
            truckFee[i] = trucks[i].getCost();
            truckDistance[i] = trucks[i].getDistance();
            truckLoad[i] = trucks[i].getLoad();
        }

        //三大影响要素
        int affectFullLoad = jsonObject.getIntValue(AFFECT_FULL_LOAD);
        int affectDistance = jsonObject.getIntValue(AFFECT_DISTANCE);
        int affectCost = jsonObject.getIntValue(AFFECT_COST);

        //卸货时间
        int fixTimeCost = jsonObject.getIntValue(FIX_TIME_COST);

        //货车速度
        int carVel = jsonObject.getIntValue(CAR_V);



        Result solve = c2JService.solve(
                nodes.length,
                graph,
                demand,
                trucks.length,
                truckFee,
                truckDistance,
                truckLoad,
                affectFullLoad,
                affectDistance,
                affectCost,
                fixTimeCost,
                carVel
        );

        return myUtil.success(solve);
    }
}
