package com.fuchuang.fuchuang.cpp;

import java.util.HashMap;

/**
 * @author orangeboy
 * @version 1.0
 * @date 2020/5/19 20:20
 */

public interface Cpp {
    public Result get(int vCnt, int[][] graph, int[] demand,    //顶点数 边长邻接表 需求表
                                       int carCnt, int[] carCost, int[] carMaxDis, int[] carMaxLoad,  //车辆种类数量 车的费用 车的最大里程数 车的最大装载量
                                       int affectFullLoad, int affectSumDis, int affectSumCost,//满载率影响参数 总路程影响参数 总费用影响参数
                                       int fixTimeCost, int carVel);        //每个点的固定卸货时间， 车辆速度
}
