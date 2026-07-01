# Changelog
All notable changes to this project will be documented in this file.

## [Unreleased]

## [5.6.11] - 2026-07-01
### Changed
- 修改版本号
- 修改版本号
- Step 3: Upgrade Build Toolchain And Remove Direct Compile Blockers - Compile: SUCCESS

- Upgrade Maven wrapper and plugin versions for Java 21
- Fix invalid dependency coordinates and local Maven settings
- Remove direct internal JDK compile blockers in core/test code
- Compile verified with mvnw clean test-compile on Java 21
- Step 4: Harden Java 21 Compatibility And Update Metadata - Compile: SUCCESS

- Update README and Travis metadata to Java 21
- Refresh Java API javadoc link to Java 21 docs
- Re-verify reactor test-compile on Java 21
- Step 6: Final Validation - Compile: SUCCESS, Tests: passed

- Refresh vulnerable dependency versions compatible with Java 21
- Remove duplicate source attachment in hotwheel-affinity verify lifecycle
- Verify clean test and clean verify succeed on Java 21
- Residual CVEs remain in Spring 4.x line and require framework migration
- Step 1-4: Migrate Spring stack to 6.2.19 and Jakarta - Compile: SUCCESS, Tests: full suite passed

- Upgrade Spring BOM, Spring Data Redis, and MyBatis Spring for CVE remediation
- Replace javax servlet/validation APIs with jakarta imports in hotwheel-spring
- Verify clean test and clean verify succeed on Java 21
- Security: clear remaining direct dependency CVEs via Spring 6 migration
- Compatibility-first security hardening - Compile: SUCCESS

- Mask sensitive property values in EncryptablePropertyholder debug logs
- Add optional strict host key checking hooks to SFTPHelper
- Preserve existing runtime behavior by keeping secure checks opt-in
- Include current workspace launch configuration change in .vscode/launch.json
- Merge branch 'appmod/java-upgrade-20260701082335' into 6.0.x

## [5.6.10] - 2020-03-06
### Changed
- 增加traceId的key默认值
- fix javadoc
- fix traceid clean
- fix mdc
- update version 5.6.5
- add ssl
- fix update
- fix jackson
- update version 5.6.7
- fix version
- update version 5.6.9
- fix jackson

## [5.6.2] - 2019-03-24
### Changed
- fix
- add maven
- add travis and codecov
- fix status
- fix maven version
- change jdk version
- fix asm
- update version
- fix gpg
- fix mavem deploy
- fix sonatype deploy

## [5.6.1] - 2019-02-27
### Changed
- upgrade version 5.5.4
- update version 5.6.0
- fix pom
- fix pom
- fix

## [5.5.4] - 2019-02-19
### Changed
- 5.5.3: 增加release属性文件
- 5.5.3: 增加release属性文件
- 5.5.3: 增加release属性文件
- 5.5.3: 增加release属性文件
- 5.5.3: 增加release属性文件
- 5.5.3-R0: 增加release属性文件
- 5.5.3-R2: 修复javadoc验证
- 预备新版本5.6.0
- fix jackson
- add developer
- fix fileupload
- fix fileupload

## [5.5.3] - 2018-05-02
### Changed
- 5.5.2: 增加release属性文件
- 5.5.2: 增加release属性文件
- 5.5.3: 增加release属性文件
- 5.5.3: 增加release属性文件

## [5.5.2] - 2018-05-02
### Changed
- 5.5.1: 测试dubbo参数
- 5.5.1: 调整maven仓库配置信息
- 5.5.1: 调整maven仓库配置信息
- 5.5.2: 设置snapshot版本
- 5.5.2: 修订部分javadoc的描述内容
- 5.5.2: 升级版本号到5.5.2
- 5.5.2: 升级版本号到5.5.2

## [5.5.1] - 2018-04-24
### Changed

## [5.5.3-release] - 2018-05-05
### Changed

## [5.5.3-R2] - 2018-05-05
### Changed
- 新建5.5.1版本
- 5.5.1: 删除不必要的模块
- 5.5.1: 增加cpu亲和性
- 5.5.1: 预备给线程池做cpu亲和性
- 5.5.1: 去掉gpg签名
- 5.5.1: 统一版本号
- 5.5.1: 规范代码
- 5.5.1: 删除all模块
- 5.5.1: 发布正式版本
- 5.5.1: 测试dubbo参数
- 5.5.1: 调整maven仓库配置信息
- 5.5.1: 调整maven仓库配置信息
- 5.5.2: 设置snapshot版本
- 5.5.2: 修订部分javadoc的描述内容
- 5.5.2: 升级版本号到5.5.2
- 5.5.2: 升级版本号到5.5.2
- 5.5.2: 增加release属性文件
- 5.5.2: 增加release属性文件
- 5.5.3: 增加release属性文件
- 5.5.3: 增加release属性文件
- 5.5.3: 增加release属性文件
- 5.5.3: 增加release属性文件
- 5.5.3: 增加release属性文件
- 5.5.3: 增加release属性文件
- 5.5.3: 增加release属性文件
- 5.5.3-R0: 增加release属性文件
- 5.5.3-R2: 修复javadoc验证

## [5.5.0] - 2018-04-19
### Changed

## [5.5.2-2] - 2018-05-02
### Changed
- 5.5.2: 增加release属性文件

## [5.5.2-1] - 2018-05-02
### Changed

## [5.5.3-R1] - 2018-05-05
### Changed
- 新版本5.5.0, 简化优化类库
- 5.5.0: 调整包路径
- 5.5.0: 调整包路径
- 5.5.0: 调整URL短链接类名及方法
- 新建5.5.1版本
- 5.5.1: 删除不必要的模块
- 5.5.1: 增加cpu亲和性
- 5.5.1: 预备给线程池做cpu亲和性
- 5.5.1: 去掉gpg签名
- 5.5.1: 统一版本号
- 5.5.1: 规范代码
- 5.5.1: 删除all模块
- 5.5.1: 发布正式版本
- 5.5.1: 测试dubbo参数
- 5.5.1: 调整maven仓库配置信息
- 5.5.1: 调整maven仓库配置信息
- 5.5.2: 设置snapshot版本
- 5.5.2: 修订部分javadoc的描述内容
- 5.5.2: 升级版本号到5.5.2
- 5.5.2: 升级版本号到5.5.2
- 5.5.2: 增加release属性文件
- 5.5.2: 增加release属性文件
- 5.5.3: 增加release属性文件
- 5.5.3: 增加release属性文件
- 5.5.3: 增加release属性文件
- 5.5.3: 增加release属性文件
- 5.5.3: 增加release属性文件
- 5.5.3: 增加release属性文件
- 5.5.3: 增加release属性文件
- 5.5.3-R0: 增加release属性文件

## [5.4.0] - 2018-04-10
### Changed
- 重构包结构, 版本号从5.4.0开始
- 增加日志拦截器
- 增加日志拦截器
- httpclient增加对Connection的检测
- 确定版本号5.4.0

## [5.3.25] - 2018-04-04
### Changed
- update: 升级版本号到5.3.22
- update: 升级版本号到5.3.22快照
- fix: 修复pom互相依赖的问题
- fix: 修复pom互相依赖的问题
- fix: 删除多余的引入
- update: 升级版本号5.3.23
- update: 升级版本号5.3.25

## [5.3.21] - 2018-03-20
### Changed
- update: 升级版本号到5.3.17, 增加resource变量
- update: 升级版本号到5.3.18, 增加resource变量
- update: 升级版本号到5.3.19, 增加resource变量
- update: 升级版本号到5.3.20, 增加resource变量
- update: 升级版本号到5.3.21

## [5.3.16] - 2018-01-15
### Changed
- fix: 增加备注
- update: 升级版本号到5.3.16

## [5.3.15] - 2018-01-13
### Changed
- update: 升级版本号到5.3.15, RedisApi开放public构造方法

## [5.3.14] - 2018-01-13
### Changed
- update: 升级版本号到5.3.14, 删除属性文件的加载，原方式不安全

## [5.3.13] - 2018-01-13
### Changed
- update: 升级版本号到5.3.8, 升级fastjson版本到1.2.43
- update: 升级版本号到5.3.9, md5计算拆分2个方法, 默认是utf-8编码, 另一种指定字符集
- update: 升级版本号到5.3.10, 恢复fastjson版本号1.2.33, 新版本有bug, 会将部分字符集按照unicode编码输出 https://github.com/alibaba/fastjson/issues/690
- update: 升级版本号到5.3.13

## [5.3.7] - 2017-12-01
### Changed
- add: 增加${value}正则替换的测试
- update: 升级版本号到5.3.3, 增加springMVC关闭检测
- update: 升级版本号到5.3.5, springC关闭输出日志
- update: 升级版本号到5.3.6, springC关闭输出日志
- update: 升级版本号到5.3.7, springC关闭输出日志

## [5.3.2] - 2017-11-20
### Changed
- create: 开启5.2.0+版本的优化计划
- fix: 修改maven仓库名称, 不改变id
- update: 升级druid版本到1.1.3
- update: 升级tomcat-jdbc版本到7.0.81
- 增加web日志监控输出
- 调整日志记录方式
- 调整日志记录方式
- 调整日志记录方式, 增加客户端IP地址
- 调整日志记录方式, 增加客户端IP地址
- 调整trace拦截器的包路径
- 升级版本号到5.2.3
- 组件升级版本号到5.2.3
- 组件升级版本号到5.2.5, 优化SimpleDateFormat
- 组件升级版本号到5.2.6, 如果没有tradeId, 就创建一个放入MDC中
- 组件升级版本号到5.2.7, 如果没有tradeId, 就创建一个放入MDC中, 并放入response的header中
- 组件升级版本号到5.2.8, tradeId放入response的header中的操作放在postHandle执行
- 组件升级版本号到5.2.9, tradeId放入response的header中的操作放在preHandle执行
- 组件升级版本号到5.2.10, 调整tradeId格式
- 组件升级版本号到5.2.11, 调整tradeId重置方法
- 组件升级版本号到5.2.12, 调整tradeId重置方法中时间戳的顺序
- 组件升级版本号到5.2.13, 重新制作traceId的计算方法, 避开线程同步的问题
- 组件升级版本号到5.2.15, 将pid和threadId也放入ThreadLocal中
- 组件升级版本号到5.2.16, 将pid和threadId也放入ThreadLocal中
- 组件升级版本号到5.2.17, 将pid和threadId也放入ThreadLocal中
- 组件升级版本号到5.2.18, 修复trace码获取错误的bug
- 组件升级版本号到5.2.19, 增加http跟踪码的生成规则及原因, 日志调整用配置文件中的来输出
- 组件升级版本号到5.2.20, 异常情况详细信息
- 调整部分代码
- 修复部分缺少大括号的代码
- update: 升级版本号到5.3.0, 对ASIO的异常情况进行细化
- update: 升级版本号到5.3.1, 增加参数别名及大小写忽略
- create: 新建5.3.2分支, 优化chunked编码流的接收情况
- update: 升级am版本号到5.2
- fix: 异常日志输出的bug
- update: 版本号5.3.2

## [5.1.7] - 2017-09-15
### Changed
- fix: 回调方法输出异常
- add: 增加额外增加任务的处理机制

## [5.1.6] - 2017-09-13
### Changed
- update: 升级版本号5.0.10, httpclient增加超时判断
- fix: ModelAndView增加命名
- fix: 解析日期增加Date类型默认输出的string
- add: 增加字段注解类
- create: 创建5.1.x分支, 优化异常处理机制
- create: 创建5.1.1分支, 修正时区
- update: 验证测试代码
- update: 验证测试代码
- add: 增加mybatis结果集映射字段
- update: 升级版本号5.1.2
- update: 升级版本号5.1.3, 非基础数据类型在null时直接返回
- update: 升级版本号5.1.5, 非基础数据类型在null时直接返回
- update: 升级版本号5.1.6, ASIO debug日志判断level

## [5.0.9] - 2017-06-21
### Changed
- update: 升级版本号5.0.8
- update: 升级版本号5.0.9

## [5.0.8] - 2017-06-21
### Changed
- 新疆3.1.x 分支进一步优化线程池
- 新疆3.1.x 分支进一步优化线程池
- 扩展线程池方法
- 扩展线程池方法
- 扩展线程池方法
- 扩展线程池方法
- 扩展线程池方法
- 扩展线程池方法
- 扩展线程池方法
- 扩展线程池方法
- 扩展线程池方法, 暴露任务名称
- 扩展线程池方法, 暴露任务名称
- 扩展线程池方法, 暴露任务名称
- add: cluster
- upgrade: pom
- upgrade: 3.1.1
- upgrade: 3.1.1
- upgrade: 3.1.2
- upgrade: 3.1.3
- upgrade: 3.1.3
- update: 规范pom文件
- add: 全局捕获异常文件
- add: spring框架
- add: spring框架
- add: spring框架
- add: http utils
- fix: json parase
- fix: json parase
- fix: json parase
- fix: json parase
- fix: json parase
- fix: json parase
- fix: json parase
- fix: json parase
- fix: json parase
- add: 增加spring 全局异常捕获
- add: 增加spring 全局异常捕获
- delete: 删除无用的测试代码
- fix: 数字判断, 如果字符串未空, 也是匹配失败
- fix: 判断定时任务运行周期
- fix: 判断定时任务运行周期
- fix: 判断定时任务运行周期
- fix: 判断定时任务运行周期
- upgrade: 3.2.1
- upgrade: 3.0.2
- upgrade: 3.0.2
- upgrade: 3.0.2
- upgrade: 3.0.2
- 修复一处json反射的错误
- update: 3.2.2
- upgrade: 3.2.1
- upgrade: 3.2.1
- upgrade: 3.0.2
- upgrade: 3.0.2
- upgrade: 3.0.3, 更新log4j版本号到2.8.2
- upgrade: 3.0.3, 更新log4j版本号到2.8.2
- upgrade: 3.0.3, 更新log4j版本号到2.8.2
- upgrade: 3.0.5, 更新log4j版本号到2.8.2
- upgrade: 3.0.6, 更新log4j版本号到2.8.2
- upgrade: 3.0.6, 更新log4j版本号到2.8.2
- upgrade: 3.0.6, 更新log4j版本号到2.8.2
- upgrade: 3.0.6, 更新log4j版本号到2.8.2
- 修复一处json反射的错误
- create JDB version
- 完善全局异常信息
- 完善全局异常信息
- 完善全局异常信息
- 完善全局异常信息
- 完善全局异常信息
- 构建5.0.x组件
- 构建5.0.x组件
- 构建5.0.x组件
- 调整maven配置, 增加验证测试代码
- 优化日期类字符串的解析
- add: 验证框架
- add: 验证框架
- add: 正则表达式说明
- add: 正则表达式说明
- fix: 修改仓库地址
- fix: 修改仓库地址
- fix: 修改仓库地址
- fix: 修改仓库地址
- update: 升级版本号5.0.8
- update: 升级版本号5.0.8
- update: 升级版本号5.0.8
- update: 升级版本号5.0.8
- update: 升级版本号5.0.8

## [3.0.1] - 2017-01-18
### Changed
- json输出字段串，默认字段名大小写
- json输出字段串，默认字段名大小写
- 设定版本号3.0.1
- 设定版本号3.0.1
- 增加ClassLoader特征
- 增加ClassLoader特征
- 增加ClassLoader特征
- 增加ClassLoader特征
- add: test
- add: cron 时间控制, from spring-context
- fix: deploy 忽略test模块

## [3.0.0] - 2017-01-11
### Changed
- 设定版本号2.1.9, 增加对war MEAT-INF信息的读取
- 设定版本号2.1.9, 增加对war MEAT-INF信息的读取
- 设定版本号2.1.9, 增加对war MEAT-INF信息的读取
- 设定版本号2.1.9, 增加对war MEAT-INF信息的读取
- 调整包路径
- 修整注解
- add spring
- 删除测试代码
- 修订信息
- 修订信息
- 修订信息
- 修订信息
- 修订信息
- add: 浮点计算
- json输出字段串，默认字段名大小写

## [2.1.9] - 2017-01-01
### Changed
- 设定版本号2.1.9, 增加对war MEAT-INF信息的读取
- 设定版本号2.1.9, 增加对war MEAT-INF信息的读取
- 设定版本号2.1.9, 增加对war MEAT-INF信息的读取
- 设定版本号2.1.9, 增加对war MEAT-INF信息的读取

## [2.1.8] - 2016-12-30
### Changed
- 设定版本号2.1.7
- 设定版本号2.1.7
- 设定版本号2.1.8, 增加对war MEAT-INF信息的读取

## [2.1.7] - 2016-12-21
### Changed
- 测试BitSet类的用法
- 优化SocketChannel的关闭处理，减少TIME_WAIT的状态滞留时间
- 启用SO_LINGER选项，设置超时为0秒，即立即关闭socket，并丢弃所有数据
- 收敛socket操作到asio

## [2.1.6] - 2016-12-15
### Changed
- 规范代码
- 规范代码, httpclient增加关闭输入流
- 设定版本号2.1.5
- fix: 解决字段不包含在class中解析被打断的bug
- 设定版本号2.1.6

## [2.1.4] - 2016-12-10
### Changed
- 优化Xml解析
- 优化xml解析
- 优化xml解析
- 设定版本号2.1.4

## [2.1.3] - 2016-12-10
### Changed
- 修复非chunked编码获取body长度不准确的bug
- 设定版本号2.1.2
- 优化XML解析, 支持反射bean时可以用别名及大小写忽略
- fix解析http chunked在非行数据时没有累加已读取的字节数

## [2.1.1] - 2016-12-05
### Changed
- 修复域名不可用时的异常处理
- 设定版本号2.1.1

## [2.1.0] - 2016-12-05
### Changed
- 设定版本号2.0.40
- 设定版本号2.0.40
- 优化ASIO组件缓存
- 优化ASIO组件缓存
- 统一编码

## [2.0.40] - 2016-12-04
### Changed
- 设定版本号2.0.39
- 设定版本号2.0.40

## [2.0.39] - 2016-12-03
### Changed

## [2.0.38] - 2016-12-03
### Changed
- 引进apache异步组件做对比
- 引进apache异步组件做对比
- 引进apache异步组件做对比
- 引进apache异步组件做对比
- 引进apache异步组件做对比
- 设定版本号2.0.38

## [2.0.37] - 2016-11-30
### Changed
- 设定版本号2.0.33
- 设定版本号2.0.33
- 设定版本号2.0.33
- 优化nio
- 设定版本号2.0.34
- 设定版本号2.0.34
- 设定版本号2.0.35, 修正计数器在异常情况的累加不正确的问题，原因暂时不明，但是有加保护错误，防止请求溢出或不正确的引用
- 引入mina IoBuffer增强ByteBuffer不支持可变长度的问题
- 打开未知的异常情况
- 设定版本号2.0.36
- 设定版本号2.0.36，修订事件的判断方式，不明觉厉，代码来自mina
- server 端不支持绑定对象对JDNI上时的错误log改为警告信息
- 设定版本号2.0.37

## [2.0.31] - 2016-11-29
### Changed
- 修复json特殊字符输出的问题
- 修复list数组下标重复bug
- 设定版本号2.0.31

## [2.0.30] - 2016-11-27
### Changed
- 修复多war部署时dsmp抢占先启动，可能会影响其它应用的问题
- Api工具类增加对字段是否泛型或模板的判断方法
- Api工具类增加对字段是否泛型或模板的判断方法
- 增加测试判断泛型字段的单元测试
- 调整部分类名，避免和工作空间类名冲突
- Nio日志全部以debug输出，设定版本号2.0.27
- Nio优化内存控制，设定版本号2.0.28
- Nio优化内存控制，设定版本号2.0.29
- Nio优化内存控制，设定版本号2.0.30

## [2.0.25] - 2016-11-23
### Changed
- 设定版本号2.0.23, 修订json格式化的bug
- 设定版本号2.0.25, 修订json格式化的bug

## [2.0.23] - 2016-11-23
### Changed
- 设定版本号2.0.21, 修订方法判断的bug
- 设定版本号2.0.22, 修订方法判断的bug
- 设定版本号2.0.23, 修订json格式化的bug

## [2.0.21] - 2016-11-22
### Changed
- 完善NioHttpClient的回调方法
- 设定版本号2.0.21

## [2.0.20] - 2016-11-21
### Changed
- add ForkJoinPool的封装
- fix Json解析中对数组的支持
- test Json解析中对数组的支持
- 设定版本号2.0.20

## [2.0.19] - 2016-11-20
### Changed
- 设定版本2.0.17
- 设定版本2.0.17
- 设定版本2.0.18
- RequestMapping增加http方法的映射
- RequestMapping增加http方法的映射
- RequestMapping增加http方法的映射
- 测试模块不上传
- 修订Nio部分代码
- 设定版本号2.0.19
- add ForkJoinPool的封装
- add ForkJoinPool的封装
- add ForkJoinPool的封装

## [2.0.17] - 2016-11-17
### Changed
- 设定版本2.0.16
- Merge branch 'new-ioc' into 2.0.x
- 设定版本2.0.16
- 设定版本2.0.17

## [2.0.16] - 2016-11-15
### Changed
- 设定版本2.0.16

## [2.0.15] - 2016-11-15
### Changed
- 增加日志记录点
- 增加日志记录点
- 设定版本2.0.14
- 设定版本2.0.15

## [2.0.13] - 2016-11-14
### Changed
- Merge branch '2.0.x'
- 整理类库
- 收敛日志组件
- 调整类库目录结构
- 调整类库目录结构
- 调整类库目录结构
- 调整类库目录结构
- 调整类库目录结构
- add module daemon
- add module daemon
- add module daemon
- add module daemon
- add module ForkJoinPool
- add module ForkJoinPool
- add module ForkJoinPool
- 修改定时任务运行状态重置点
- 整理类库
- Merge branch '2.0.x'
- add IoC
- fix 状态初始失败的问题
- fix 状态初始失败的问题
- 屏蔽调试的日志点
- 调整运行状态的重置点

## [2.0.10] - 2016-11-11
### Changed
- 修改logiml可以配置
- 调整JNDI用法
- 调整JNDI用法

## [2.0.9] - 2016-11-10
### Changed
- add task
- 定时任务增加判断是否运行，条件开始时间大于结束时间

## [2.0.8] - 2016-11-10
### Changed
- add CONTAINER_PREFIX 常量
- add task
- add task
- Merge branch '2.0.x'

## [2.0.7] - 2016-11-09
### Changed
- version 2.0.7, change logger into to error
- version 2.0.6, add webapps
- add bean factory
- add JNDI
- add JNDI
- version 2.0.6
- Merge branch 'v2.0.3'
- Merge branch 'v2.0.6'
- Merge branch 'v2.0.7'

## [2.0.2] - 2016-11-07
### Changed
- add web test
- add web test
- add web test
- mybatis支持多个数据源
- mybatis支持多个数据源
- 初始化HotWheel项目
- 初始化HotWheel项目
- add: 增加反射效率测试
- add: 基础组件
- add: 异步非阻塞http客户端组件
- 调整maven pom文件
- add: 同步阻塞http客户端
- add: tomcat jdbc poll, 去除juli日志组件, 改slf4j
- add: README
- add: README
- add: SQLApi
- 规范pom文件
- add support mybatis
- add support mybatis
- add web test
- Merge branch 'v2.0.1'
- Merge branch 'v2.0.2'


[Unreleased]: https://gitee.com/quant1x/hotwheel.git/compare/v5.6.11...HEAD
[5.6.11]: https://gitee.com/quant1x/hotwheel.git/compare/v5.6.10...v5.6.11
[5.6.10]: https://gitee.com/quant1x/hotwheel.git/compare/v5.6.2...v5.6.10
[5.6.2]: https://gitee.com/quant1x/hotwheel.git/compare/v5.6.1...v5.6.2
[5.6.1]: https://gitee.com/quant1x/hotwheel.git/compare/v5.5.4...v5.6.1
[5.5.4]: https://gitee.com/quant1x/hotwheel.git/compare/v5.5.3...v5.5.4
[5.5.3]: https://gitee.com/quant1x/hotwheel.git/compare/v5.5.2...v5.5.3
[5.5.2]: https://gitee.com/quant1x/hotwheel.git/compare/v5.5.1...v5.5.2
[5.5.1]: https://gitee.com/quant1x/hotwheel.git/compare/v5.5.3-release...v5.5.1
[5.5.3-release]: https://gitee.com/quant1x/hotwheel.git/compare/v5.5.3-R2...v5.5.3-release
[5.5.3-R2]: https://gitee.com/quant1x/hotwheel.git/compare/v5.5.0...v5.5.3-R2
[5.5.0]: https://gitee.com/quant1x/hotwheel.git/compare/v5.5.2-2...v5.5.0
[5.5.2-2]: https://gitee.com/quant1x/hotwheel.git/compare/v5.5.2-1...v5.5.2-2
[5.5.2-1]: https://gitee.com/quant1x/hotwheel.git/compare/v5.5.3-R1...v5.5.2-1
[5.5.3-R1]: https://gitee.com/quant1x/hotwheel.git/compare/v5.4.0...v5.5.3-R1
[5.4.0]: https://gitee.com/quant1x/hotwheel.git/compare/v5.3.25...v5.4.0
[5.3.25]: https://gitee.com/quant1x/hotwheel.git/compare/v5.3.21...v5.3.25
[5.3.21]: https://gitee.com/quant1x/hotwheel.git/compare/v5.3.16...v5.3.21
[5.3.16]: https://gitee.com/quant1x/hotwheel.git/compare/v5.3.15...v5.3.16
[5.3.15]: https://gitee.com/quant1x/hotwheel.git/compare/v5.3.14...v5.3.15
[5.3.14]: https://gitee.com/quant1x/hotwheel.git/compare/v5.3.13...v5.3.14
[5.3.13]: https://gitee.com/quant1x/hotwheel.git/compare/v5.3.7...v5.3.13
[5.3.7]: https://gitee.com/quant1x/hotwheel.git/compare/v5.3.2...v5.3.7
[5.3.2]: https://gitee.com/quant1x/hotwheel.git/compare/v5.1.7...v5.3.2
[5.1.7]: https://gitee.com/quant1x/hotwheel.git/compare/v5.1.6...v5.1.7
[5.1.6]: https://gitee.com/quant1x/hotwheel.git/compare/v5.0.9...v5.1.6
[5.0.9]: https://gitee.com/quant1x/hotwheel.git/compare/v5.0.8...v5.0.9
[5.0.8]: https://gitee.com/quant1x/hotwheel.git/compare/v3.0.1...v5.0.8
[3.0.1]: https://gitee.com/quant1x/hotwheel.git/compare/v3.0.0...v3.0.1
[3.0.0]: https://gitee.com/quant1x/hotwheel.git/compare/v2.1.9...v3.0.0
[2.1.9]: https://gitee.com/quant1x/hotwheel.git/compare/v2.1.8...v2.1.9
[2.1.8]: https://gitee.com/quant1x/hotwheel.git/compare/v2.1.7...v2.1.8
[2.1.7]: https://gitee.com/quant1x/hotwheel.git/compare/v2.1.6...v2.1.7
[2.1.6]: https://gitee.com/quant1x/hotwheel.git/compare/v2.1.4...v2.1.6
[2.1.4]: https://gitee.com/quant1x/hotwheel.git/compare/v2.1.3...v2.1.4
[2.1.3]: https://gitee.com/quant1x/hotwheel.git/compare/v2.1.1...v2.1.3
[2.1.1]: https://gitee.com/quant1x/hotwheel.git/compare/v2.1.0...v2.1.1
[2.1.0]: https://gitee.com/quant1x/hotwheel.git/compare/v2.0.40...v2.1.0
[2.0.40]: https://gitee.com/quant1x/hotwheel.git/compare/v2.0.39...v2.0.40
[2.0.39]: https://gitee.com/quant1x/hotwheel.git/compare/v2.0.38...v2.0.39
[2.0.38]: https://gitee.com/quant1x/hotwheel.git/compare/v2.0.37...v2.0.38
[2.0.37]: https://gitee.com/quant1x/hotwheel.git/compare/v2.0.31...v2.0.37
[2.0.31]: https://gitee.com/quant1x/hotwheel.git/compare/v2.0.30...v2.0.31
[2.0.30]: https://gitee.com/quant1x/hotwheel.git/compare/v2.0.25...v2.0.30
[2.0.25]: https://gitee.com/quant1x/hotwheel.git/compare/v2.0.23...v2.0.25
[2.0.23]: https://gitee.com/quant1x/hotwheel.git/compare/v2.0.21...v2.0.23
[2.0.21]: https://gitee.com/quant1x/hotwheel.git/compare/v2.0.20...v2.0.21
[2.0.20]: https://gitee.com/quant1x/hotwheel.git/compare/v2.0.19...v2.0.20
[2.0.19]: https://gitee.com/quant1x/hotwheel.git/compare/v2.0.17...v2.0.19
[2.0.17]: https://gitee.com/quant1x/hotwheel.git/compare/v2.0.16...v2.0.17
[2.0.16]: https://gitee.com/quant1x/hotwheel.git/compare/v2.0.15...v2.0.16
[2.0.15]: https://gitee.com/quant1x/hotwheel.git/compare/v2.0.13...v2.0.15
[2.0.13]: https://gitee.com/quant1x/hotwheel.git/compare/v2.0.10...v2.0.13
[2.0.10]: https://gitee.com/quant1x/hotwheel.git/compare/v2.0.9...v2.0.10
[2.0.9]: https://gitee.com/quant1x/hotwheel.git/compare/v2.0.8...v2.0.9
[2.0.8]: https://gitee.com/quant1x/hotwheel.git/compare/v2.0.7...v2.0.8
[2.0.7]: https://gitee.com/quant1x/hotwheel.git/compare/v2.0.2...v2.0.7

[2.0.2]: https://gitee.com/quant1x/hotwheel.git/releases/tag/v2.0.2
