package com.fuchuang.fuchuang.service;

import com.fuchuang.fuchuang.cpp.CppImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @author orangeboy
 * @version 1.0
 * @date 2020/5/18 23:03
 */

@Service
public class C2JService {

    CppImpl cppImpl = new CppImpl();


    public HashMap<String, Object> get(){
        cppImpl.main();
    }




}
