package com.fuchuang.fuchuang.cpp;

import com.fuchuang.fuchuang.pojo.Result;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author orangeboy
 * @version 1.0
 * @date 2020/5/19 10:28
 */

@Component
public class CppImpl2 implements Cpp {

    /**
     * 所有节点数
     */
    private int NODE_SUM = 11;

    /**
     * 种群大小
     */
    private final int POPULATION_SIZE = 50;

    /**
     * 满载率效能占比
     */
    private int RATIO_OF_FULL_LOAD_AND_PERFORMANCE = 40;

    /**
     * 总路程效能占比
     */
    private int RATIO_OF_TOTAL_DISTANCE_AND_PERFORMANCE = 30;

    /**
     * 总价格效能占比
     */
    private int RATIO_OF_TOTAL_PRICE_AND_PERFORMANCE = 30;

    /**
     * 作辅助最大
     */
    private final int INF = 100000;

    /**
     * 总遗传代数
     */
    private final int MAX_GENERATION = (NODE_SUM - 1) * 10;

    /**
     * 单条染色体 单种变异方式 概率倒数
     */
    private int PCHANGE = 1000;
    private int LEAST_REQUEST = 32   ;
    //满载率最小要求（实际上达不到并且不能太高）最大值为 MZ


    /**
     * gene路线参数
     */
    class SingleRoad {
        double fullLoadRatio = 0;
        double lc = 0;
        int car = 0;
    }

    /**
     * 临时数组
     */
    double[] tempArray1 = new double[POPULATION_SIZE];

    /**
     * 临时数组
     */
    double[] tempArray2 = new double[POPULATION_SIZE];

    /**
     * 临接表
     */
    double[][] graph;

    /**
     * 顶点间的最短距离
     */
    double[][] minDistancesBetweenNodes;

    /**
     * 各点需求量
     */
    double[] demands;

    /**
     * 临时字符串
     */
    String[] tempStr1 = new String[50];
    String[] tempStr2 = new String[50];
    String[] tempStr3 = new String[50];
    String[] tempStr4 = new String[50];

    /**
     * 用作辅助计算
     */
    int temp = 0;
    int temp2 = 0;

    /**
     * 辅助gene变异
     */
    String variationTemp1 = "";

    /**
     * 车辆种类
     */
    int truckTypeSum = 0;

    /**
     * 车辆最大限制
     */
    double[] truckFullLoads = new double[10];

    /**
     * 车辆最大里程
     */
    double[] truckMaxDistances = new double[10];

    /**
     * 车辆费用
     */
    double[] truckPrice = new double[10];

    /**
     * 最小价格
     */
    double minPrice = INF;

    /**
     * 最小路程
     */
    double minDistance = INF;

    /**
     * 满载率
     */
    SingleRoad[][] fullLoadRatios;

    /**
     * 参数和适应度（parameter[0]表示路程，parameter[1]表示总价格）
     */
    double[][] parameter = new double[3][POPULATION_SIZE];


    /**
     * 用于存储染色体
     */
    String[] father = new String[POPULATION_SIZE];

    /**
     * 用于存储染色体
     */
    String[] son = new String[POPULATION_SIZE];

    int variationTime = 0;

    void init(double[] carMaxLoad, double[] carMaxDis, double[] carCost, int carCnt) {       //初始化参数




        for (int i = 0; i < POPULATION_SIZE; i++){
            father[i] = "";
            son[i] = "";
            parameter[0][i] = 0;
            parameter[1][i] = 0;
            parameter[2][i] = 0;
            tempArray1[i] = 0;
            tempArray2[i] = 0;
            if(i < 50){
                tempStr1[i] = "";
                tempStr2[i] = "";
                tempStr3[i] = "";
                tempStr4[i] = "";
            }

            for(int j = 0; j < NODE_SUM; j++){
                fullLoadRatios[i][j] = new SingleRoad();
            }
        }

        this.truckTypeSum = carCnt;

        if (this.truckTypeSum >= 0) System.arraycopy(carMaxLoad, 0, this.truckFullLoads, 0, this.truckTypeSum);
        if (this.truckTypeSum >= 0) System.arraycopy(carMaxDis, 0, this.truckMaxDistances, 0, this.truckTypeSum);
        if (this.truckTypeSum >= 0) System.arraycopy(carCost, 0, this.truckPrice, 0, this.truckTypeSum);
    }

    /**
     * 求最短相邻路的算法
     */
    void floyd() {
        for (int i = 0; i < NODE_SUM; i++) {
            for (int k = 0; k < NODE_SUM; k++) {
                if (i != k && graph[i][k] == 0) {
                    minDistancesBetweenNodes[i][k] = INF;
                }
                else {
                    minDistancesBetweenNodes[i][k] = graph[i][k];
                }
            }
        }
        for (int k = 0; k < NODE_SUM; k++) {
            for (int i = 0; i < NODE_SUM; i++) {
                for (int j = 0; j < NODE_SUM; j++) {
                    if (minDistancesBetweenNodes[i][j] > (minDistancesBetweenNodes[i][k] + minDistancesBetweenNodes[k][j])) {
                        minDistancesBetweenNodes[i][j] = minDistancesBetweenNodes[i][k] + minDistancesBetweenNodes[k][j];
                    }
                }
            }
        }
    }

    /**
     * gene满载率的评价
     * @param fullLoadParameter 满载率参数
     * @return 评价率
     */
    double evaluateFullLoad(double fullLoadParameter) {
        if (fullLoadParameter < 0) {
            return 0;
        }
        else if (fullLoadParameter <= 0.8) {
            return fullLoadParameter * fullLoadParameter * fullLoadParameter;
        }
        else if (fullLoadParameter <= 1) {
            return 1 - 12.2 * (fullLoadParameter - 1) * (fullLoadParameter - 1);
        }
        else if (fullLoadParameter <= 1.1) {
            return 1 - 100 * (fullLoadParameter - 1) * (fullLoadParameter - 1);
        }
        else {
            return 0;
        }
    }

    double evaluateAdaptability(double parameter, double min) {
        if (parameter > 1.4 * min) {
            return 0;
        }
        else {
            return 1 - 25.0 * (parameter / min - 1) * (parameter / min - 1) / 4;
        }
    }

    /**
     * 父代生成器
     * 产生的染色体表现为：gene:A-3-4-1-A (A~Z表示种类，正整数表示配送点（中心为0)
     * gene与gene之间用 “#” 隔开 染色体保存在数组中
     * @param array 需要传入的数组参数
     * @param truckTypeSum 车辆类型总数
     */
    void generateFather(String[] array, int truckTypeSum) {
        int type = 0;
        int[] a = new int[NODE_SUM];
        int temp3 = 0;
        int pointer = 0;
        String str = "";
        Random r = new Random(System.currentTimeMillis());
        for (int i = 0; i < POPULATION_SIZE; i++) {
            pointer = 0;

            //重新设置所有站点访问情况
            for (int j = 0; j < NODE_SUM - 1; j++) {
                a[j] = j + 1;
            }

            //洗牌
            for (int j = 0; j < 2 * NODE_SUM - 2; j++) {
                temp = r.nextInt(32767) % (NODE_SUM - 1);
                temp2 = r.nextInt(32767) % (NODE_SUM - 1);
                temp3 = a[temp];
                a[temp] = a[temp2];
                a[temp2] = temp3;
            }

            //随机化产生路线
            for (int j = 0; j < NODE_SUM - 1; j++) {
                temp = r.nextInt(32767) % (NODE_SUM - 1);
                temp++;
                type = r.nextInt(32767) % truckTypeSum;
                array[i] += (char)(type + 'A');
                array[i] += '-';
                if ((pointer + temp) < NODE_SUM - 1) {
                    for (int k = pointer; k < pointer + temp; k++) {

                        str = String.valueOf(a[k]);
                        array[i] += str;
                        array[i] += '-';
                    }
                    array[i] += (char)(type + 'A');
                    array[i] += '#';
                    pointer += temp;
                }
                else {
                    for (int k = pointer; k < NODE_SUM - 1; k++) {
                        str = String.valueOf(a[k]);
                        array[i] += str;
                        array[i] += '-';
                    }
                    array[i] += (char)(type + 'A');
                    break;
                }
            }
        }
    }

    /**
     * 字符串分割  把str 按 c 分割，保存在 s[50] 中，
     * 返回分割段数，常常用strs[50] strs2[50] strs3[50] strs4[50] 作保存数组
     * @param str 需要分割的字符串
     * @param c 按c分割
     * @param s 保存的数组
     * @return 分割段数
     */
    int split(String str, char c, String[] s) {
        int pointer = 0;
        char[] chars = str.toCharArray();
        StringBuilder tempStr = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == c) {
                s[pointer] = tempStr.toString();
                tempStr = new StringBuilder();
                pointer++;
            }
            else if (i == (chars.length - 1)) {
                tempStr.append(chars[i]);
                s[pointer] = tempStr.toString();
                tempStr = new StringBuilder();
                pointer++;
            }
            else {
                tempStr.append(chars[i]);
            }
        }
        return pointer;
    }

    /**
     *用于计算适应度 最终结果：fullLoadRatios[ZQSIZE][NODEN]  中保存着染色体每个gene的参数
     * (满载率评价)（gene路线长度）（gene所用车辆）
     * canshu[3][ZQSIZE] 中保存着染色体参数
     * canshu[0]中储存染色体总路程长度
     * canshu[1]中储存染色体总所需价格
     * canshu[0]中储存染色体总适应度
     * @param array 数组参数
     */
    void adaptabilityCalc(String[] array) {
        int sumCost = 0;
        double sumDis = 0;
        double fullLoad = 0;
        int startFrom = 0;
        int endFrom = 0;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            tempArray2[i] = 1;
        }
        for (int i = 0; i < POPULATION_SIZE; i++) {
            sumCost = 0;
            sumDis = 0;
            int shu = split(array[i], '#', tempStr1);
            for (int k = 0; k < shu; k++) {
                int nei = split(tempStr1[k], '-', tempStr2);
                fullLoad = 0;

                //累计总价
                sumCost += truckPrice[tempStr2[0].charAt(0) - 'A'];

                //记录车辆
                fullLoadRatios[i][k].car = tempStr2[0].charAt(0) - 'A';
                for (int p = 1; p < nei - 1; p++) {

                    endFrom = Integer.parseInt(tempStr2[p]);

                    //累计单路路程
                    fullLoadRatios[i][k].lc += minDistancesBetweenNodes[startFrom][endFrom];

                    //重置位置
                    startFrom = endFrom;

                    //累计满载
                    fullLoad += demands[endFrom];
                }
                endFrom = 0;

                //累计单路路程
                fullLoadRatios[i][k].lc += minDistancesBetweenNodes[startFrom][endFrom];

                //路程超限
                if (fullLoadRatios[i][k].lc > truckMaxDistances[tempStr2[0].charAt(0) - 'A']) {
                    fullLoadRatios[i][k].lc = INF;
                    tempArray2[i] = 0;
                }
                else {
                    //累计路程
                    sumDis += fullLoadRatios[i][k].lc;
                }
                fullLoadRatios[i][k].fullLoadRatio = evaluateFullLoad(fullLoad / truckFullLoads[tempStr2[0].charAt(0) - 'A']) * RATIO_OF_FULL_LOAD_AND_PERFORMANCE;   //计算单路满载率适应度
                if ((int) fullLoadRatios[i][k].fullLoadRatio == 0) {
                    tempArray2[i] = 0;
                }
            }
            parameter[0][i] = sumDis;
            parameter[1][i] = sumCost;
        }
        int count = 0;
        double sum = 0;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            count = 0;
            sum = 0;
            for (int j = 0; j < NODE_SUM - 1; j++) {
                if (fullLoadRatios[i][j].lc != 0) {
                    //不能超路程限制
                    if (fullLoadRatios[i][j].lc != INF) {
                        count++;
                        sum += fullLoadRatios[i][j].fullLoadRatio;
                    }
                }
                else {
                    if (count != 0) {
                        //gene满载率平均评价作为染色体总满载率评价
                        parameter[2][i] = sum / count;
                        tempArray1[i] = sum / count;
                    }
                    break;
                }
            }
        }
        for (int i = 0; i < POPULATION_SIZE; i++) {
            if (tempArray2[i] != 0) {
                if (parameter[0][i] < minDistance) {
                    minDistance = parameter[0][i];
                }
                if (parameter[1][i] < minPrice) {
                    minPrice = parameter[1][i];
                }
            }
        }
        for (int i = 0; i < POPULATION_SIZE; i++) {
            if (tempArray2[i] == 1) {
                parameter[2][i] += evaluateAdaptability(parameter[0][i], minDistance) * RATIO_OF_TOTAL_DISTANCE_AND_PERFORMANCE;
                parameter[2][i] += evaluateAdaptability(parameter[1][i], minPrice) * RATIO_OF_TOTAL_PRICE_AND_PERFORMANCE;
            }
        }
    }

    /**
     * 对调变异（染色体中的节点相互调换)
     * @param chromosomeStr 染色体
     */
    void variation1(String chromosomeStr) {
        Random r = new Random(System.currentTimeMillis());
        variationTemp1 = "";
        temp = r.nextInt(32767) % (NODE_SUM - 1) + 1;
        temp2 = r.nextInt(32767) % (NODE_SUM - 1) + 1;
        while (temp2 == temp) {
            temp2 = r.nextInt(32767) % (NODE_SUM - 1) + 1;
        }

        String str1 = "", str2 = "";
        str1 = String.valueOf(temp);
        str2 = String.valueOf(temp2);
        int a = split(chromosomeStr, '#', tempStr1);
        int b = 0;
        for (int i = 0; i < a; i++) {
            b = split(tempStr1[i], '-', tempStr2);
            for (int k = 1; k < (b - 1); k++) {
                if (tempStr2[k].equals(str1)) {
                    tempStr2[k] = str2;
                    continue;
                }
                if (tempStr2[k].equals(str2)) {
                    tempStr2[k] = str1;
                }
            }
            for (int k = 0; k < (b - 1); k++) {
                variationTemp1 += tempStr2[k] + '-';
            }
            if (i != (a - 1)) {
                variationTemp1 += tempStr2[b - 1] + '#';
            }
            else {
                variationTemp1 += tempStr2[b - 1];
            }
        }
    }

    /**
     * 插入变异（将染色体中某一个节点插入到另一个节点之前）
     * @param chromosomeStr 染色体
     */
    void variation2(String chromosomeStr) {
        char[] a = chromosomeStr.toCharArray();
        Random r = new Random(System.currentTimeMillis());
        variationTemp1 = "";
        temp = r.nextInt(32767) % (NODE_SUM - 1) + 1;
        String s1 = "";


        s1 = String.valueOf(temp);
        int currentIndex = 0;
        temp2 = r.nextInt(32767) % (NODE_SUM - 1) + 1;
        int yn = 0, count = 0;
        int gang = 0;
        for (int i = 0; i < chromosomeStr.length(); i++) {
            if ('9' < a[i] || a[i] < '0') {
                if (variationTemp1.equals(s1)) {
                    currentIndex = i;
                }
                variationTemp1 = "";
                if (yn != 0) {
                    yn = 0;
                    count++;
                    if (count == temp2) {
                        gang = i;
                    }
                }
            }
            else {
                yn = 1;
                variationTemp1 += a[i];
            }
        }
        chromosomeStr = new String(a);
        variationTemp1 = "";
        int currentIndex2 = 0;
        if (currentIndex == gang) {
        }
        else {
            for (int i = gang - 1; i >= 0; i--) {
                if (chromosomeStr.charAt(i) == '-') {
                    gang = i;
                    break;
                }
            }
            for (int i = currentIndex - 1; i >= 0; i--) {
                if (chromosomeStr.charAt(i) == '-') {
                    currentIndex2 = i;
                    break;
                }
            }
            if (currentIndex2 > gang) {
                int temp = Math.min(Math.max(currentIndex2 - gang - 1, 0), gang + 1);

                variationTemp1 = chromosomeStr.substring(0, Math.max(gang + 1, 0)) + s1 + '-' + chromosomeStr.substring(temp, Math.max(currentIndex2 - gang - 1, 0)) + chromosomeStr.substring(Math.min(Math.max(chromosomeStr.length() - currentIndex, 0), currentIndex), Math.max(chromosomeStr.length() - currentIndex, 0));
            }
            else {
                variationTemp1 = chromosomeStr.substring(0, Math.max(currentIndex2, 0)) + chromosomeStr.substring(Math.min(Math.max(gang + 1 - currentIndex, 0), currentIndex), Math.max(gang + 1 - currentIndex, 0)) + s1 + '-' + chromosomeStr.substring(Math.min(gang + 1, Math.max(chromosomeStr.length() - gang - 1, 0)), Math.max(chromosomeStr.length() - gang - 1, 0));
            }
        }
    }

    /**
     * 车辆类型变异（gene所用车辆更换）
     * @param chromosome 染色体
     */
    void variation3(String chromosome) {
        Random r = new Random(System.currentTimeMillis());
        char[] a = chromosome.toCharArray();
        variationTemp1 = "";
        int shu = split(chromosome, '#', tempStr1);
        temp = r.nextInt(32767) % shu + 1;
        temp2 = r.nextInt(32767) % truckTypeSum;
        int count = 0;
        for (int i = 0; i < chromosome.length(); i++) {
            if (a[i] <= 'Z' && a[i] >= 'A') {
                count++;
                if (count == (2 * temp - 1)) {
                    a[i] = (char)(temp2 + 'A');
                }
                if (count == (2 * temp)) {
                    a[i] = (char)(temp2 + 'A');
                    break;
                }
            }
        }
    }

    /**
     * 模拟交叉感染（染色体中gene评价最高的作为一个基因，另一条染色体中删除此段gene中的节点然后加入到此gene末尾）
     * @param a 基因1
     * @param b 基因2
     * @param array 传入的参数
     */
    void crossMutation(int a, int b, String[] array) {
        Random r = new Random(System.currentTimeMillis());

        temp = split(array[a], '#', tempStr1);
        temp2 = split(array[b], '#', tempStr2);
        variationTemp1 = "";

        int shu = r.nextInt(32767) % temp;
        variationTemp1 += tempStr1[shu];
        int jiaochagene = split(tempStr1[shu], '-', tempStr3);
        StringBuilder pianduan = new StringBuilder();
        int genelength = 0;
        for (int i = 0; i < temp2; i++) {
            pianduan = new StringBuilder();
            genelength = split(tempStr2[i], '-', tempStr4);
            pianduan.append(tempStr4[0]).append('-');
            for (int k = 1; k < genelength - 1; k++) {
                for (int p = 1; p < jiaochagene - 1; p++) {
                    if (tempStr4[k].equals(tempStr3[p])) {
                        tempStr4[k] = "";
                        break;
                    }
                }
                if (!tempStr4[k].equals("")) {
                    pianduan.append(tempStr4[k]).append('-');
                }
            }
            pianduan.append(tempStr4[genelength - 1]);
            if (pianduan.length() > 3) {
                variationTemp1 += '#' + pianduan.toString();
            }
        }

        StringBuilder variationTemp2 = new StringBuilder();
        shu = r.nextInt(32767) % temp2;
        variationTemp2.append(tempStr2[shu]);
        jiaochagene = split(tempStr2[shu], '-', tempStr3);
        for (int i = 0; i < temp; i++) {
            pianduan = new StringBuilder();
            genelength = split(tempStr1[i], '-', tempStr4);
            pianduan.append(tempStr4[0]).append('-');
            for (int k = 1; k < genelength - 1; k++) {
                for (int p = 1; p < jiaochagene - 1; p++) {
                    if (tempStr4[k].equals(tempStr3[p])) {
                        tempStr4[k] = "";
                        break;
                    }
                }
                if (!tempStr4[k].equals("")) {
                    pianduan.append(tempStr4[k]).append('-');
                }
            }
            pianduan.append(tempStr4[genelength - 1]);
            if (pianduan.length() > 3) {
                variationTemp2.append('#').append(pianduan);
            }
        }

        array[a] = variationTemp1;
        array[b] = variationTemp2.toString();
    }

    /**
     * 产生大的随机数
     * @return
     */
    int getBigRandom() {
        Random r = new Random();
        return (r.nextInt(32767) % 1000) * 1000000 + (r.nextInt(32767) % 1000) * 1000 + (r.nextInt(32767) % 1000);
    }

    /**
     * 由父代产生子代的过程
     *  子代染色体构造方法：
     *  1.按照适应度轮盘选择
     *  2.所有满载率评价大于 LEASTREQUEST 的gene直接遗传
     *  3.如果2中没有符合要求的染色体那么将满载率评价最高的gene加入子代染色体
     *  4.随机化生成其余所有节点的路线
     * @param array 数组参数
     * @param next 下一个数组
     */
    void generateSons(String[] array, String[] next) {
        for (int i = 0; i < POPULATION_SIZE; i++) {
            next[i] = "";
        }
        for (int i = 1; i < POPULATION_SIZE; i++) {
            parameter[2][i] += parameter[2][i - 1];
        }
        Random r = new Random(System.currentTimeMillis());

        //轮盘指针
        double roulettePointer = 0;
        int max = 0;
        int yn = 0;
        int chromosome = 0;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            chromosome = 0;

            //轮盘赌注
            roulettePointer = getBigRandom() % (parameter[2][POPULATION_SIZE - 1] * 10000);
            roulettePointer /= 10000;
            for (int k = 0; k < POPULATION_SIZE; k++) {
                if (parameter[2][k] >= roulettePointer) {
                    chromosome = k;
                    break;
                }
            }
            max = 0;
            yn = 0;
            temp = split(array[chromosome], '#', tempStr1);
            for (int k = 1; k < NODE_SUM; k++) {
                tempArray1[k] = 0;
            }
            int  temp3 = 0;
            for (int k = 0; k < temp; k++) {
                //所有满载率评价大于 LESSREQUEST 的gene直接遗传
                if (fullLoadRatios[chromosome][k].fullLoadRatio > LEAST_REQUEST && fullLoadRatios[chromosome][k].lc != INF) {
                    next[i] += tempStr1[k] + '#';
                    temp2 = split(tempStr1[k], '-', tempStr2);
                    for (int p = 1; p < temp2 - 1; p++) {
                        temp3 = Integer.parseInt(tempStr2[p]);
                        tempArray1[temp3] = 1;
                    }
                    yn = 1;
                }
                if (fullLoadRatios[chromosome][k].fullLoadRatio > fullLoadRatios[chromosome][max].fullLoadRatio && fullLoadRatios[chromosome][k].lc != INF) {
                    max = k;
                }
            }

            //如果2中没有符合要求的染色体那么将满载率评价最高的gene加入子代染色体
            if (yn == 0) {
                next[i] += tempStr1[max] + '#';
                temp2 = split(tempStr1[max], '-', tempStr2);
                for (int p = 1; p < temp2 - 1; p++) {
                    temp3 = Integer.parseInt(tempStr2[p]);
                    tempArray1[temp3] = 1;
                }
            }
            //随机化生成其余所有节点的路线
            int pointer = 0;
            int temp4 = 0;
            for (int k = 1; k < NODE_SUM; k++) {
                if (tempArray1[k] == 0) {
                    tempStr3[pointer] = String.valueOf(k);
                    pointer++;
                }
            }
            for (int k = 0; k < pointer; k++) {
                temp3 = r.nextInt(32767) % pointer;
                temp4 = r.nextInt(32767) % pointer;
                String zhongjian = tempStr3[temp4];
                tempStr3[temp4] = tempStr3[temp3];
                tempStr3[temp3] = zhongjian;
            }
            int yifangwen = 0;
            for (int k = 0; k < NODE_SUM; k++) {
                if (pointer >= 2) {
                    temp3 = r.nextInt(32767) % (pointer - 1) + 1;
                }
                else {
                    temp3 = pointer;
                }
                temp4 = r.nextInt(32767) % truckTypeSum;
                next[i] += (char)(temp4 + 'A');
                next[i] += '-';
                if (yifangwen + temp3 < pointer) {
                    for (int p = yifangwen; p < yifangwen + temp3; p++) {
                        next[i] += tempStr3[p] + "-";
                    }
                    yifangwen += temp3;
                    next[i] += (char)(temp4 + 'A');
                    next[i] += '#';
                }
                else {
                    for (int p = yifangwen; p < pointer; p++) {
                        next[i] += tempStr3[p] + "-";
                    }
                    next[i] += (char)(temp4 + 'A');
                    break;
                }
            }
        }
    }

    /**
     * 结束时在所有染色体中找到最优解
     * @return 指向
     */
    int findTheBest() {
        double max = 0;
        int yn = 0, pointer = -1;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            for (int k = 0; k < NODE_SUM; k++) {
                if ((fullLoadRatios[i][k].fullLoadRatio != 0 && fullLoadRatios[i][k].lc == INF) || (!(fullLoadRatios[i][k].fullLoadRatio != 0) && fullLoadRatios[i][k].lc != 0)) { //gene载货评价不为0，gene路程不超限
                    yn = 1;
                    break;
                }
            }
            if (yn == 0 && parameter[2][i] > max) {
                max = parameter[2][i];
                pointer = i;
            }
            yn = 0;
        }
        return pointer;
    }


    /**
     * 清空fullLoadRatios[][] 和 canshu[][]
     */
    void clear() {
        for (int i = 0; i < fullLoadRatios.length; i++) {
            for (int j = 0; j < fullLoadRatios[0].length; j++) {
                if(fullLoadRatios[i][j] != null){
                    fullLoadRatios[i][j].car = 0;
                    fullLoadRatios[i][j].lc = 0;
                    fullLoadRatios[i][j].fullLoadRatio = 0;
                }
            }
            parameter[0][i] = 0;
            parameter[1][i] = 0;
            parameter[2][i] = 0;
        }
    }

    /**
     * 按照 PCHANGE 对当前种群进行四种遗传变异
     * @param array 需要传入的数组参数
     */
    void geneticsAndVariation(String[] array) {
        for (int i = 0; i < POPULATION_SIZE; i++) {
            temp = getBigRandom() % PCHANGE;
            if (temp == 1) {
                int a = 0;
                int b = 0;
                a = getBigRandom() % POPULATION_SIZE;
                b = getBigRandom() % POPULATION_SIZE;
                crossMutation(a, b, array);
            }
        }
        for (int i = 0; i < POPULATION_SIZE; i++) {
            temp = getBigRandom() % PCHANGE;
            if (temp == 1) {
                variation1(array[i]);
                variationTime++;
            }
            temp = getBigRandom() % PCHANGE;
            if (temp == 1) {
                variation2(array[i]);
                variationTime++;
            }
            temp = getBigRandom() % 2 * PCHANGE;
            if (temp == 1) {
                variation3(array[i]);
                variationTime++;
            }
        }
    }


    /**
     * 算法的入口
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
     * @return 操作结果
     */
    @Override
    public Result solve(int vCnt, double[][] graph, double[] demand,
                        int carCnt, double[] carCost, double[] carMaxDis, double[] carMaxLoad,
                        int affectFullLoad, int affectSumDis, int affectSumCost,
                        int fixTimeCost, int carVel)
    {
        //初始化数据
        NODE_SUM = vCnt;
        minDistancesBetweenNodes = new double[NODE_SUM][NODE_SUM];
        fullLoadRatios = new SingleRoad[POPULATION_SIZE][NODE_SUM];
        this.graph = graph;
        this.demands = demand;
        RATIO_OF_FULL_LOAD_AND_PERFORMANCE = affectFullLoad;
        RATIO_OF_TOTAL_DISTANCE_AND_PERFORMANCE = affectSumDis;
        RATIO_OF_TOTAL_PRICE_AND_PERFORMANCE = affectSumCost;

        //开始工作
        floyd();
        init(carMaxLoad, carMaxDis, carCost, carCnt);
        generateFather(father, truckTypeSum);
        adaptabilityCalc(father);
        floyd();
        for (int i = 0; i < MAX_GENERATION; i++) {
            if (i % 2 != 0) {
                generateSons(son, father);
                clear();
                adaptabilityCalc(father);
                floyd();
                geneticsAndVariation(father);
            }
            else {
                generateSons(father, son);
                clear();
                adaptabilityCalc(son);
                floyd();
                geneticsAndVariation(son);
            }
        }




        double evaluation = -1;
        String bestSolutionRoute = "";
        if (MAX_GENERATION % 2 != 0) {
            generateSons(son, father);
            clear();
            adaptabilityCalc(father);
            int cy = findTheBest();
            if (cy != -1) {
                bestSolutionRoute = son[cy];
            }

        }
        else {
            generateSons(father, son);
            clear();
            adaptabilityCalc(son);
            int cy = findTheBest();

            if (cy != -1) {
                bestSolutionRoute = son[cy];
                evaluation = parameter[2][cy];

            }

            for (int i = 0; i < POPULATION_SIZE; i++) {
                for (int k = 0; k < NODE_SUM; k++) {
                    if (fullLoadRatios[i][k].lc == 0) {
                        break;
                    }
                }
            }
        }
         System.out.println("变异次数" + variationTime);
         System.out.println("最短路程" + minDistance);
         System.out.println("最小价格" + minPrice);

        //解析路程
        return new Result(evaluation, minPrice, minDistance, bestSolutionRoute);
    }


}

