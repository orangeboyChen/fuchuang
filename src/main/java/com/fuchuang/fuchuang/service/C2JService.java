package com.fuchuang.fuchuang.service;

import com.fuchuang.fuchuang.cpp.Cpp;
import com.fuchuang.fuchuang.cpp.CppImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @author orangeboy
 * @version 1.0
 * @date 2020/5/18 23:03
 */

@Service
public class C2JService {

    @Autowired
    private Cpp cpp;


    public HashMap<String, Object> get(int[][] graph, int load, int[][] car){
        return cpp.get(graph, load, car);
    }




}
