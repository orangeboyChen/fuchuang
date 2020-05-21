package com.fuchuang.fuchuang.cpp;

import com.fuchuang.fuchuang.pojo.Node;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

/**
 * @author orangeboy
 * @version 1.0
 * @date 2020/5/19 10:28
 */

@Component
public class CppImpl extends Thread {
    //共享最优解，多线程同步更新
    private static Result bestRes = new Result(-1.0,99999999,99999999,"");

    private static final int MAXNODEN = 100; //最大节点数
    private int NODEN;           //所有节点数
    private static int ZQSIZE = 50     ;            //种群大小
    private int MZ  ;               //满载率效能占比
    private int LC  ;                   //总路程效能占比
    private int PR  ;                  //总价格效能占比
    private static int ALLMAX = 100000   ;          //作辅助最大
    private static int MAXGEN ;     //总遗传代数
    private static int PCHANGE = 1000     ;         //单条染色体 单种变异方式 概率倒数
    private static int LEAST_REQUEST = 32   ;
    //满载率最小要求（实际上达不到并且不能太高）最大值为 MZ

    public boolean running = false;

   //Scanner scanner = new Scanner(System.in);

    class singleroad {               //gene路线参数
        double manzai = 0;
        double lc = 0;
        int car = 0;
    }

    double linshi[] = new double[ZQSIZE];
    double linshi2[] = new double[ZQSIZE];

    int xianglin[][];  //邻接表
    int minroad[][];
    float goodsnumber[];

    String strs[] = new String[105];
    String strs2[] = new String[105];
    String strs3[] = new String[105];
    String strs4[] = new String[105];


    static int fuzhu = 0;            //用作辅助计算
    static int fuzhu2 = 0;           //用作辅助计算

    static String fuzhubianyi = "";        //辅助gene变异
    int carclass = 0;                        //车辆种类
    float[] carmax;                     //车辆最大限制
    float[] carroad;                 //车辆最大里程
    int[] carprice;                  //车辆费用
    static int minprice = ALLMAX;          //最小价格
    static int minlength = ALLMAX;         //最小路程

    static singleroad[][] manzailv = new singleroad[ZQSIZE][MAXNODEN];

    static double[][] canshu = new double[3][ZQSIZE];      //参数和适应度（canshu[0]表示路程  canshu[1]表示总价格）

    static String[] fudai = new String[ZQSIZE];
    static String[] zidai = new String[ZQSIZE];

    int bianyicishu = 0;

    public static Result getBestRes(){
        return  bestRes;
    }

    @Override
    public void run() {
        //线程运行中
        running = true;

        //初始化参数
        init();

        //产生父代
        createf(fudai, carclass);

        //计算适应度
        shiyingdujisuan(fudai);

        //遗传迭代
        for (int i = 0; i < MAXGEN; i++) {
            if (i % 2 != 0) {
                zidaishengchengqi(zidai, fudai);
                clear();
                shiyingdujisuan(fudai);
                floyd();
                yichuanbianyi(fudai);
            }
            else {
                zidaishengchengqi(fudai, zidai);
                clear();
                shiyingdujisuan(zidai);
                floyd();
                yichuanbianyi(zidai);
            }
        }

        //处理结果
        Result result;
        String[] T1, T2;
        if (MAXGEN % 2 != 0) {
            T1 = zidai.clone();
            T2 = fudai.clone();
        }
        else {
            T1 = fudai.clone();
            T2 = zidai.clone();
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
        zidaishengchengqi(T1, T2);
        clear();
        //shiyingdujisuan(fudai);
        shiyingdujisuan(T2);
        int cy = zuiyou();
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
            shiyingdu((int) canshu[0][cy], minlength);
            shiyingdu((int) canshu[1][cy], minprice);
            result = new Result(canshu[2][cy], minprice, minlength, T2[cy]);
            updateBestRes(result);    //更新最优解
        }

        Solve.finishedCnt ++;

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


    //同步更新最优解
    private synchronized void updateBestRes(Result res){
        if(bestRes.getEvaluation() < res.getEvaluation()){
            bestRes = res;
        }
    }

    public void setAll(int vCnt, int[][] graph, int[] demand,    //顶点数 边长邻接表 需求表
                                       int carCnt, int[] carCost, int[] carMaxDis, int[] carMaxLoad,  //车辆种类数量 车的费用 车的最大里程数 车的最大装载量
                                       int affectFullLoad, int affectSumDis, int affectSumCost,//满载率影响参数 总路程影响参数 总费用影响参数
                                       int fixTimeCost, int carVel){        //每个点的固定卸货时间， 车辆速度



        NODEN = vCnt;
        carclass = carCnt;
        MAXGEN = (NODEN-1)*10;
        MZ = affectFullLoad;
        LC = affectSumDis;
        PR = affectSumCost;

        xianglin = new int[NODEN + 5][NODEN + 5];
        minroad = new int[NODEN + 5][NODEN + 5];
        goodsnumber = new float[NODEN + 5];
        carmax = new float[carclass + 5];
        carroad = new float[carclass + 5];
        carprice = new int[carclass + 5];

        for(int i = 0; i < NODEN; i++) {
            goodsnumber[i] = demand[i];
            for(int j = 0; j < NODEN; j++){
                xianglin[i][j] = graph[i][j];
            }
        }

        for(int i = 0; i < carCnt; i++){
            carmax[i] = carMaxLoad[i];
            carroad[i] = carMaxDis[i];
            carprice[i] = carCost[i];
        }
    }



    void init() {       //初始化参数
        floyd();
        for (int i = 0; i < ZQSIZE; i++){
            fudai[i] = "";
            zidai[i] = "";
            canshu[0][i] = 0;
            canshu[1][i] = 0;
            canshu[2][i] = 0;
            linshi[i] = 0;
            linshi2[i] = 0;
            if(i < 105){
                strs[i] = "";
                strs2[i] = "";
                strs3[i] = "";
                strs4[i] = "";
            }

            for(int j = 0; j < NODEN; j++){
                manzailv[i][j] = new singleroad();
            }
        }
    }

    void floyd() {
        for (int i = 0; i < NODEN; i++) {
            for (int k = 0; k < NODEN; k++) {
                if (i != k && xianglin[i][k] == 0) {
                    minroad[i][k] = ALLMAX;
                }
                else {
                    minroad[i][k] = xianglin[i][k];
                }
            }
        }
        for (int k = 0; k < NODEN; k++) {
            for (int i = 0; i < NODEN; i++) {
                for (int j = 0; j < NODEN; j++) {
                    if (minroad[i][j] > (minroad[i][k] + minroad[k][j])) {
                        minroad[i][j] = minroad[i][k] + minroad[k][j];
                    }
                }
            }
        }
        return;
    }

    double manzailvshiyingdu(double a) {           //gene满载率的评价
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

    double shiyingdu(int canshu, int min) {
        if (canshu > 1.4 * min) {
            return 0;
        }
        else {
            return 1 - 25.0 * ((double)canshu / min - 1) * ((double)canshu / min - 1) / 4;
        }
    }

    void createf(String[] shuzu, int carclass) {         //父代产生器 产生的染色体表现为：     gene:  A-3-4-1-A (A~Z表示种类，正整数表示配送点（中心为0）)
        int zhonglei = 0;                                      //                                    gene与gene之间用 “#” 隔开 染色体保存在数组中
        int[] shu = new int[NODEN];
        int fuzhu3 = 0;
        int zhizhen = 0;
        String chuan = "";
        Random r = new Random(System.currentTimeMillis());
        for (int i = 0; i < ZQSIZE; i++) {
            zhizhen = 0;
            for (int j = 0; j < NODEN - 1; j++) {               //重新设置所有站点访问情况
                shu[j] = j + 1;
            }
            for (int j = 0; j < 2 * NODEN - 2; j++) {           //洗牌
                fuzhu = r.nextInt(32767) % (NODEN - 1);
                fuzhu2 = r.nextInt(32767) % (NODEN - 1);
                fuzhu3 = shu[fuzhu];
                shu[fuzhu] = shu[fuzhu2];
                shu[fuzhu2] = fuzhu3;
            }
            for (int j = 0; j < NODEN - 1; j++) {               //随机化产生路线
                fuzhu = r.nextInt(32767) % (NODEN - 1);
                fuzhu++;
                zhonglei = r.nextInt(32767) % carclass;
                shuzu[i] += (char)(zhonglei + 'A');
                shuzu[i] += '-';
                if ((zhizhen + fuzhu) < NODEN - 1) {
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
                    for (int k = zhizhen; k < NODEN - 1; k++) {
                        chuan = String.valueOf(shu[k]);
                        shuzu[i] += chuan;
                        shuzu[i] += '-';
                    }
                    shuzu[i] += (char)(zhonglei + 'A');
                    break;
                }
            }
        }
        return;
    }



    void shiyingdujisuan(String[] shuzu) {                  //用于计算适应度 最终结果：manzailv[ZQSIZE][NODEN]  中保存着染色体每个gene的参数
        int zongjia = 0;                                            //                            (满载率评价)（gene路线长度）（gene所用车辆）
        double zonglucheng = 0;                                        //                         canshu[3][ZQSIZE] 中保存着染色体参数
        double manzai = 0;                                          //                             canshu[0]中储存染色体总路程长度
        int star = 0;                                             //                             canshu[1]中储存染色体总所需价格
        int end = 0;                                              //                             canshu[0]中储存染色体总适应度
        for (int i = 0; i < ZQSIZE; i++) {
            linshi2[i] = 1;
        }
        for (int i = 0; i < ZQSIZE; i++) {
            zongjia = 0;
            zonglucheng = 0;
            int shu = MyUtil.split(shuzu[i], '#', strs);
            for (int k = 0; k < shu; k++) {
                int nei = MyUtil.split(strs[k], '-', strs2);
                manzai = 0;
                zongjia += carprice[strs2[0].charAt(0) - 'A'];              //累计总价
                manzailv[i][k].car = strs2[0].charAt(0) - 'A';              //记录车辆
                for (int p = 1; p < nei - 1; p++) {

                    end = Integer.parseInt(strs2[p]);
                    manzailv[i][k].lc += minroad[star][end];       //累计单路路程
                    star = end;                                    //重置位置
                    manzai += goodsnumber[end];                    //累计满载
                }
                end = 0;
                manzailv[i][k].lc += minroad[star][end];           //累计单路路程
                if (manzailv[i][k].lc > carroad[strs2[0].charAt(0) - 'A']) {        //路程超限
                    manzailv[i][k].lc = ALLMAX;
                    linshi2[i] = 0;
                }
                else {
                    zonglucheng += manzailv[i][k].lc;                 //累计路程
                }
                manzailv[i][k].manzai = manzailvshiyingdu(manzai / carmax[strs2[0].charAt(0) - 'A']) * MZ;   //计算单路满载率适应度
                if ((int)manzailv[i][k].manzai == 0) {
                    linshi2[i] = 0;
                }
            }
            canshu[0][i] = zonglucheng;
            canshu[1][i] = zongjia;
        }
        int jishu = 0;
        double he = 0;
        for (int i = 0; i < ZQSIZE; i++) {
            jishu = 0;
            he = 0;
            for (int j = 0; j < NODEN - 1; j++) {
                if (manzailv[i][j].lc != 0) {
                    if (manzailv[i][j].lc != ALLMAX) {       //不能超路程限制
                        jishu++;
                        he += manzailv[i][j].manzai;
                    }
                }
                else {
                    if (jishu != 0) {
                        canshu[2][i] = he / jishu;           //gene满载率平均评价作为染色体总满载率评价
                        linshi[i] = he / jishu;
                    }
                    break;
                }
            }
        }
        for (int i = 0; i < ZQSIZE; i++) {
            if (linshi2[i] != 0) {
                if (canshu[0][i] < minlength) {
                    minlength = (int)canshu[0][i];
                }
                if (canshu[1][i] < minprice) {
                    minprice = (int)canshu[1][i];
                }
            }
        }
        for (int i = 0; i < ZQSIZE; i++) {
            if (linshi2[i] == 1) {
                canshu[2][i] += shiyingdu((int)canshu[0][i], minlength) * LC;
                canshu[2][i] += shiyingdu((int)canshu[1][i], minprice) * PR;
            }
        }
        return;
    }

    String bianyi1(String a) {
        //对调变异（染色体中的节点相互调换）
        Random r = new Random(System.currentTimeMillis());
        fuzhubianyi = "";
        fuzhu = r.nextInt(32767) % (NODEN - 1) + 1;
        fuzhu2 = r.nextInt(32767) % (NODEN - 1) + 1;
        while (fuzhu2 == fuzhu) {
            fuzhu2 = r.nextInt(32767) % (NODEN - 1) + 1;
        }
        String s1 = "", s2 = "";
        s1 = String.valueOf(fuzhu);

        s2 = String.valueOf(fuzhu2);
        int shu = MyUtil.split(a, '#', strs);
        int nei = 0;
        for (int i = 0; i < shu; i++) {
            nei = MyUtil.split(strs[i], '-', strs2);
            for (int k = 1; k < (nei - 1); k++) {
                if (strs2[k] == s1) {
                    strs2[k] = s2;
                    continue;
                }
                if (strs2[k] == s2) {
                    strs2[k] = s1;
                }
            }
            for (int k = 0; k < (nei - 1); k++) {
                fuzhubianyi += strs2[k] + '-';
            }
            if (i != (shu - 1)) {
                fuzhubianyi += strs2[nei - 1] + '#';
            }
            else {
                fuzhubianyi += strs2[nei - 1];
            }
        }
        return fuzhubianyi;
    }

    String bianyi2(String aa) {
        //插入变异（将染色体中某一个节点插入到另一个节点之前）
        char[] a = aa.toCharArray();
        Random r = new Random(System.currentTimeMillis());
        fuzhubianyi = "";
        fuzhu = r.nextInt(32767) % (NODEN - 1) + 1;
        String s1 = "";


        s1 = String.valueOf(fuzhu);
        int dangqian = 0;
        fuzhu2 = r.nextInt(32767) % (NODEN - 1) + 1;
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

    String bianyi3(String aa) {                                    //车辆类型变异（gene所用车辆更换）
        Random r = new Random(System.currentTimeMillis());
        char[] a = aa.toCharArray();
        fuzhubianyi = "";
        int shu = MyUtil.split(aa, '#', strs);
        fuzhu = r.nextInt(32767) % shu + 1;
        fuzhu2 = r.nextInt(32767) % carclass;
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

        fuzhu = MyUtil.split(shuzu[a], '#', strs);
        fuzhu2 = MyUtil.split(shuzu[b], '#', strs2);
        fuzhubianyi = "";

        int shu = r.nextInt(32767) % fuzhu;
        fuzhubianyi += strs[shu];
        int jiaochagene = MyUtil.split(strs[shu], '-', strs3);
        String pianduan = "";
        int genelength = 0;
        for (int i = 0; i < fuzhu2; i++) {
            pianduan = "";
            genelength = MyUtil.split(strs2[i], '-', strs4);
            pianduan += strs4[0] + '-';
            for (int k = 1; k < genelength - 1; k++) {
                for (int p = 1; p < jiaochagene - 1; p++) {
                    if (strs4[k] == strs3[p]) {
                        strs4[k] = "";
                        break;
                    }
                }
                if (strs4[k] != "") {
                    pianduan += strs4[k] + '-';
                }
            }
            pianduan += strs4[genelength - 1];
            if (pianduan.length() > 3) {
                fuzhubianyi += '#' + pianduan;
            }
        }

        String fuzhubianyi2 = "";
        shu = r.nextInt(32767) % fuzhu2;
        fuzhubianyi2 += strs2[shu];
        jiaochagene = MyUtil.split(strs2[shu], '-', strs3);
        for (int i = 0; i < fuzhu; i++) {
            pianduan = "";
            genelength = MyUtil.split(strs[i], '-', strs4);
            pianduan += strs4[0] + '-';
            for (int k = 1; k < genelength - 1; k++) {
                for (int p = 1; p < jiaochagene - 1; p++) {
                    if (strs4[k] == strs3[p]) {
                        strs4[k] = "";
                        break;
                    }
                }
                if (!strs4[k].equals("")) {
                    pianduan += strs4[k] + '-';
                }
            }
            pianduan += strs4[genelength - 1];
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

    void zidaishengchengqi(String[] shuzu, String[] next) {              //由父代产生子代的过程
        for (int i = 0; i < ZQSIZE; i++) {                                          //子代染色体构造方法：1.按照适应度轮盘选择
            next[i] = "";                                                           //                    2.所有满载率评价大于 LEASTREQUEST 的gene直接遗传
        }                                                                           //                    3.如果2中没有符合要求的染色体那么将满载率评价最高的gene加入子代染色体
        for (int i = 1; i < ZQSIZE; i++) {                                          //                    4.随机化生成其余所有节点的路线
            canshu[2][i] += canshu[2][i - 1];
        }
        Random r = new Random(System.currentTimeMillis());
        double lunpanzhizhen = 0;
        int max = 0;
        int yn = 0;
        int ranseti = 0;
        for (int i = 0; i < ZQSIZE; i++) {
            ranseti = 0;
            lunpanzhizhen = bigRand() % (int)(canshu[2][ZQSIZE - 1] * 10000);       //轮盘赌注
            lunpanzhizhen /= 10000;
            for (int k = 0; k < ZQSIZE; k++) {
                if (canshu[2][k] >= lunpanzhizhen) {
                    ranseti = k;
                    break;
                }
            }
            max = 0;
            yn = 0;
            fuzhu = MyUtil.split(shuzu[ranseti], '#', strs);
            for (int k = 1; k < NODEN; k++) {
                linshi[k] = 0;
            }
            int  fuzhu3 = 0;
            for (int k = 0; k < fuzhu; k++) {
                if (manzailv[ranseti][k].manzai > LEAST_REQUEST && manzailv[ranseti][k].lc != ALLMAX) {  //所有满载率评价大于 LESSREQUEST 的gene直接遗传
                    next[i] += strs[k] + '#';
                    fuzhu2 = MyUtil.split(strs[k], '-', strs2);
                    for (int p = 1; p < fuzhu2 - 1; p++) {
                        fuzhu3 = Integer.parseInt(strs2[p]);
                        linshi[fuzhu3] = 1;
                    }
                    yn = 1;
                }
                if (manzailv[ranseti][k].manzai > manzailv[ranseti][max].manzai && manzailv[ranseti][k].lc != ALLMAX) {
                    max = k;
                }
            }
            if (yn == 0 && max != -1) {                                                     //如果2中没有符合要求的染色体那么将满载率评价最高的gene加入子代染色体
                next[i] += strs[max] + '#';
                fuzhu2 = MyUtil.split(strs[max], '-', strs2);
                for (int p = 1; p < fuzhu2 - 1; p++) {
                    fuzhu3 = Integer.parseInt(strs2[p]);
                    linshi[fuzhu3] = 1;
                }
            }
            int zhizhen = 0;                                //随机化生成其余所有节点的路线
            int fuzhu4 = 0;
            for (int k = 1; k < NODEN; k++) {
                if (linshi[k] == 0) {
                    strs3[zhizhen] = String.valueOf(k);
                    zhizhen++;
                }
            }
            for (int k = 0; k < zhizhen; k++) {
                fuzhu3 = r.nextInt(32767) % zhizhen;
                fuzhu4 = r.nextInt(32767) % zhizhen;
                String zhongjian = strs3[fuzhu4];
                strs3[fuzhu4] = strs3[fuzhu3];
                strs3[fuzhu3] = zhongjian;
            }
            int yifangwen = 0;
            for (int k = 0; k < NODEN; k++) {
                if (zhizhen >= 2) {
                    fuzhu3 = r.nextInt(32767) % (zhizhen - 1) + 1;
                }
                else {
                    fuzhu3 = zhizhen;
                }
                fuzhu4 = r.nextInt(32767) % carclass;
                next[i] += (char)(fuzhu4 + 'A');
                next[i] += '-';
                if (yifangwen + fuzhu3 < zhizhen) {
                    for (int p = yifangwen; p < yifangwen + fuzhu3; p++) {
                        next[i] += strs3[p] + "-";
                    }
                    yifangwen += fuzhu3;
                    next[i] += (char)(fuzhu4 + 'A');
                    next[i] += '#';
                }
                else {
                    for (int p = yifangwen; p < zhizhen; p++) {
                        next[i] += strs3[p] + "-";
                    }
                    next[i] += (char)(fuzhu4 + 'A');
                    break;
                }
            }
        }
        return;
    }

    int zuiyou() {                                                             //结束时在所有染色体中找到最优解
        double max = 0;
        int yn = 0, zhizhen = -1;
        for (int i = 0; i < ZQSIZE; i++) {
            for (int k = 0; k < NODEN; k++) {
                if ((manzailv[i][k].manzai != 0 && manzailv[i][k].lc == ALLMAX) || (!(manzailv[i][k].manzai != 0) && manzailv[i][k].lc != 0)) { //gene载货评价不为0，gene路程不超限
                    yn = 1;
                    break;
                }
            }
            if (yn == 0 && canshu[2][i] > max) {
                max = canshu[2][i];
                zhizhen = i;
            }
            yn = 0;
        }
        return zhizhen;
    }

    void clear() {                                      //清空manzailv[][] 和 canshu[][]
        for (int i = 0; i < manzailv.length; i++) {
            for (int j = 0; j < manzailv[0].length; j++) {
                manzailv[i][j].car = 0;
                manzailv[i][j].lc = 0;
                manzailv[i][j].manzai = 0;
            }
            canshu[0][i] = 0;
            canshu[1][i] = 0;
            canshu[2][i] = 0;
        }
    }

    void yichuanbianyi(String[] shuzu) {          //按照 PCHANGE 对当前种群进行四种遗传变异
        for (int i = 0; i < ZQSIZE; i++) {
            fuzhu = bigRand() % PCHANGE;
            if (fuzhu == 1) {
                int a = 0;
                int b = 0;
                a = bigRand() % ZQSIZE;
                b = bigRand() % ZQSIZE;
                jiaocha(a, b, shuzu);
            }
        }
        for (int i = 0; i < ZQSIZE; i++) {
            fuzhu = bigRand() % PCHANGE;
            if (fuzhu == 1) {
                bianyi1(shuzu[i]);
                bianyicishu++;
            }
            fuzhu = bigRand() % PCHANGE;
            if (fuzhu == 1) {
                bianyi2(shuzu[i]);
                bianyicishu++;
            }
            fuzhu = bigRand() % 2 * PCHANGE;
            if (fuzhu == 1) {
                bianyi3(shuzu[i]);
                bianyicishu++;
            }
        }
        return;
    }

}

