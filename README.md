# Trubbo

Trubbo是一款参考Dubbo框架实现的RPC框架,底层网络通信基于Netty实现,采用Kryo进行序列化,使用Zookeeper作为注册中心,且与Spring
Boot框架整合.

## 快速开始

### 添加依赖

拉取项目后,执行以下命令

```shell
mvn clean install -DskipTests
```

在需要的项目中添加以下依赖

```maven
<dependency>
     <groupId>com.maxchen</groupId>
     <artifactId>trubbo-spring</artifactId>
     <version>1.0-SNAPSHOT</version>
</dependency>
```

### 创建API模块

例如,可以创建如下接口:

```java
public interface TestService {
    User getUser(int id);

    CompletableFuture<User> getUserAsync(int id);

    Boolean addUser(User user);
}
```

`User`类:

```java

@ToString
@AllArgsConstructor
public class User {
    @Getter
    long id;
    String name;
    List<String> list;
}
```

### 启动Zookeeper

这里以使用docker启动为例:

```shell
docker pull zookeeper
cd /usr/local && mkdir zookeeper && cd zookeeper
mkdir data
docker run -d -e TZ="Asia/Shanghai" -p 2181:2181 -v $PWD/data:/data --name zookeeper --restart always zookeeper
```

### 服务提供端使用

在添加api模块,`trubbo-spring`和Spring Boot的依赖后,在`application.yml`中添加以下配置:

```yaml
trubbo:
  zookeeper:
    address: 127.0.0.1:2181 # zookeeper地址
  url: 127.0.0.1:8081 #本机公网IP:暴露的端口 因为是本地测试故IP为127.0.0.1
```

在服务实现类上添加注解`@TrubboService`,例如:

```java

@TrubboService(TestService.class) // 指定接口
public class TestServiceImpl implements TestService {
    private static final Map<Integer, User> DATABASE = new HashMap<>() {{
        put(1, new User(1L, "Jack", new ArrayList<>(List.of("1", "2"))));
        put(2, new User(2L, "Mike", new ArrayList<>(List.of("3", "4"))));
        put(3, new User(3L, "Lily", new ArrayList<>(List.of("5", "6"))));
    }};

    @Override
    public User getUser(int id) {
        return DATABASE.get(id);
    }

    @Override
    public CompletableFuture<User> getUserAsync(int id) {
        return CompletableFuture.supplyAsync(() -> DATABASE.get(id));
    }

    @Override
    public Boolean addUser(User user) {
        DATABASE.put((int) user.getId(), user);
        return true;
    }
}
```

在启动类中添加`@EnableTrubbo`注解,例如:

```java

@SpringBootApplication
@EnableTrubbo
public class TrubboProviderSpringTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrubboProviderSpringTestApplication.class, args);
    }
}
```

启动Spring Boot,看见以下输出表明Trubbo被成功导入且初始化完成:

```text
.___________..______       __    __  .______   .______     ______   
|           ||   _  \     |  |  |  | |   _  \  |   _  \   /  __  \  
`---|  |----`|  |_)  |    |  |  |  | |  |_)  | |  |_)  | |  |  |  | 
    |  |     |      /     |  |  |  | |   _  <  |   _  <  |  |  |  | 
    |  |     |  |\  \----.|  `--'  | |  |_)  | |  |_)  | |  `--'  | 
    |__|     | _| `._____| \______/  |______/  |______/   \______/  
    
...

2024-12-06T22:33:09.414+08:00  INFO 16296 --- [           main] c.m.trubbo.spring.TrubboInitializer      : Trubbo initialization finished

```

### 服务消费端使用

添加api模块以及`trubbo-spring`和`Spring Boot`的依赖后,在`application.yml`中添加以下配置:

```yaml
trubbo:
  zookeeper:
    address: 127.0.0.1:2181 # zookeeper地址
```

在需要调用RPC的接口上添加注解`@TrubboReference`,例如:

```java

@Component
public class Task implements CommandLineRunner {
    @TrubboReference
    private TestService testService;

    @Override
    public void run(String... args) {
        System.out.println(new Date() + " Receive result ======> " + testService.getUser(1));
        testService.getUserAsync(2).thenAccept(user -> System.out.println(new Date() + " Receive result ======> " + user));
        if (testService.addUser(new User(1, "maxchen", new ArrayList<>()))) {
            System.out.println("Add user success");
        }
        System.out.println(new Date() + " Receive result ======> " + testService.getUser(1));
    }
}

```

同样在启动类中添加`@EnableTrubbo`注解,启动Spring Boot,不出意外的话可以看见如下输出:

```text
Fri Dec 06 22:33:20 HKT 2024 Receive result ======> User(id=1, name=Jack, list=[1, 2])
Fri Dec 06 22:33:20 HKT 2024 Receive result ======> User(id=2, name=Mike, list=[3, 4])
Add user success
Fri Dec 06 22:33:20 HKT 2024 Receive result ======> User(id=1, name=maxchen, list=[])
```
