# 使用说明

## 前置准备条件：<br>
#### 1.有mysql,kafka中间件  <br>
#### 2.配置mysql:  <br>
  1>  配置my.cnf  <br>
 ```
[mysqld]    
 log-bin=mysql-bin # 开启 binlog   
 binlog-format=ROW # 选择 ROW 模式   
 server_id=1 # 配置 MySQL replaction 需要定义，不要和 canal 的 slaveId 重复   
```

  2> set global binlog_rows_query_log_events = 'on';    <br>
  
  3>  执行sql :  在项目里的sql目录下的sql.txt文件内容     <br>
  
 #### 3.配置kafka   <br>
   创建topic :user_info,common_req_trace_log,common_canal_crm_audit    <br>
   user_info : 用于web端修改用户时走mq消息修改方式   <br>
   common_req_trace_log :用于web入口端的数据监控traceId,入参,项目,类,方法等   <br>
   common_canal_crm_audit :用于监控mysql的数据变更，最后和web端入口数据进行关联起来  <br>

 #### 4.canal （大部分操作copy canal文档）<br>
   1> 下载编译（或者直接下载安装包） wget https://github.com/waterlang/canal-data-sql/releases/download/v.1.1.4/canal.deployer-1.1.4.tar.gz  <br>
   
   2>   解压缩  <br>
   ```
    mkdir /tmp/canal   
    tar zxvf canal.deployer-$version.tar.gz  -C /tmp/canal   
   ``` 
   解压完成后，进入 /tmp/canal 目录，可以看到如下结构 
   ```$xslt
drwxr-xr-x 2 jianghang jianghang  136 2013-02-05 21:51 bin
drwxr-xr-x 4 jianghang jianghang  160 2013-02-05 21:51 conf
drwxr-xr-x 2 jianghang jianghang 1.3K 2013-02-05 21:51 lib
drwxr-xr-x 2 jianghang jianghang   48 2013-02-05 21:29 logs
```
   3> 配置修改 <Br>
   a.修改instance.properties  <br>
   ```$xslt
vi conf/example/instance.properties
```
  你只需要修改canal.instance.master.address,  canal.instance.dbUsername,  canal.instance.dbPassword这几个参数  
```$xslt
#################################################
## mysql serverId , v1.0.26+ will autoGen
# canal.instance.mysql.slaveId=0

# enable gtid use true/false
canal.instance.gtidon=false

# position info
canal.instance.master.address=192.168.16.248:3308
#canal.instance.master.address=
canal.instance.master.journal.name=
canal.instance.master.position=
canal.instance.master.timestamp=
canal.instance.master.gtid=

# rds oss binlog
canal.instance.rds.accesskey=
canal.instance.rds.secretkey=
canal.instance.rds.instanceId=

# table meta tsdb info
canal.instance.tsdb.enable=true
#canal.instance.tsdb.url=jdbc:mysql://127.0.0.1:3306/canal_tsdb
#canal.instance.tsdb.dbUsername=canal
#canal.instance.tsdb.dbPassword=canal

#canal.instance.standby.address =
#canal.instance.standby.journal.name =
#canal.instance.standby.position =
#canal.instance.standby.timestamp =
#canal.instance.standby.gtid=

# username/password
#canal.instance.dbUsername=root
#canal.instance.dbPassword=
canal.instance.dbUsername=canal
canal.instance.dbPassword=canal
canal.instance.connectionCharset = UTF-8
# enable druid Decrypt database password
canal.instance.enableDruid=false
#canal.instance.pwdPublicKey=MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBALK4BUxdDltRRE5/zXpVEVPUgunvscYFtEip3pmLlhrWpacX7y7GCMo2/JM6LeHmiiNdH1FWgGCpUfircSwlWKUCAwEAAQ==

# table regex
canal.instance.filter.regex=crm.crmchance20180226
# table black regex
canal.instance.filter.black.regex=
# table field filter(format: schema1.tableName1:field1/field2,schema2.tableName2:field1/field2)
#canal.instance.filter.field=test1.t_product:id/subject/keywords,test2.t_company:id/name/contact/ch
# table field black filter(format: schema1.tableName1:field1/field2,schema2.tableName2:field1/field2)
#canal.instance.filter.black.field=test1.t_product:subject/product_image,test2.t_company:id/name/contact/ch

# mq config
canal.mq.topic=common_canal_crm_audit
#canal.mq.topic=audit_test
# dynamic topic route by schema or table regex
#canal.mq.dynamicTopic=mytest1.user,mytest2\\..*,.*\\..*
canal.mq.partition=0
# hash partition config
#canal.mq.partitionsNum=3
#canal.mq.partitionHash=test.table:id^name,.*\\..*
#################################################

```

 b.修改canal.properties <br>  
    你只需要修改canal.mq.servers的地址 <BR>
 ```$xslt
vi conf/canal.properties
```
```$xslt
# tcp, kafka, RocketMQ
canal.serverMode = kafka

##################################################
######### 		     MQ 		     #############
##################################################
canal.mq.servers = 192.168.16.248:9092
canal.mq.retries = 0
canal.mq.batchSize = 16384
canal.mq.maxRequestSize = 1048576
canal.mq.lingerMs = 100
canal.mq.bufferMemory = 33554432
canal.mq.canalBatchSize = 50
canal.mq.canalGetTimeout = 100
canal.mq.flatMessage = true
canal.mq.compressionType = none
canal.mq.acks = all
#canal.mq.properties. =
```

 4> 启动： <Br>
```$xslt
 sh bin/startup.sh

```
  5> 查看启动是否成功：
```$xslt
    tail -f -n 400 logs/canal/canal.log  //查看的canal的日志
    tail -f -n 400  logs/example/example.log  //查看的instance的日志
```   
  
# 项目启动

## 项目描述
audit-web-api : 主要是项目对外（浏览器等）暴露的api端，他调用的是audit-service服务 <br>

audit-service : 主要为上层api提供接口，数据保存等由他来做  <br>

audit-consumer : 收集web-api 入口数据的解析及保存及audit-service里修改后的数据变更。这两个最
终的数据是通过traceId进行关联，这样就知道每次数据的变更是从哪个入口修改的   <br>

项目里的代码比较简单就不具体介绍了。  <br>

## 项目启动
 先启动audit-web-api项目 ，再启动audit-service,最后启动audit-consumer <br>

  然后在postman里执行：
  ```$xslt
    method :put 
      url : http://127.0.0.1:8888/user/
      content-type :application/json
      content : 
      {
        "id":14,
        "name": "zhangsan222",
        "address": "addreds"
      }
      
```
 执行成功后会收到success返回值，这时可以去数据库看一下数据是否写入  <br>
  
 
 