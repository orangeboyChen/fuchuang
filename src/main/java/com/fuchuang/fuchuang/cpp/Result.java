package com.fuchuang.fuchuang.cpp;

import java.util.List;

public class Result {
    private double evaluation;
    private int sumCost;
    private int sumDis;

    class Route{
        int carType;
        List<Integer> route;
        public  Route(String _route){
            route.clear();
            String tmp[] = new String[105];
            int vertexCntPlus2 = MyUtil.split(_route, '-', tmp);

            carType = (int)tmp[0].charAt(0) - (int)('A');

            for(int j = 1; j < vertexCntPlus2 - 1; j++)
                route.add(Integer.valueOf(tmp[j]));
        }
    }

    private List<Route> routes;

    public Result(double _evaluation, int _sumCost, int _sumDis, String _route){
        evaluation = _evaluation;
        sumCost = _sumCost;
        sumDis = _sumDis;
        routes.clear();
        if(_route == "") return;
        String result = _route, results[] = new String[105];
        int resCnt = MyUtil.split(result, '#', results);  //车辆数
        for(int i = 0; i < resCnt; i++) {
            Route newRoute = new Route(results[i]);
            routes.add(newRoute);
        }
    }

    public double getEvaluation(){
        return evaluation;
    }

    public int getSumCost(){
        return sumCost;
    }

    public int getSumDis(){
        return sumDis;
    }

    public List<Route> getRoutes(){
        return routes;
    }
}
