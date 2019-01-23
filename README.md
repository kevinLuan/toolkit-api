
####Java 函数式接口+Lambda表达式实现流式编排函数工具API
  
    // 使用Lambda表达式对编写出来的代码可读性较好，毕竟程序是给人看的，给机器执行的。
    // 通过lambda表达式编写的API其根本就是一些语法糖而已
  
##### 分页编排式API

```java
  
    // 使用Lambda表达式对编写出来的代码可读性较好，毕竟程序是给人看的，给机器执行的。
       // 写法一
           PageFunc.pageFunc(1, 5, Integer.class).count(() -> {
             return 100;// dao.count();
           }).find((p) -> {
             return findList(p.getStart(), p.getPageSize());
           }).forEach((e) -> {
             System.out.println("返回元素:"+e);
             Assert.assertNotNull(e); 
           });
   
    
        // 写法二
            PageFunc.pageFunc(1, 5, Integer.class).count(() -> {
              return 100;// 只有这里返回大于零，才会执行下面的find查询
            }).find((page) -> {
              return findList(page.getStart(), page.getPageSize());
            }).done((res, page) -> {
              Pagination pagination = page.getPagination();
              System.out.println("分页实体："+pagination.toString());
              System.out.println("返回List:"+res);
            });

```

##### 分页迭代器--批量装载并处理数据
```Java
      PageIterator<Integer> iterator = PageIterator.of(10000, 100, (page) -> {
        return findList(page.getStart(), page.getPageSize());//批量装载数据
      });
      while (iterator.hasNext()) {//数据处理
        System.out.println(iterator.next());
      }
```
 
##### 问题反馈&建议
    mailto: 136713318@qq.com
  