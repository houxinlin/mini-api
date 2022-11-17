# mini-api
 一个轻量级的后端框架，使用方式同SpringBoot一样。
 
# 最新版本
```text
1.1.2
```
# 依赖
### Gradle
```xml
implementation 'com.houxinlin:mini-api:{version}'
```
### Maven
```xml
<dependency>
    <groupId>com.houxinlin</groupId>
    <artifactId>mini-api</artifactId>
    <version>{version}</version>
</dependency>
```

# 教程
### 创建实例
```java
public class Main{
    public static void main(String[] args) {
        CoolMini coolMini = new CoolMini(4951);
        coolMini.start(Main.class);
    }
}
```

### 创建映射
返回类型可以是任意类型，同SpringBoot一样，最终序列化方式使用Gson，可以自定义序列化方式。
```java
@RestController
public class IndexController{
    @GetMapping("test")
    public String test(@RequestParam("userName")  String userName) {
        System.out.println(userName);
        return "OK";
    }
}
```
可指定GET、POST、PUT、DELETE方式，并提供了以下注解用来获取参数。
1. @PathVariable 同SpringBoot
2. @RequestBody 用来获取请求体，实际接收类型可以是String、基本数据类型、自定义对象，当是自定义对象时会通过Gson尝试把requestbody转换为对应类型。
3. @RequestParam 获取请求参数，实际接收类型可以是String、基本数据类型、某对象，当为某对象时，如果系统无法自定义转换，可以通过HttpParameterTypeConverter进行自定义转换。
4. @RequestUri 获取本次请求的完整路径

**注意:** mini-api会扫描启动类所在包以及子包下所有标有@RestController注解的类进行注册。
## 获取Request & Response
HttpRequest对应于Servlet规范中的HttpServletRequest。

HttpResponse对应于Servlet规范中的HttpServletResponse。

但对他们进行了简化。
```java
@RestController
public class TestController {
    @GetMapping("test")
    public String test(HttpRequest request, HttpResponse httpResponse){
        return "OK";
    }
}
```
### 数据库操作
系统集成了Mybatis，并简化了使用，需要在启动之前通过setDataSource设置一个数据源。
```java
public class Main {
    public static void main(String[] args) {
        CoolMini coolMini = new CoolMini(4951);
        coolMini.setDataSource(new MysqlDataSource("root","xx","jdbc:mysql://xxx:3306/name"));
        coolMini.start(Main.class);
    }
}
```
可通过@AutowriteCrud自动注入一个MybatisCrudRepository实例
```java
@RestController
public class IndexController {
    @AutowriteCrud
    MybatisCrudRepository mybatisCrudRepository;
}
```
MybatisCrudRepository提供了getMapper方法用来使用原生Mybatis通过Mapper接口查询的方法，但也可以使用其list方法传递sql语句以及参数进行查询。
```java
int userId=1;

mybatisCrudRepository.list("select * from user where id=?",userId)
```

### 自定义拦截器
```java
public class Main {
    public static void main(String[] args)  {
        CoolMini coolMini = new CoolMini(4951);
      
        coolMini.addHttpIntercept(new HttpIntercept() {
            @Override
            public boolean intercept(@NotNull HttpRequest httpRequest, @NotNull HttpResponse httpResponse) {
                //返回true表示拦截，false则不拦截
                return false;
            }

            @Override
            public void postHandler(@NotNull HttpRequest httpRequest, @NotNull HttpResponse httpResponse) {
               //拦截后会被回调
            }
        });
       coolMini.start(Main.class);
    }
}

```
### 自定义参数转换器
如果想把请求参数转换为自定义数据类型，可以添加HttpParameterTypeConverter。
```java
@GetMapping("test")
public String test(@RequestParam("user") User user){
        return "OK";
}
```
```java
public class Main {
    public static void main(String[] args) {
        CoolMini coolMini = new CoolMini(4951);
        coolMini.addHttpParameterTypeConverter(true, new HttpParameterTypeConverter<User>() {
            @Override
            public boolean canConvert(@NotNull MethodParameter methodParameter, @NotNull String s) {
                return true;
            }

            @Nullable
            @Override
            public User typeConvert(String s) {
                return new User(s);
            }
        });
    }
}
```

# 打包

注意，使用Gradle默认不会打包依赖，所以要进行以下配置。
```gradle
jar{
    manifest {
      attributes("Main-Class":"Main")
    }
    from{
        configurations.runtimeClasspath.findAll { it.name.endsWith('jar') }.collect {
            zipTree(it)
        }
    }
    duplicatesStrategy(DuplicatesStrategy.EXCLUDE)

}
```

下面是使用Kotlin Gradle方式
```kotlin
tasks.jar{
    manifest {
        attributes.set("Main-Class","MainKt")
    }
    val contents = configurations.runtimeClasspath.get()
        .map { if (it.isDirectory) it else zipTree(it) }
    from(contents)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
```