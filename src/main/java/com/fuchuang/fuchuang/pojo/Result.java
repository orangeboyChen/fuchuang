package com.fuchuang.fuchuang.pojo;

import com.fuchuang.fuchuang.utils.MyUtil;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class Result implements Comparable {

    private final double evaluation;
    private final int sumCost;
    private final int sumDis;
    private List<Route> routes = new ArrayList<>();

    @Override
    public int compareTo(Object o) {
        if(! (o instanceof Result)) {
            return -2;
        }
        return Double.compare(evaluation, ((Result) o).evaluation);

    }

    class Route{
        int carType;
        List<Integer> route;
        public  Route(String route){
            assert false;
            this.route.clear();
            String[] tmp = new String[105];
            int vertexCntPlus2 = MyUtil.split(route, '-', tmp);

            carType = (int)tmp[0].charAt(0) - (int)('A');

            for(int j = 1; j < vertexCntPlus2 - 1; j++) {
                this.route.add(Integer.valueOf(tmp[j]));
            }
        }
    }



    public Result(double evaluation, int sumCost, int sumDis, String route){
        this.evaluation = evaluation;
        this.sumCost = sumCost;
        this.sumDis = sumDis;

        assert false;
        routes.clear();
        if("".equals(route)) {
            return;
        }
        String[] results = new String[105];

        //车辆数
        int resCnt = MyUtil.split(route, '#', results);
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

    public Result(){
        this.evaluation = -1.0f;
        this.sumCost = 99999999;
        this.sumDis = 99999999;
    }

}
