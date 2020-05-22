package com.fuchuang.fuchuang.cpp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author orangeboy
 * @version 1.0
 * @date 2020/5/22 2:37
 */
class CppImplTest {

    @Test
    void solve() {
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

        int[] demand = new int[]{
          2,3,6,5,4,8,7,4,5,1,2,5
        };

        Cpp cpp = new CppImpl();
        System.out.println(cpp.solve(11, graph, demand,
                2, new int[]{200, 400}, new int[]{35, 35}, new int[]{2, 5},
                40, 30, 30, 1, 1));
    }
}