package com.sinke.easymybatisplus;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;

public class DbHelper {



    public static <T> IService getTableDao(final Class<T> t) {
        return SpringUtil.getBean(getBeanName(t) + "ServiceImpl");
    }

    /**
     *
     * @param t
     * @param dbUser
     * @return
     * @param <T>
     */
    public static <T> IService getTableDao(final Class<T> t,String dbUser) {
        if(StrUtil.isNotEmpty(dbUser)){
            String name=dbUser.substring(0, 1).toLowerCase() + dbUser.substring(1).toUpperCase();
            return SpringUtil.getBean(name+"_"+ t.getSimpleName() + "ServiceImpl");
        }
        return SpringUtil.getBean(getBeanName(t) + "ServiceImpl");
    }
    public static <T> BaseMapper getTableMapper(final Class<T> t) {
        return SpringUtil.getBean(getBeanName(t) + "Mapper");
    }
    public static <T> BaseMapper getTableMapper(final Class<T> t,String dbUser) {
        if(StrUtil.isNotEmpty(dbUser)){
            String name=dbUser.substring(0, 1).toLowerCase() + dbUser.substring(1);

            return SpringUtil.getBean(name+"_"+ t.getSimpleName() + "Mapper");
        }
        return SpringUtil.getBean(getBeanName(t) + "Mapper");
    }
    private static String getBeanName(final Class t) {
        if(!AnnotationUtil.hasAnnotation(t, TableName.class)){
            throw ExceptionUtil.wrapRuntime(StrUtil.format("传入的{}不是entity类",t.getName()));
        }
        return t.getSimpleName().substring(0, 1).toLowerCase() + t.getSimpleName().substring(1);
    }



    public static <T> QueryWrapper<T> newQueryWrapper(Class <T> cls){
        return new QueryWrapper<T>();

    }
    public static <T> UpdateWrapper<T> newUpdateWrapper(){
        return new UpdateWrapper<T>();
    }
    public static <T>UpdateWrapper<T> newUpdateWrapper(Class <T> cls){
        return new UpdateWrapper<T>();

    }
    public static <T> LambdaQueryWrapper<T> newLambdaQueryWrapper(Class <T> cls){
        return new LambdaQueryWrapper<T>();

    }
    public static <T> LambdaUpdateWrapper<T> newLambdaUpdateWrapper(Class <T> cls){
        return new LambdaUpdateWrapper<T>();
    }
}

