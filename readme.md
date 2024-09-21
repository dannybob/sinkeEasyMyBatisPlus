# 进一步简化mybatis-plus的操作
相信使用过mybatis-plus人都有这样的经历，mybatis-plus确实精简了数据库操作，但是还是需要为每个实体创建其对一个的mapper接口和实现类或者 IService接口和实现类，无形中让开发者多少感觉不太方面，如果能省去这些繁琐的过程，势必会让我们使用mybaitis-plus更爽。
## 使用说明
将该项目使用maven打包后，在自己的项目的pom.xml引入依赖：
```
    <dependency>
        <groupId>com.sinke</groupId>
        <artifactId>easyMyBatisPlus-spring-boot-starter</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
```
## 使用示例
```
    List<Student> studentList=  DbHelper.getTableDao(Student.class).list();
    Student student= (Student) DbHelper.getTableDao(Student.class).getById(1);
    Student updateStudent =new Student();
    DbHelper.getTableDao(Student.class).updateById(updateStudent );
```
