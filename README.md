# 介绍

netftp 是对 \[apache common net\]、 [ftp4j](https://github.com/asbachb/ftp4j) 、\[jsch\] 等 ftp 客户端组件的整理。把在项目中常用的上传、下载、迁移等操作做了包装。简化 ftp 操作代码。

支持 sftp、ftp、ftps。




# 安装

## -> maven

```xml
<dependency>
  <groupId>io.github.ludongrong</groupId>
  <artifactId>netftp</artifactId>
  <version>1.1.1</version>
</dependency>
```



## -> Gradle

```
compile 'io.github.ludongrong:netftp:1.1.1'
```



# 简单使用

## -> 构建 ftpConfig

### -> ftp 协议（被动模式）

```java
FtperConfig ftperConfig = FtperConfig.withHost("127.0.0.1")
    .withPort(21)
    .withUsername("1")
    .withPassword("1")
    .withPasvMode(true)
    .withProtocol(FtperConfig.ProtocolEnum.ftp)
    .build();
```



### -> ftp 协议（主动模式）

```java
FtperConfig ftperConfig = FtperConfig.withHost("127.0.0.1")
    .withPort(21)
    .withUsername("1")
    .withPassword("1")
    .withPasvMode(true)
    .withProtocol(FtperConfig.ProtocolEnum.ftp)
    .build();
```



### -> ftps 协议

```java
FtperConfig ftperConfig = FtperConfig.withHost("127.0.0.1")
    .withPort(21)
    .withUsername("1")
    .withPassword("1")
    .withPasvMode(true)
    .withProtocol(FtperConfig.ProtocolEnum.ftps)
    .build();
```



### -> sftp 协议

```java
FtperConfig ftperConfig = FtperConfig.withHost("127.0.0.1")
    .withPort(21)
    .withUsername("1")
    .withPassword("1")
    .withProtocol(FtperConfig.ProtocolEnum.sftp)
    .build();
```



## -> 构建 ftper

```java
IFtper ftper = FtperFactory.createFtper(ftperConfig);
```



## -> 使用 ftper

```java
// 创建目录
ftper.createDirectory("/test/dir1");
// 上传
ftper.upload("/test/dir2", "test.csv", byteis);
// 下载
ftper.down("/test/dir2", "test.csv", byteos));
// 迁移
ftper.move("/test/dir2", "test.csv", "/test/dir1", "test.txt");
// 删除文件
ftper.delete("/test/dir1", "test.txt");
// 删除目录
ftper.delete("/test", "dir1");
```



# 扩展

在实际项目应用当中， ftp 的操作往往是批量操作且带有操作的前置条件。换句话讲不是简单进行一次上传、下载、迁移等操作。比如删除远程某目录下w开头的文件、再比如把远程某目录下的某某文件迁移到某目录下，然后再下载该文件等。

## -> 删除某目录下某文件

```java
Watched watched = new Watched(new Filter[] {
    new DirectoryFiler("/test/w*", true), 
    new FileFiler("*1*", true), 
    new DeleteAction()});

DirectoryWatcher watcher = new DirectoryWatcher(ftperConfig, "/test");
watcher.addObserver(watched);
watcher.hit(true);
```



## -> 删除过期文件

```java
Watched watched = new Watched(new Filter[] {
    new FileModifyTimeFilter(System.currentTimeMillis(), true), 
    new DeleteAction()});
        
DirectoryWatcher watcher = new DirectoryWatcher(ftperConfig, "/test");
watcher.addObserver(watched);
watcher.hit(true);
```

备注：删除 System.currentTimeMillis() 时间以前的文件。当然正常逻辑不会删除当前时间以前的文件，而是删除一小时以前或一天以前文件。这时候就要把 System.currentTimeMillis() 减去 一天的时间时间戳后传入。



## -> 下载csv文件

```java
Watched watched = new Watched(new Filter[] {
    new FileFiler("*0.csv", true), 
    new DownloadAction(projectPath)});

DirectoryWatcher watcher = new DirectoryWatcher(ftperConfig, "/test");
watcher.addObserver(watched);
watcher.hit(true);
```



## -> 同步csv文件

```java
Watched watched = new Watched(new Filter[] {
    new FileFiler("*0.csv", true), 
    new TagFilter("c:/test", false),
    new DownloadAction("c:/test")});

DirectoryWatcher watcher = new DirectoryWatcher(ftpConfig, "/test");
watcher.addObserver(watched);
watcher.hit(true);
```

备注：文件下载到 "c:/test" 目录，TagFilter 根据判断 "c:/test" 目录是否已经存在已下载的文件，如果存在则跳过下载。



## -> 其他

我们通过 “删除某目录下某文件”、“删除过期文件” 例子，看出虽然实现功能不同，但是只是变换了 Filter 和 Action。因此，我们可以通过变换 Filter 和 Action 来达到我们想要的功能。

内置 Filter ：

- DirectoryFiler（目录过滤器）
- FileFiler（文件名过滤器）
- FileModifyTimeFilter（文件最后修改时间过滤器）
- TagFilter（标签过滤器）

内置 Action ：

- DeleteAction（删除远程文件）
- MoveAction（迁移远程文件）
- DownloadAction（下载远程文件到本地）
- TagAction（在本地创建文件标签，配置 TagFilter 实现标签过滤）

比如第一次删除远程文件后，第二次将不再删除如何写呢？

```
Watched watched = new Watched(new Filter[] {
    new FileFiler("*0.csv", true), 
    new TagFilter("c:/test", false),
    new DeleteAction(),
    new TagAction("c:/test")});

DirectoryWatcher watcher = new DirectoryWatcher(ftpConfig, "/test");
watcher.addObserver(watched);
watcher.hit(true);
```



# 添砖加瓦

## -> 贡献代码的步骤

- 在 Github 上fork项目到自己的repo
- 把fork过去的项目也就是你的项目clone到你的本地
- 修改代码（记得一定要修改develop分支）
- commit后push到自己的库（develop分支）
- 登录Gitee或Github在你首页可以看到一个 pull request 按钮，点击它，填写一些说明信息，然后提交即可。
- 等待维护者合并



## -> 提交代码规范

- 注释完备

- 命名意思简洁明了
- 添加单元测试