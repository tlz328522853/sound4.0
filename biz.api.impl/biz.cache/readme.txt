************************************************
**********************概述***********************
这是一个 使用dubbo+cache，实现远程接口缓存的项目，目前仅支持 redis（value,list,set,hash）的相关操作；
该系统中 redis的使用远程dubbo接口定义  完全继承 spring-data-redis.jar中的接口，所以具体方法含义 自己寻找。
ValueOperations　　ValueOperationsService   ——基本数据类型和实体类的缓存
ListOperations　　  ListOperationsService    ——list的缓存
SetOperations　　     SetOperationsService     ——set的缓存
HashOperations　　   HashOperationsService    ——Map的缓存

*重要：redis中对对象Operation操作的方法做了封装，
例如 getOperations.delete方法>>>封装为>>>getOperations_delete方法


**********************IP拦截器***********************
可以根据dubbo客户端的访问IP，限制和拦截某些IP，目前全开放


**********************redis操作key head拦截器***********************
根据redis请求key的 modulecode,
例如：key>>SHIPMENT:test,其modulecode为 SHIPMENT:,dubbo-redis服务端会验证 是否允许该modulecode通过操作。
允许：放过，不允许：返回null


**********************添加允许的 redis key head***********************
*重要：添加的modulecode，一定要添加后缀 “:”；
*重要：添加modulecode的dubbo操作，直接操作就可以，没有拦截操作！

1:添加成功 ， 0：已经存在 ， -1：添加失败
ModuleDefineService.addModuleCode(String code);
例如：int i=moduleDefineService.addModuleCode("O2OSHIPMENT:");
i=1,modulecode该添加成功，可以直接使用
i=0,该modulecode已经存在，请换一个，以免数据冲突覆盖
i=-1,系统异常，添加失败

key 参考：RedisKeyGenerator


**********************流程操作***********************
1.你的dubbo客户端引入 所需的 服务端接口
2.定义模块的 modulecode 例如：(O2O_SHIPMENT:)
3.使用moduleDefineService服务，添加定义的 modulecode
4.redis cache key=modulecode+业务key




------------------------------------------具体使用参考-------------------------------------------------------
添加pom.xml依赖项目
<dependency>
	<groupId>com.sound.cloud.api</groupId>
	<artifactId>biz.api.cache</artifactId>
	<version>1.0</version>
</dependency>

添加dubbo远程接口服务：
<!-- dubbo redis service -->
<dubbo:reference id="valueOperationsService" interface="com.sdcloud.api.cache.redis.service.ValueOperationsService"/>
<dubbo:reference id="listOperationsService" interface="com.sdcloud.api.cache.redis.service.ListOperationsService"/>
<dubbo:reference id="setOperationsService" interface="com.sdcloud.api.cache.redis.service.SetOperationsService"/>
<dubbo:reference id="hashOperationsService" interface="com.sdcloud.api.cache.redis.service.HashOperationsService"/>
<dubbo:reference id="moduleDefineService" interface="com.sdcloud.api.cache.redis.service.ModuleDefineService" />

具体使用方法例子：
public void testSayHello(){
	//项目注册 redis key_head,该操作 最好在项目启动的时候 添加一次即可
	moduleDefineService.addModuleCode("O2O_SHIPMENT:");
	//添加 数据
	valueOperationsService.set("O2O_SHIPMENT:testkey", "test");
    //查询数据
	System.out.println(valueOperationsService.get("O2O_SHIPMENT:testkey"));
}








