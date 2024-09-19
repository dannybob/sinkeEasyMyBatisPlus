package com.sinke.easymybatisplus;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class EInit {
    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    @Autowired
    EProperties eProperties;

    // 根据类名获取 bean name
    private String getBeanName(String className) {
        int index = className.lastIndexOf(".");
        String simpleClassName = index != -1 ? className.substring(index + 1) : className;

        char firstChar = simpleClassName.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z') {
            firstChar -= 'A' - 'a';
        }
        return firstChar + simpleClassName.substring(1);
    }
    @PostConstruct
    void initMybatisTableMapperAndDao() {
        // 扫描数据库实体，根据实体创建对应 mapper、service

        Set<Class<?>> entityClassSet = ClassUtil.scanPackage(eProperties.getScanEntityLocation()+ "");
        if (entityClassSet.isEmpty()) {
            return;
        }
        // 创建实体其他数据库用户的实体类
        Set<Class<?>> thirdDbEntityClassSet = new HashSet<>();
        for (Class<?> entityClass : entityClassSet) {
            if(ObjectUtil.isNull(entityClass.getAnnotation(TableName.class))){
                continue;
            }
            List<String> thirdDbUsers=eProperties.getDbUsers();
            for (String dbUser : thirdDbUsers) {
                List<AnnotationDescription> annotations = new ArrayList<>();
                String tableNameValue = ObjectUtil.isNull(entityClass.getAnnotation(TableName.class)) ? "" : entityClass.getAnnotation(TableName.class).value();
                String value = dbUser.toUpperCase() + "." + (StringUtils.isEmpty(tableNameValue) ? entityClass.getSimpleName() : tableNameValue);
                annotations.add(AnnotationDescription.Builder.ofType(TableName.class).define("value", value).build());
                Class<?> thirdDbUserEntity = new ByteBuddy()
                        .subclass(TypeDescription.Generic.Builder.rawType(entityClass).build())
                        .name(dbUser.toUpperCase() + "_" + entityClass.getSimpleName())
                        .annotateType(annotations)
                        .make()
                        .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                        .getLoaded();
                thirdDbEntityClassSet.add(thirdDbUserEntity);

            }
        }
        if (CollectionUtil.isNotEmpty(entityClassSet)) {
            thirdDbEntityClassSet.addAll(entityClassSet);
        }
        for (Class<?> entityClass : thirdDbEntityClassSet) {
            // 只创建带有 TableName 注解的实体
            TableName tableName = entityClass.getAnnotation(TableName.class);
            if (tableName == null) {
                continue;
            }
            String mapperClassName = "sinke.easym" + ".mapper" + "." + entityClass.getSimpleName() + "Mapper";
            String serviceImplClassName = "sinke.easym"+ ".service" + "." + entityClass.getSimpleName() + "ServiceImpl";
            String serviceClassName = "sinke.easym" + ".service" + "." + entityClass.getSimpleName() + "Service";
            /* 创建 mapper */
            Class<?> mapperClass = new ByteBuddy()
                    .makeInterface(TypeDescription.Generic.Builder.parameterizedType(BaseMapper.class, entityClass).build())
                    .name(mapperClassName)
                    .annotateType(AnnotationDescription.Builder.ofType(Mapper.class).build())
                    .make()
                    .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();
            MapperFactoryBean<?> factoryBean = new MapperFactoryBean<>(mapperClass);
            factoryBean.setSqlSessionFactory(sqlSessionFactory);
            sqlSessionFactory.getConfiguration().addMapper(mapperClass);
            try {
                SpringUtil.registerBean(getBeanName(mapperClassName), factoryBean.getObject());
                System.out.println(String.format("register mapper Bean -> name:%s", getBeanName(mapperClassName)));;
            } catch (Exception e) {
                e.printStackTrace();
            }

            /* 创建 service */
            Class<?> serviceClass = new ByteBuddy()
                    .makeInterface(TypeDescription.Generic.Builder.parameterizedType(IService.class, entityClass).build())
                    .name(serviceClassName)
                    .make()
                    .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();

            List<AnnotationDescription> annotations = new ArrayList<>();
            annotations.add(AnnotationDescription.Builder.ofType(Service.class).build());
            Class<?> serviceImplClass = new ByteBuddy()
                    .subclass(TypeDescription.Generic.Builder.parameterizedType(ServiceImpl.class, mapperClass, entityClass).build())
                    .implement(serviceClass)
                    .name(serviceImplClassName)
                    .annotateType(annotations)
                    .make()
                    .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();
            try {
                SpringUtil.registerBean(getBeanName(serviceImplClassName), serviceImplClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }



    }

}
