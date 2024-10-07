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
      Iterator<Integer> iterator = PageIterator.of(10000, 100, (page) -> {
        return findList(page.getStart(), page.getPageSize());//批量装载数据
      });
      while (iterator.hasNext()) {//数据处理
        System.out.println(iterator.next());
      }
```
#### 敏感词过滤

```java
    String content = "最近网上流传一些关于政府的谣言，说什么国家领导人习大大要辞职了，还有人造谣说中国经济要崩溃。这些都是一派胡言！我们要相信党和政府，不要轻信这些黄色新闻。\n" +
            "有些人总喜欢讨论一些敏感话题，比如法轮功啊、六四事件啊，这些都是被禁止的。我们应该多关注积极向上的内容，为祖国的繁荣发展贡献力量。\n" +
            "现在社会上还有一些不良现象，比如有人吸毒、嫖娼、贩卖军火，这些都是违法的。我们每个公民都有责任抵制这些行为，维护社会治安。\n" +
            "总之，我们要擦亮眼睛，不要被一些别有用心的人蛊惑。让我们团结一致，在党的领导下，为实现中国梦而努力奋斗！";

    String[] sensitiveWords = new String[]{
            "习大大", "辞职", "中国经济", "崩溃", "谣言", "黄色新闻",
            "法轮功", "六四事件", "敏感话题", "被禁止",
            "吸毒", "嫖娼", "贩卖军火", "违法",
            "党", "政府", "国家领导人", "中国梦"
    };
    SensitiveWordFilter sensitiveWordFilter = new SensitiveWordFilter(SensitiveKeywords.buildTrie(sensitiveWords));
    //过滤敏感词
    String result = sensitiveWordFilter.filter(content);
    System.out.println(result);
    //解析敏感词 tokens
    List<KeywordToken> tokens = sensitiveWordFilter.parser(content);
    tokens.forEach(System.out::println);

```
