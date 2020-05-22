package com.fuchuang.fuchuang.cpp;

import com.alibaba.fastjson.JSON;
import com.fuchuang.fuchuang.pojo.Result;
import com.fuchuang.fuchuang.utils.MyUtil;

import java.util.Random;

/**
 * @author orangeboy
 * @version 1.0
 * @date 2020/5/19 10:28
 */


public class CppImpl {
    /**
     * 返回的结果
     */
    private static Result bestRes = new Result();

    /**
     * 最大节点数
     */
    private static final int MAX_NODE_SUM = 100;

    /**
     * 所有节点数
     */

    private int nodeSum;

    /**
     * 种群大小
     */
    private static final int POPULATION_SIZE = 50;

    /**
     * 满载率效能占比
     */
    private int ratioOfEnergyAndFullLoad;

    /**
     * 总路程效能占比
     */
    private int ratioOfEnergyAndDistance;

    /**
     * 总价格效能占比
     */
    private int ratioOfEnergyAndPrice;

    /**
     * 作辅助最大
     */
    private final static int INF = 100000;

    /**
     * 总遗传代数
     */
    private static int MAXGEN;

    /**
     * 单条染色体 单种变异方式 概率倒数
     */
    private static int PCHANGE = 1000;

    /**
     * 满载率最小要求（实际上达不到并且不能太高）最大值为 MZ
     */
    private static int LEAST_REQUEST = 32   ;

    public boolean isRunning = false;

//    @Override
    public Result solve(int vCnt, int[][] graph, int[] demand, int carCnt, int[] carCost, int[] carMaxDis, int[] carMaxLoad, int affectFullLoad, int affectSumDis, int affectSumCost, int fixTimeCost, int carVel) {
        this.setAll(vCnt, graph, demand, carCnt, carCost, carMaxDis, carMaxLoad, affectFullLoad, affectSumDis, affectSumCost, fixTimeCost, carVel);
        return this.startCalc();
    }

    //Scanner scanner = new Scanner(System.in);

    class SingleRoad {               //gene路线参数
        double manzai = 0;
        double lc = 0;
        int car = 0;
    }

    double[] temp1 = new double[POPULATION_SIZE];
    double[] temp2 = new double[POPULATION_SIZE];

    int[][] graph;
    int[][] minDistancesOfNodes;
    float[] demands;

    String[] tempStr1 = new String[105];
    String[] tempStr2 = new String[105];
    String[] tempStr3 = new String[105];
    String[] tempStr4 = new String[105];

    //用作辅助计算
    static int fuzhu = 0;

    //用作辅助计算
    static int fuzhu2 = 0;

    //辅助gene变异
    static String fuzhubianyi = "";

    //车辆种类
    int sumOfCarKind = 0;

    //车辆最大限制
    float[] maxLimitationsOfCar;

    //车辆最大里程
    float[] maxDistancesOfCar;

    //车辆费用
    int[] pricesOfCar;

    //最小价格
    static int minPrice = INF;

    //最小路程
    static int minLength = INF;

    static SingleRoad[][] ratioOfFullLoad = new SingleRoad[POPULATION_SIZE][MAX_NODE_SUM];

    //参数和适应度（parameter[0]表示路程  parameter[1]表示总价格）
    static double[][] parameter = new double[3][POPULATION_SIZE];

    static String[] parent = new String[POPULATION_SIZE];
    static String[] son = new String[POPULATION_SIZE];

    int variationTimes = 0;

    public static Result getBestRes(){
        return  bestRes;
    }


    public Result startCalc() {
        //线程运行中
        isRunning = true;

        //初始化参数
        init();

        //产生父代
        createFathers(parent, sumOfCarKind);

        //计算适应度
        adaptabilityCalc(parent);

        //遗传迭代
        for (int i = 0; i < MAXGEN; i++) {
            if (i % 2 != 0) {
                birthASon(son, parent);
                clear();
                adaptabilityCalc(parent);
                floyd();
                geneticVariation(parent);
            }
            else {
                birthASon(parent, son);
                clear();
                adaptabilityCalc(son);
                floyd();
                geneticVariation(son);
            }
        }

        //处理结果
        Result result;
        String[] T1, T2;
        if (MAXGEN % 2 != 0) {
            T1 = son.clone();
            T2 = parent.clone();
        }
        else {
            T1 = parent.clone();
            T2 = son.clone();
            /*zidaishengchengqi(fudai, zidai);
            clear();
            shiyingdujisuan(zidai);
            int cy = zuiyou();
            if (cy != -1) {
                System.out.println("最终的最优解是" + zidai[cy]);

                for (int i = 0; i < NODEN; i++) {
                    if (manzailv[cy][i].manzai != 0) {
                        System.out.println("路线参数为：" + 100 * manzailv[cy][i].manzai / MZ + "(" + manzailv[cy][i].lc + ")");
                    }
                }
                System.out.println("总路程" + canshu[0][cy] + " " + shiyingdu((int) canshu[0][cy], minlength));
                System.out.println("总价格" + canshu[1][cy] + " " + shiyingdu((int) canshu[1][cy], minprice));
                System.out.println("总评价" + canshu[2][cy]);

            }
            else {
                System.out.println("抱歉最终没有得到最优解");
            }


            for (int i = 0; i < ZQSIZE; i++) {
                System.out.println(zidai[i]);
                for (int k = 0; k < NODEN; k++) {
                    if (manzailv[i][k].lc != 0) {
                        System.out.println(100 * manzailv[i][k].manzai / MZ + "(" + manzailv[i][k].lc + ") ");
                    }
                    else {
                        break;
                    }
                }
                System.out.println("\n");
            }*/
        }
        //zidaishengchengqi(zidai, fudai);
        birthASon(T1, T2);
        clear();
        //shiyingdujisuan(fudai);
        adaptabilityCalc(T2);
        int cy = bestSolutionCalc();
        if (cy != -1) {
            /*System.out.println("最终的最优解是" + fudai[cy]);
            System.out.println("最终的最优解是" + T2[cy]);

            for (int i = 0; i < NODEN; i++) {
                if (manzailv[cy][i].manzai != 0) {
                    System.out.println("路线参数为：" + 100 * manzailv[cy][i].manzai / MZ + "(" + manzailv[cy][i].lc + ")");
                }
            }
            System.out.println("总路程" + canshu[0][cy] + " " + shiyingdu((int) canshu[0][cy], minlength));
            System.out.println("总价格" + canshu[1][cy] + " " + shiyingdu((int) canshu[1][cy], minprice));
            System.out.println("总评价" + canshu[2][cy]);*/
            getAdaptability((int) parameter[0][cy], minLength);
            getAdaptability((int) parameter[1][cy], minPrice);
            return new Result(parameter[2][cy], minPrice, minLength, T2[cy]);
//            updateBestRes(result);    //更新最优解
        }
        return null;

//        Solve.finishedCnt ++;

        /*for (int i = 0; i < ZQSIZE; i++) {
            //System.out.println(fudai[i]);
            System.out.println(T2[i]);
            for (int k = 0; k < NODEN; k++) {
                if (manzailv[i][k].lc != 0) {
                    System.out.println(100 * manzailv[i][k].manzai / MZ + "(" + manzailv[i][k].lc + ")" + " ");
                }
                else {
                    break;
                }
            }
            System.out.println("\n");
        }*/
    }

    public void setAll(int vCnt, int[][] graph, int[] demand, int carCnt, int[] carCost, int[] carMaxDis, int[] carMaxLoad, int affectFullLoad, int affectSumDis, int affectSumCost, int fixTimeCost, int carVel)
    {
        nodeSum = vCnt;
        sumOfCarKind = carCnt;
        MAXGEN = (nodeSum -1)*10;
        ratioOfEnergyAndFullLoad = affectFullLoad;
        ratioOfEnergyAndDistance = affectSumDis;
        ratioOfEnergyAndPrice = affectSumCost;

        this.graph = new int[nodeSum + 5][nodeSum + 5];
        minDistancesOfNodes = new int[nodeSum + 5][nodeSum + 5];
        demands = new float[nodeSum + 5];
        maxLimitationsOfCar = new float[sumOfCarKind + 5];
        maxDistancesOfCar = new float[sumOfCarKind + 5];
        pricesOfCar = new int[sumOfCarKind + 5];

        for(int i = 0; i < nodeSum; i++) {
            demands[i] = demand[i];
            for(int j = 0; j < nodeSum; j++){
                this.graph[i][j] = graph[i][j];
            }
        }

        for(int i = 0; i < carCnt; i++){
            maxLimitationsOfCar[i] = carMaxLoad[i];
            maxDistancesOfCar[i] = carMaxDis[i];
            pricesOfCar[i] = carCost[i];
        }
    }



    void init() {       //初始化参数
        floyd();
        for (int i = 0; i < POPULATION_SIZE; i++){
            parent[i] = "";
            son[i] = "";
            parameter[0][i] = 0;
            parameter[1][i] = 0;
            parameter[2][i] = 0;
            temp1[i] = 0;
            temp2[i] = 0;
            if(i < 105){
                tempStr1[i] = "";
                tempStr2[i] = "";
                tempStr3[i] = "";
                tempStr4[i] = "";
            }

            for(int j = 0; j < nodeSum; j++){
                ratioOfFullLoad[i][j] = new SingleRoad();
            }
        }
    }

    void floyd() {
        for (int i = 0; i < nodeSum; i++) {
            for (int k = 0; k < nodeSum; k++) {
                if (i != k && graph[i][k] == 0) {
                    minDistancesOfNodes[i][k] = INF;
                }
                else {
                    minDistancesOfNodes[i][k] = graph[i][k];
                }
            }
        }
        for (int k = 0; k < nodeSum; k++) {
            for (int i = 0; i < nodeSum; i++) {
                for (int j = 0; j < nodeSum; j++) {
                    if (minDistancesOfNodes[i][j] > (minDistancesOfNodes[i][k] + minDistancesOfNodes[k][j])) {
                        minDistancesOfNodes[i][j] = minDistancesOfNodes[i][k] + minDistancesOfNodes[k][j];
                    }
                }
            }
        }
        return;
    }

    /**
     * //gene满载率的评价
     * @param a
     * @return
     */
    double evaluateFullLoad(double a) {
        if (a < 0) {
            return 0;
        }
        else if (a <= 0.8) {
            return a * a * a;
        }
        else if (a <= 1) {
            return 1 - 12.2 * (a - 1) * (a - 1);
        }
        else if (a <= 1.1) {
            return 1 - 100 * (a - 1) * (a - 1);
        }
        else {
            return 0;
        }
    }

    double getAdaptability(int canshu, int min) {
        if (canshu > 1.4 * min) {
            return 0;
        }
        else {
            return 1 - 25.0 * ((double)canshu / min - 1) * ((double)canshu / min - 1) / 4;
        }
    }

    void createFathers(String[] shuzu, int carclass) {         //父代产生器 产生的染色体表现为：     gene:  A-3-4-1-A (A~Z表示种类，正整数表示配送点（中心为0）)
        int zhonglei = 0;                                      //                                    gene与gene之间用 “#” 隔开 染色体保存在数组中
        int[] shu = new int[nodeSum];
        int fuzhu3 = 0;
        int zhizhen = 0;
        String chuan = "";
        Random r = new Random(System.currentTimeMillis());
        for (int i = 0; i < POPULATION_SIZE; i++) {
            zhizhen = 0;
            for (int j = 0; j < nodeSum - 1; j++) {               //重新设置所有站点访问情况
                shu[j] = j + 1;
            }
            for (int j = 0; j < 2 * nodeSum - 2; j++) {           //洗牌
                fuzhu = r.nextInt(32767) % (nodeSum - 1);
                fuzhu2 = r.nextInt(32767) % (nodeSum - 1);
                fuzhu3 = shu[fuzhu];
                shu[fuzhu] = shu[fuzhu2];
                shu[fuzhu2] = fuzhu3;
            }
            for (int j = 0; j < nodeSum - 1; j++) {               //随机化产生路线
                fuzhu = r.nextInt(32767) % (nodeSum - 1);
                fuzhu++;
                zhonglei = r.nextInt(32767) % carclass;
                shuzu[i] += (char)(zhonglei + 'A');
                shuzu[i] += '-';
                if ((zhizhen + fuzhu) < nodeSum - 1) {
                    for (int k = zhizhen; k < zhizhen + fuzhu; k++) {

                        chuan = String.valueOf(shu[k]);
                        shuzu[i] += chuan;
                        shuzu[i] += '-';
                    }
                    shuzu[i] += (char)(zhonglei + 'A');
                    shuzu[i] += '#';
                    zhizhen += fuzhu;
                }
                else {
                    for (int k = zhizhen; k < nodeSum - 1; k++) {
                        chuan = String.valueOf(shu[k]);
                        shuzu[i] += chuan;
                        shuzu[i] += '-';
                    }
                    shuzu[i] += (char)(zhonglei + 'A');
                    break;
                }
            }
        }
    }



    void adaptabilityCalc(String[] shuzu) {
        //用于计算适应度 最终结果：manzailv[ZQSIZE][NODEN]  中保存着染色体每个gene的参数
        int totalFee = 0;

        //(满载率评价)（gene路线长度）（gene所用车辆）
        double totalDistance = 0;

        //canshu[3][ZQSIZE] 中保存着染色体参数
        double totalFullLoadRatio = 0;

        //canshu[0]中储存染色体总路程长度
        int star = 0;

        //canshu[1]中储存染色体总所需价格
        int end = 0;

        // canshu[0]中储存染色体总适应度
        for (int i = 0; i < POPULATION_SIZE; i++) {
            temp2[i] = 1;
        }
        for (int i = 0; i < POPULATION_SIZE; i++) {
            totalFee = 0;
            totalDistance = 0;
            int shu = MyUtil.split(shuzu[i], '#', tempStr1);
            for (int k = 0; k < shu; k++) {
                int nei = MyUtil.split(tempStr1[k], '-', tempStr2);
                totalFullLoadRatio = 0;

                //累计总价
                totalFee += pricesOfCar[tempStr2[0].charAt(0) - 'A'];

                //记录车辆
                ratioOfFullLoad[i][k].car = tempStr2[0].charAt(0) - 'A';
                for (int p = 1; p < nei - 1; p++) {

                    end = Integer.parseInt(tempStr2[p]);

                    //累计单路路程
                    ratioOfFullLoad[i][k].lc += minDistancesOfNodes[star][end];

                    //重置位置
                    star = end;

                    //累计满载
                    totalFullLoadRatio += demands[end];
                }
                end = 0;

                //累计单路路程
                ratioOfFullLoad[i][k].lc += minDistancesOfNodes[star][end];

                //路程超限
                if (ratioOfFullLoad[i][k].lc > maxDistancesOfCar[tempStr2[0].charAt(0) - 'A']) {
                    ratioOfFullLoad[i][k].lc = INF;
                    temp2[i] = 0;
                }
                else {
                    //累计路程
                    totalDistance += ratioOfFullLoad[i][k].lc;
                }
                ratioOfFullLoad[i][k].manzai = evaluateFullLoad(totalFullLoadRatio / maxLimitationsOfCar[tempStr2[0].charAt(0) - 'A']) * ratioOfEnergyAndFullLoad;   //计算单路满载率适应度
                if ((int) ratioOfFullLoad[i][k].manzai == 0) {
                    temp2[i] = 0;
                }
            }
            parameter[0][i] = totalDistance;
            parameter[1][i] = totalFee;
        }
        int jishu = 0;
        double he = 0;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            jishu = 0;
            he = 0;
            for (int j = 0; j < nodeSum - 1; j++) {
                if (ratioOfFullLoad[i][j].lc != 0) {

                    //不能超路程限制
                    if (ratioOfFullLoad[i][j].lc != INF) {
                        jishu++;
                        he += ratioOfFullLoad[i][j].manzai;
                    }
                }
                else {
                    if (jishu != 0) {

                        //gene满载率平均评价作为染色体总满载率评价
                        parameter[2][i] = he / jishu;
                        temp1[i] = he / jishu;
                    }
                    break;
                }
            }
        }
        for (int i = 0; i < POPULATION_SIZE; i++) {
            if (temp2[i] != 0) {
                if (parameter[0][i] < minLength) {
                    minLength = (int) parameter[0][i];
                }
                if (parameter[1][i] < minPrice) {
                    minPrice = (int) parameter[1][i];
                }
            }
        }
        for (int i = 0; i < POPULATION_SIZE; i++) {
            if (temp2[i] == 1) {
                parameter[2][i] += getAdaptability((int) parameter[0][i], minLength) * ratioOfEnergyAndDistance;
                parameter[2][i] += getAdaptability((int) parameter[1][i], minPrice) * ratioOfEnergyAndPrice;
            }
        }
        return;
    }

    String variation1(String a) {
        //对调变异（染色体中的节点相互调换）
        Random r = new Random(System.currentTimeMillis());
        fuzhubianyi = "";
        fuzhu = r.nextInt(32767) % (nodeSum - 1) + 1;
        fuzhu2 = r.nextInt(32767) % (nodeSum - 1) + 1;
        while (fuzhu2 == fuzhu) {
            fuzhu2 = r.nextInt(32767) % (nodeSum - 1) + 1;
        }
        String s1 = "", s2 = "";
        s1 = String.valueOf(fuzhu);

        s2 = String.valueOf(fuzhu2);
        int shu = MyUtil.split(a, '#', tempStr1);
        int nei = 0;
        for (int i = 0; i < shu; i++) {
            nei = MyUtil.split(tempStr1[i], '-', tempStr2);
            for (int k = 1; k < (nei - 1); k++) {
                if (tempStr2[k] == s1) {
                    tempStr2[k] = s2;
                    continue;
                }
                if (tempStr2[k] == s2) {
                    tempStr2[k] = s1;
                }
            }
            for (int k = 0; k < (nei - 1); k++) {
                fuzhubianyi += tempStr2[k] + '-';
            }
            if (i != (shu - 1)) {
                fuzhubianyi += tempStr2[nei - 1] + '#';
            }
            else {
                fuzhubianyi += tempStr2[nei - 1];
            }
        }
        return fuzhubianyi;
    }

    String variation2(String aa) {
        //插入变异（将染色体中某一个节点插入到另一个节点之前）
        char[] a = aa.toCharArray();
        Random r = new Random(System.currentTimeMillis());
        fuzhubianyi = "";
        fuzhu = r.nextInt(32767) % (nodeSum - 1) + 1;
        String s1 = "";


        s1 = String.valueOf(fuzhu);
        int dangqian = 0;
        fuzhu2 = r.nextInt(32767) % (nodeSum - 1) + 1;
        int yn = 0, jishu = 0;
        int gang = 0;
        for (int i = 0; i < aa.length(); i++) {
            if ('9' < a[i] || a[i] < '0') {
                if (fuzhubianyi.equals(s1)) {
                    dangqian = i;
                }
                fuzhubianyi = "";
                if (yn != 0) {
                    yn = 0;
                    jishu++;
                    if (jishu == fuzhu2) {
                        gang = i;
                    }
                }
            }
            else {
                yn = 1;
                fuzhubianyi += a[i];
            }
        }
        aa = new String(a);
        fuzhubianyi = "";
        int dangqian2 = 0;
        if (dangqian == gang) {
            return aa;
        }
        else {
            for (int i = gang - 1; i >= 0; i--) {
                if (aa.charAt(i) == '-') {
                    gang = i;
                    break;
                }
            }
            for (int i = dangqian - 1; i >= 0; i--) {
                if (aa.charAt(i) == '-') {
                    dangqian2 = i;
                    break;
                }
            }
            if (dangqian2 > gang) {
                int temp = Math.min(Math.max(dangqian2 - gang - 1, 0), gang + 1);

                fuzhubianyi = aa.substring(0, Math.max(gang + 1, 0)) + s1 + '-' + aa.substring(Math.min(Math.max(dangqian2 - gang - 1, 0), gang + 1), Math.max(dangqian2 - gang - 1, 0)) + aa.substring(Math.min(Math.max(aa.length() - dangqian, 0), dangqian), Math.max(aa.length() - dangqian, 0));
            }
            else {
                fuzhubianyi = aa.substring(0, Math.max(dangqian2, 0)) + aa.substring(Math.min(Math.max(gang + 1 - dangqian, 0), dangqian), Math.max(gang + 1 - dangqian, 0)) + s1 + '-' + aa.substring(Math.min(gang + 1, Math.max(aa.length() - gang - 1, 0)), Math.max(aa.length() - gang - 1, 0));
            }
        }
        return fuzhubianyi;
    }

    String variation3(String aa) {                                    //车辆类型变异（gene所用车辆更换）
        Random r = new Random(System.currentTimeMillis());
        char[] a = aa.toCharArray();
        fuzhubianyi = "";
        int shu = MyUtil.split(aa, '#', tempStr1);
        fuzhu = r.nextInt(32767) % shu + 1;
        fuzhu2 = r.nextInt(32767) % sumOfCarKind;
        int jishu = 0;
        for (int i = 0; i < aa.length(); i++) {
            if (a[i] <= 'Z' && a[i] >= 'A') {
                jishu++;
                if (jishu == (2 * fuzhu - 1)) {
                    a[i] = (char)(fuzhu2 + 'A');
                }
                if (jishu == (2 * fuzhu)) {
                    a[i] = (char)(fuzhu2 + 'A');
                    break;
                }
            }
        }
        return aa;
    }

    void jiaocha(int a, int b, String[] shuzu) {
        //模拟交叉感染（染色体中gene评价最高的作为一个基因，另一条染色体中删除此段gene中的节点然后加入到此gene末尾）
        Random r = new Random(System.currentTimeMillis());

        fuzhu = MyUtil.split(shuzu[a], '#', tempStr1);
        fuzhu2 = MyUtil.split(shuzu[b], '#', tempStr2);
        fuzhubianyi = "";

        int shu = r.nextInt(32767) % fuzhu;
        fuzhubianyi += tempStr1[shu];
        int jiaochagene = MyUtil.split(tempStr1[shu], '-', tempStr3);
        String pianduan = "";
        int genelength = 0;
        for (int i = 0; i < fuzhu2; i++) {
            pianduan = "";
            genelength = MyUtil.split(tempStr2[i], '-', tempStr4);
            pianduan += tempStr4[0] + '-';
            for (int k = 1; k < genelength - 1; k++) {
                for (int p = 1; p < jiaochagene - 1; p++) {
                    if (tempStr4[k] == tempStr3[p]) {
                        tempStr4[k] = "";
                        break;
                    }
                }
                if (tempStr4[k] != "") {
                    pianduan += tempStr4[k] + '-';
                }
            }
            pianduan += tempStr4[genelength - 1];
            if (pianduan.length() > 3) {
                fuzhubianyi += '#' + pianduan;
            }
        }

        String fuzhubianyi2 = "";
        shu = r.nextInt(32767) % fuzhu2;
        fuzhubianyi2 += tempStr2[shu];
        jiaochagene = MyUtil.split(tempStr2[shu], '-', tempStr3);
        for (int i = 0; i < fuzhu; i++) {
            pianduan = "";
            genelength = MyUtil.split(tempStr1[i], '-', tempStr4);
            pianduan += tempStr4[0] + '-';
            for (int k = 1; k < genelength - 1; k++) {
                for (int p = 1; p < jiaochagene - 1; p++) {
                    if (tempStr4[k] == tempStr3[p]) {
                        tempStr4[k] = "";
                        break;
                    }
                }
                if (!tempStr4[k].equals("")) {
                    pianduan += tempStr4[k] + '-';
                }
            }
            pianduan += tempStr4[genelength - 1];
            if (pianduan.length() > 3) {
                fuzhubianyi2 += '#' + pianduan;
            }
        }

        shuzu[a] = fuzhubianyi;
        shuzu[b] = fuzhubianyi2;
    }

    int bigRand() {                                                                  //产生大随机数
        Random r = new Random();
        return (r.nextInt(32767) % 1000) * 1000000 + (r.nextInt(32767) % 1000) * 1000 + (r.nextInt(32767) % 1000);
    }

    /**
     * 由父代产生子代的过程
     * @param shuzu
     * @param next
     */

    /**
     * 子代染色体构造方法：
     * 1.按照适应度轮盘选择
     * 2.所有满载率评价大于 LEASTREQUEST 的gene直接遗传
     * 3.如果2中没有符合要求的染色体那么将满载率评价最高的gene加入子代染色体
     * 4.随机化生成其余所有节点的路线
     */
    void birthASon(String[] shuzu, String[] next) {


        for (int i = 0; i < POPULATION_SIZE; i++) {
            next[i] = "";
        }
        for (int i = 1; i < POPULATION_SIZE; i++) {
            parameter[2][i] += parameter[2][i - 1];
        }
        Random r = new Random(System.currentTimeMillis());

        //轮盘指针
        double cornnaPointer = 0;
        int max = 0;
        int yn = 0;
        int ranseti = 0;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            ranseti = 0;

            //轮盘赌注
            cornnaPointer = bigRand() % (int)(parameter[2][POPULATION_SIZE - 1] * 10000);
            cornnaPointer /= 10000;
            for (int k = 0; k < POPULATION_SIZE; k++) {
                if (parameter[2][k] >= cornnaPointer) {
                    ranseti = k;
                    break;
                }
            }
            max = 0;
            yn = 0;
            fuzhu = MyUtil.split(shuzu[ranseti], '#', tempStr1);
            for (int k = 1; k < nodeSum; k++) {
                temp1[k] = 0;
            }
            int  fuzhu3 = 0;
            for (int k = 0; k < fuzhu; k++) {
                //所有满载率评价大于 LESSREQUEST 的gene直接遗传
                if (ratioOfFullLoad[ranseti][k].manzai > LEAST_REQUEST && ratioOfFullLoad[ranseti][k].lc != INF) {
                    next[i] += tempStr1[k] + '#';
                    fuzhu2 = MyUtil.split(tempStr1[k], '-', tempStr2);
                    for (int p = 1; p < fuzhu2 - 1; p++) {
                        fuzhu3 = Integer.parseInt(tempStr2[p]);
                        temp1[fuzhu3] = 1;
                    }
                    yn = 1;
                }
                if (ratioOfFullLoad[ranseti][k].manzai > ratioOfFullLoad[ranseti][max].manzai && ratioOfFullLoad[ranseti][k].lc != INF) {
                    max = k;
                }
            }
            //如果2中没有符合要求的染色体那么将满载率评价最高的gene加入子代染色体
            if (yn == 0) {
                next[i] += tempStr1[max] + '#';
                fuzhu2 = MyUtil.split(tempStr1[max], '-', tempStr2);
                for (int p = 1; p < fuzhu2 - 1; p++) {
                    fuzhu3 = Integer.parseInt(tempStr2[p]);
                    temp1[fuzhu3] = 1;
                }
            }

            //随机化生成其余所有节点的路线
            int pointer = 0;
            int fuzhu4 = 0;
            for (int k = 1; k < nodeSum; k++) {
                if (temp1[k] == 0) {
                    tempStr3[pointer] = String.valueOf(k);
                    pointer++;
                }
            }
            for (int k = 0; k < pointer; k++) {
                fuzhu3 = r.nextInt(32767) % pointer;
                fuzhu4 = r.nextInt(32767) % pointer;
                String zhongjian = tempStr3[fuzhu4];
                tempStr3[fuzhu4] = tempStr3[fuzhu3];
                tempStr3[fuzhu3] = zhongjian;
            }
            int yifangwen = 0;
            for (int k = 0; k < nodeSum; k++) {
                if (pointer >= 2) {
                    fuzhu3 = r.nextInt(32767) % (pointer - 1) + 1;
                }
                else {
                    fuzhu3 = pointer;
                }
                fuzhu4 = r.nextInt(32767) % sumOfCarKind;
                next[i] += (char)(fuzhu4 + 'A');
                next[i] += '-';
                if (yifangwen + fuzhu3 < pointer) {
                    for (int p = yifangwen; p < yifangwen + fuzhu3; p++) {
                        next[i] += tempStr3[p] + "-";
                    }
                    yifangwen += fuzhu3;
                    next[i] += (char)(fuzhu4 + 'A');
                    next[i] += '#';
                }
                else {
                    for (int p = yifangwen; p < pointer; p++) {
                        next[i] += tempStr3[p] + "-";
                    }
                    next[i] += (char)(fuzhu4 + 'A');
                    break;
                }
            }
        }
    }

    /**
     * 结束时在所有染色体中找到最优解
     * @return
     */
    int bestSolutionCalc() {
        double max = 0;
        int yn = 0, zhizhen = 0;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            for (int k = 0; k < nodeSum; k++) {
                if ((ratioOfFullLoad[i][k].manzai != 0 && ratioOfFullLoad[i][k].lc == INF) || (!(ratioOfFullLoad[i][k].manzai != 0) && ratioOfFullLoad[i][k].lc != 0)) { //gene载货评价不为0，gene路程不超限
                    yn = 1;
                    break;
                }
            }
            if (yn == 0 && parameter[2][i] > max) {
                max = parameter[2][i];
                zhizhen = i;
            }
            yn = 0;
        }
        return zhizhen;
    }

    /**
     * 清空满载率数组和参数数组
     */
    void clear() {
        for (int i = 0; i < ratioOfFullLoad.length; i++) {
            for (int j = 0; j < ratioOfFullLoad[0].length; j++) {
                if(ratioOfFullLoad[i][j] == null){
                    continue;
                }
                ratioOfFullLoad[i][j].car = 0;
                ratioOfFullLoad[i][j].lc = 0;
                ratioOfFullLoad[i][j].manzai = 0;
            }
            parameter[0][i] = 0;
            parameter[1][i] = 0;
            parameter[2][i] = 0;
        }
    }

    /**
     * 按照 PCHANGE 对当前种群进行四种遗传变异
     * @param shuzu
     */
    void geneticVariation(String[] shuzu) {
        for (int i = 0; i < POPULATION_SIZE; i++) {
            fuzhu = bigRand() % PCHANGE;
            if (fuzhu == 1) {
                int a = 0;
                int b = 0;
                a = bigRand() % POPULATION_SIZE;
                b = bigRand() % POPULATION_SIZE;
                jiaocha(a, b, shuzu);
            }
        }
        for (int i = 0; i < POPULATION_SIZE; i++) {
            fuzhu = bigRand() % PCHANGE;
            if (fuzhu == 1) {
                variation1(shuzu[i]);
                variationTimes++;
            }
            fuzhu = bigRand() % PCHANGE;
            if (fuzhu == 1) {
                variation2(shuzu[i]);
                variationTimes++;
            }
            fuzhu = bigRand() % 2 * PCHANGE;
            if (fuzhu == 1) {
                variation3(shuzu[i]);
                variationTimes++;
            }
        }
    }



}

