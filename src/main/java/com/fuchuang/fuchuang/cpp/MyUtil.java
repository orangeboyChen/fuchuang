package com.fuchuang.fuchuang.cpp;

public class MyUtil {
    public  static int split(String strr, char c, String[] s) {            //字符串分割  把str 按 c 分割，保存在 s[50] 中，返回分割段数，常常用strs[50] strs2[50] strs3[50] strs4[50] 作保存数组
        int zhizhen = 0;
        char[] str = strr.toCharArray();
        String zifuchuan = "";
        for (int i = 0; i < str.length; i++) {
            if (str[i] == c) {
                s[zhizhen] = zifuchuan;
                zifuchuan = "";
                zhizhen++;
            }
            else if (i == (str.length - 1)) {
                zifuchuan += str[i];
                s[zhizhen] = zifuchuan;
                zifuchuan = "";
                zhizhen++;
            }
            else {
                zifuchuan += str[i];
            }
        }
        return zhizhen;
    }
}
