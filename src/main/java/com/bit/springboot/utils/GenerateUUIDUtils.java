package com.bit.springboot.utils;

import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

/**
 * @author LeLe
 * @date 2024/5/31 12:34
 * @Description:\
 */
public class GenerateUUIDUtils {
    public static String hashCode(String str) {
        Long hash = 0L;
        for (int i = 0; i < str.length(); i++) {
            hash = 31 * hash + str.charAt(i);
        }
        return String.valueOf(hash);
    }

    public static String generateUniqueId(Long num1, Long num2) {
        Long[] nums = {num1, num2};
        Arrays.sort(nums);
        return hashCode(nums[0] + "-" + nums[1]);
    }
}
