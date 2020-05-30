package com.fuchuang.fuchuang.cpp;

import com.fuchuang.fuchuang.pojo.Result;
import org.junit.jupiter.api.Test;
//import org.omg.CORBA.TIMEOUT;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author orangeboy
 * @version 1.0
 * @date 2020/5/22 2:37
 */
class CppImplTest {

    double[][] graph = new double[][]{
            {0,5,8,7,0,4,12,9,12,6,5},
            {5,0,4,0,0,0,0,0,0,3,0},
            {8,4,0,3,0,0,0,0,0,0,0},
            {7,0,3,0,4,0,0,0,0,0,5},
            {0,0,0,4,0,3,0,0,0,0,2},
            {4,0,0,0,3,0,10,0,0,0,2},
            {12,0,0,0,0,10,0,4,7,0,0},
            {9,0,0,0,0,0,4,0,5,0,0},
            {12,0,0,0,0,0,0,5,0,9,0},
            {6,3,0,0,0,0,0,0,9,0,0},
            {5,0,0,5,2,2,0,0,0,0,0}
    };

    double[] demand = new double[]{
            3,3.5,0.8,4,2.8,1.9,3.5,0.9,0.8,1.2,4.9
    };

    int[] demand2 = new int[]{
            3,3,1,4,2,1,3,1,1,1,4
    };

    double[][] graph2 = new double[][]{
            {  0,	7.4,	12.1,	5.3,	6.6, 8.2	,11.9,	11.2,	10.8},
            {  7.4,	0,	5.8,	9,	7.1,	11.4,	12.8,	6.9,	10.7},
            {  12.1,	5.8,	0,	12.2,	9.4,	10.1,	13.7,	2.8,	9},
            {  5.3,	9,	12.2,	0,	4.9,	4.1,	7.8,	11.9,	8.5},
            {  6.6,	7.1,	9.4,	4.9,	0,	4,	6,	6.6,	4},
            {  8.2,	11.4,	10.1,	4.1,	4,	0,	3.9,	10,	4.4},
            {  11.9,	12.8,	13.7,	7.8,	6,	3.9,	0,	10.8,	5.7},
            {  11.2,	6.9,	2.8,	11.9,	6.6,	10,	10.8,	0,	5.5},
            {  10.8,	10.7,	9,	8.5,	4,	4.4,	5.7,	5.5,	0}
    };

    double[] demand3 = new double[]{
            0, 2,	1.5,	4.5,	3,	1.5,	4,	2.5,	3
    };



    @Test
    void solve() {
//
//
//        CppImpl cpp = new CppImpl();
//        System.out.println(cpp.solve(11, graph, demand2,
//                2, new int[]{200, 400}, new int[]{35, 35}, new int[]{2, 5},
//                40, 30, 30, 0, 0));
    }

    @Test
    void solve2(){
        Cpp cpp = new CppImpl2();
        System.out.println(cpp.solve(11, graph, demand,
                2, new double[]{200, 400}, new double[]{35, 35}, new double[]{2, 5},
                40, 30, 30, 0, 0));

    }

    @Test
    void test3() throws InterruptedException, ExecutionException, TimeoutException {
        int taskSum = 100;
        List<FutureTask<Result>> futureTasks = new ArrayList<>(taskSum);
        ExecutorService executorService = Executors.newFixedThreadPool(taskSum);

        Callable<Result> callable = () -> {
            Cpp cpp = new CppImpl2();
            return cpp.solve(11, graph, demand,
                    2, new double[]{1, 1}, new double[]{35, 35}, new double[]{2, 5},
                    40, 30, 30, 0, 0);

//            return cpp.solve(graph2.length, graph2, demand3,
//                    1, new int[]{1}, new double[]{99999}, new double[]{8},
//                    85, 30, 30, 0, 0);
        };

        for (int i = 0; i < taskSum; i++) {
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
        System.out.println("\n车总数：" + bestResult.getRoutes().size());
        System.out.println("最好结果：" + bestResult);
    }
}