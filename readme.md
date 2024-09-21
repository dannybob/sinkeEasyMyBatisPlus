# 进一步简化mybatis-plus的操作
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
