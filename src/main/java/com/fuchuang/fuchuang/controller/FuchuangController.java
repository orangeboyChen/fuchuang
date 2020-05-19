package com.fuchuang.fuchuang.controller;

import com.fuchuang.fuchuang.cpp.CppImpl;
import com.fuchuang.fuchuang.service.C2JService;
import com.sun.jna.Library;
import com.sun.jna.Native;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author orangeboy
 * @version 1.0
 * @date 2020/5/16 19:08
 */
@Controller
public class FuchuangController {

    @Autowired
    private C2JService c2JService;

    @Autowired
    private CppImpl cppImpl;

    public interface JnaLibrary extends Library {
        JnaLibrary JNA_LIBRARY = Native.load("fuchuang", JnaLibrary.class);
        int get();
    }

    @GetMapping("/")
    public String get(){
        cppImpl.main();
        return null;
    }
}
