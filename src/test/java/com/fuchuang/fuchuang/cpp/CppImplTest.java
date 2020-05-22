package com.fuchuang.fuchuang.cpp;

import com.fuchuang.fuchuang.pojo.Result;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.TIMEOUT;

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

    int[][] graph = new int[][]{
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

    @Test
    void solve() {


        CppImpl cpp = new CppImpl();
        System.out.println(cpp.solve(11, graph, demand2,
                2, new int[]{200, 400}, new int[]{35, 35}, new int[]{2, 5},
                40, 30, 30, 0, 0));
    }

    @Test
    void solve2(){
        Cpp cpp = new CppImpl2();
        System.out.println(cpp.solve(11, graph, demand,
                2, new int[]{200, 400}, new double[]{35, 35}, new double[]{2, 5},
                40, 30, 30, 0, 0));

    }

    @Test
    void test3() throws InterruptedException, ExecutionException, TimeoutException {
        int taskSum = 1000;
        List<FutureTask<Result>> futureTasks = new ArrayList<>(taskSum);
        ExecutorService executorService = Executors.newFixedThreadPool(taskSum);

        Callable<Result> callable = () -> {
            Cpp cpp = new CppImpl2();
            return cpp.solve(11, graph, demand,
                    2, new int[]{200, 400}, new double[]{35, 35}, new double[]{2, 5},
                    40, 30, 30, 0, 0);
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

        System.out.println("最好结果：" + bestResult);
    }
}