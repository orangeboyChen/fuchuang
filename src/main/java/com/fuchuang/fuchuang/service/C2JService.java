package com.fuchuang.fuchuang.service;

import com.fuchuang.fuchuang.cpp.Cpp;
import com.fuchuang.fuchuang.cpp.CppImpl2;
import com.fuchuang.fuchuang.pojo.Result;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author orangeboy
 * @version 1.0
 * @date 2020/5/18 23:03
 */

@Service
public class C2JService {

    private final int taskCount = 50;

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
    public Result solve(int vCnt, double[][] graph, double[] demand, int carCnt, double[] carCost, double[] carMaxDis, double[] carMaxLoad, int affectFullLoad, int affectSumDis, int affectSumCost, int fixTimeCost, int carVel) throws InterruptedException, ExecutionException, TimeoutException
    {
        List<FutureTask<Result>> futureTasks = new ArrayList<>(taskCount);
        ExecutorService executorService = Executors.newFixedThreadPool(taskCount);

        Callable<Result> callable = () -> {
            Cpp cpp = new CppImpl2();
            return cpp.solve(vCnt, graph, demand, carCnt, carCost, carMaxDis, carMaxLoad, affectFullLoad, affectSumDis, affectSumCost, fixTimeCost, carVel);
        };

        for (int i = 0; i < taskCount; i++) {
            FutureTask<Result> futureTask = new FutureTask<>(callable);
            futureTasks.add(futureTask);
            executorService.submit(futureTask);
        }

        //最好的结果
        Result bestResult = new Result();

        //结果比较
        for (FutureTask<Result> executedTask : futureTasks) {

            Result executedResult;
            try{
                executedResult = executedTask.get(6, TimeUnit.SECONDS);
            }catch (TimeoutException e){
                System.out.println("线程运行异常");
                executedResult = new Result();
            }

            if(bestResult.compareTo(executedResult) < 0){
                bestResult = executedResult;
            }

        }
        System.out.println(bestResult);
        return bestResult;
    }




}
