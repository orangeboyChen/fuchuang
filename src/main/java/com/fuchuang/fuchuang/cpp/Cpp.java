package com.fuchuang.fuchuang.cpp;

import com.fuchuang.fuchuang.pojo.Result;

/**
 * @author orangeboy
 * @version 1.0
 * @date 2020/5/19 20:20
 */

public interface Cpp {
    /**
     * 通过算法获得解
     * @param vCnt 顶点数
     * @param graph 边长邻接表
     * @param demand 需求表
     * @param carCnt 车辆种类数量
     * @param carCost 车的费用
     * @param carMaxDis 车的最大里程数
     * @param carMaxLoad 车的最大装载量
     * @param affectFullLoad 满载率影响参数
     * @param affectSumDis 总路程影响参数
     * @param affectSumCost 总费用影响参数
     * @param fixTimeCost 每个点的固定卸货时间
     * @param carVel 车辆速度
     * @return 最好的结果
     */
    public Result solve(int vCnt, int[][] graph, double[] demand,
                        int carCnt, int[] carCost, double[] carMaxDis, double[] carMaxLoad,
                        int affectFullLoad, int affectSumDis, int affectSumCost,
                        int fixTimeCost, int carVel);
}
