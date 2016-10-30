<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
<log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/">
	<appender name="STDOUT" class="org.apache.log4j.ConsoleAppender">
		<layout class="org.apache.log4j.PatternLayout" />
	</appender>
	<appender name="INFO" class="org.apache.log4j.DailyRollingFileAppender">
		<param name="File" value="${logs.home}/${project}/runtime.log" />
		<param name="DatePattern" value="'.'yyyyMMdd" />
		<param name="Append" value="true" />
		<layout class="org.apache.log4j.PatternLayout">
			<param name="ConversionPattern" value="[%d{yyyy-MM-dd HH:mm:ss.SSS}] (%F:%L) %m %n" />
		</layout>
		<filter class="org.apache.log4j.varia.LevelRangeFilter">
			<param name="LevelMax" value="INFO" />
			<param name="LevelMin" value="INFO" />
		</filter>
	</appender>
	<appender name="DEBUG" class="org.apache.log4j.DailyRollingFileAppender">
		<param name="File" value="${logs.home}/${project}/debug.log" />
		<param name="DatePattern" value="'.'yyyyMMdd" />
		<param name="Append" value="true" />
		<layout class="org.apache.log4j.PatternLayout">
			<param name="ConversionPattern" value="[%d{yyyy-MM-dd HH:mm:ss.SSS}] %l %m %n" />
		</layout>
		<filter class="org.apache.log4j.varia.LevelRangeFilter">
			<param name="LevelMax" value="DEBUG" />
			<param name="LevelMin" value="DEBUG" />
		</filter>
	</appender>
	<appender name="ERROR" class="org.apache.log4j.DailyRollingFileAppender">
		<param name="File" value="${logs.home}/${project}/error.log" />
		<param name="DatePattern" value="'.'yyyyMMdd" />
		<param name="Append" value="true" />
		<layout class="org.apache.log4j.PatternLayout" >
			<param name="ConversionPattern" value="[%p][%d{yyyy-MM-dd HH:mm:ss.SSS}] %l %m %n" />
		</layout>
		<filter class="org.apache.log4j.varia.LevelRangeFilter">
			<param name="LevelMax" value="ERROR" />
			<param name="LevelMin" value="WARN" />
		</filter>
	</appender>
	<root>
	    <level value="debug"/>
		<appender-ref ref="STDOUT" />
		<appender-ref ref="ERROR" />
		<appender-ref ref="DEBUG" />
		<appender-ref ref="INFO" />
	</root>
</log4j:configuration>
