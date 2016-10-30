<?xml version="1.0" encoding="UTF-8"?>
<!-- status=debug 可以查看log4j的装配过程 -->
<Configuration status="debug" monitorInterval="1800">
<Properties>
        <!-- 配置日志文件输出目录 -->
        <Property name="LOG_HOME">/data/runtime/logs</Property>
    </Properties>
    <Appenders>
        <!-- 设置日志格式并配置日志压缩格式(runtime.log.年份.gz) -->
        <RollingRandomAccessFile name="runtime"
                                 immediateFlush="false" fileName="${logs.home}/${project}/runtime.log"
                                 filePattern="${logs.home}/${project}/runtime.log.%d{yyyy-MM-dd}.gz">
            <Filters>
                <!-- Now deny warn, error and fatal messages -->
                <ThresholdFilter level="warn"  onMatch="DENY"   onMismatch="NEUTRAL"/>
                <ThresholdFilter level="error" onMatch="DENY"   onMismatch="NEUTRAL"/>
                <ThresholdFilter level="fatal" onMatch="DENY"   onMismatch="NEUTRAL"/>
                <!-- This filter accepts info, warn, error, fatal and denies debug/trace -->
                <ThresholdFilter level="info"  onMatch="ACCEPT" onMismatch="DENY"/>
            </Filters>
            <!--
                %d{yyyy-MM-dd HH:mm:ss, SSS} : 日志生产时间
                %p : 日志输出格式
                %c : logger的名称
                %m : 日志内容，即 logger.info("message")
                %n : 换行符
                %C : Java类名
                %L : 日志输出所在行数
                %M : 日志输出所在方法名
                hostName : 本地机器名
                hostAddress : 本地ip地址
            -->
            <PatternLayout>
                <pattern>[%d{yyyy-MM-dd HH:mm:ss.SSS}] (%F.%M:%L) %m %n</pattern>
            </PatternLayout>
            <Policies>
                <TimeBasedTriggeringPolicy interval="1" modulate="true" />
            </Policies>
        </RollingRandomAccessFile>

        <!-- ERROR日志格式 -->
        <RollingRandomAccessFile name="error"
                                 immediateFlush="false" fileName="${logs.home}/${project}/error.log"
                                 filePattern="${logs.home}/${project}/error.log.%d{yyyy-MM-dd}.gz">
            <Filters>
                <ThresholdFilter level="WARN" onMatch="ACCEPT" />
            </Filters>
            <PatternLayout>
                <pattern>[%p][%d{yyyy-MM-dd HH:mm:ss.SSS}] %l %msg%xEx %n</pattern>
            </PatternLayout>
            <Policies>
                <TimeBasedTriggeringPolicy interval="1" modulate="true" />
            </Policies>
        </RollingRandomAccessFile>

        <!-- DEBUG日志格式 -->
        <RollingRandomAccessFile name="debug"
                                 immediateFlush="false" fileName="${logs.home}/${project}/debug.log"
                                 filePattern="${logs.home}/${project}/debug.log.%d{yyyy-MM-dd}.gz">
            <Filters>
                <ThresholdFilter level="info" onMatch="DENY" onMismatch="NEUTRAL"/>
                <ThresholdFilter level="debug" onMatch="ACCEPT" onMismatch="DENY"/>
            </Filters>
            <PatternLayout>
                <pattern>[%p][%d{yyyy-MM-dd HH:mm:ss.SSS}] %l %msg%xEx%n</pattern>
            </PatternLayout>
            <Policies>
                <TimeBasedTriggeringPolicy interval="1" modulate="true" />
            </Policies>
        </RollingRandomAccessFile>
    </Appenders>

    <Loggers>
        <!-- 配置日志的根节点 -->
        <asyncRoot level="debug" includeLocation="true">
            <AppenderRef ref="runtime"/>
            <AppenderRef ref="error"/>
            <AppenderRef ref="debug"/>
        </asyncRoot>
    </Loggers>
</Configuration>