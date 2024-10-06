toolkit-api
============
<div align="left">
  <a href="javascript:void(0);"><img src="https://img.shields.io/badge/build-passing-brightgreen" /></a>
  <a href="javascript:void(0);" target="_blank"><img src="https://img.shields.io/badge/docs-latest-brightgreen" /></a>
  <a href="https://www.apache.org/licenses/LICENSE-2.0"><img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg" alt="License"></a>
  <a href="https://central.sonatype.com/artifact/cn.taskflow/toolkit-api?smo=true"><img src="https://img.shields.io/maven-metadata/v.svg?label=Maven%20Central&metadataUrl=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2Fcn%2Ftaskflow%2Ftoolkit-api%2Fmaven-metadata.xml" alt="License"></a>
</div>

## 简述
    轻量级通用工具API库

### 功能
* Java 函数式接口+Lambda表达式实现流式编排函数工具API
* 使用Lambda表达式对编写出来的代码可读性较好，毕竟程序是给人看的，给机器执行的。
* 通过lambda表达式编写的API其根本就是一些语法糖而已

### 安装

要将 TaskFlow 集成到您的 Java 项目中。

Maven 项目在 'pom.xml' 文件中添加以下依赖：
```xml
<dependency>
    <groupId>cn.taskflow</groupId>
    <artifactId>toolkit-api</artifactId>
    <version>latest</version>
</dependency>
```
Gradle 项目添加以下依赖：
```groovy
    dependencies {
        implementation 'cn.taskflow:toolkit-api:latest'
    }
```

### 示例程序

#### 分页编排式API

```java
       // 写法一
           Func.pageFunc(1, 5, Integer.class).count(() -> {
             return 100;// dao.count();
           }).find((p) -> {
             return findList(p.getStart(), p.getPageSize());
           }).forEach((e) -> {
             System.out.println("返回元素:"+e);
             Assert.assertNotNull(e); 
           });
   
    
        // 写法二
            Func.pageFunc(1, 5, Integer.class).count(() -> {
              return 100;// 只有这里返回大于零，才会执行下面的find查询
            }).find((page) -> {
              return findList(page.getStart(), page.getPageSize());
            }).done((res, page) -> {
              Pagination pagination = page.getPagination();
              System.out.println("分页实体："+pagination.toString());
              System.out.println("返回List:"+res);
            });

```

#### 分页迭代器--批量装载并处理数据
```Java
      PageIterator<Integer> iterator = PageIterator.of(10000, 100, (page) -> {
        return findList(page.getStart(), page.getPageSize());//批量装载数据
      });
      while (iterator.hasNext()) {//数据处理
        System.out.println(iterator.next());
      }
```

