package com.fuchuang.fuchuang.cpp;

import java.util.List;

public class Solve implements Cpp{
    private static  final  int RUNTIMES = 5; //多线程次数
    public static int finishedCnt = 0;

    private List<CppImpl> problems;
    @Override
    public Result get(int vCnt, int[][] graph, int[] demand, int carCnt, int[] carCost, int[] carMaxDis, int[] carMaxLoad, int affectFullLoad, int affectSumDis, int affectSumCost, int fixTimeCost, int carVel) {
        //多线程RUNTIMES次
        for(int i = 0; i < RUNTIMES; i++){
            CppImpl tmp =new CppImpl();
            tmp.setAll(vCnt, graph, demand, carCnt, carCost, carMaxDis, carMaxLoad, affectFullLoad, affectSumDis, affectSumCost, fixTimeCost, carVel);
            problems.add(tmp);
        }
        for(int i = 0; i < RUNTIMES; i++){
            problems.get(i).start();
        }

        //检查所有线程是否都已经执行完毕（在Cppimpl 中， 每个线程执行完毕后 finishCnt会+1）
        while(finishedCnt != RUNTIMES){}

        return CppImpl.getBestRes();
    }
}
