# ShopSeckill
网上商城--秒杀

电商项目的秒杀功能模块

一、项目介绍
核心知识：分布式项目的高并发
高并发的瓶颈：数据库访问
如何解决高并发：
	1.缓存（减少数据库的访问（redis））；
	2.异步通信mq（通常的需求我们都是服务端处理来自客户端的请求，再将结果发给客户端。异步通信的工作方式是：当服务端接收到请求后先将请求发送给消息队列，然后在另一端处理，当前服务端先返回客户端一个“处理中”的结果，等到另一端处理完请求再把真正的处理结果返回给客户端）；
	3.水平扩展：搭建分布式集群（Tomcat集群，数据库集群）

二、项目核心技术点：
	1.MD5加密（密码加密）
	2.分布式session（共享session）
	3.商品详情页，订单详情页，缓存优化
	4.消息队列（秒杀订单生成）
	5.安全优化（隐藏秒杀地址，限流，防止恶意防刷）

三、技术实现
	前端：Thymeleaf + bootstrap + jquery
	中间件： RabbitMQ（异步通信） + redis （高并发）+ Druid（德鲁伊，数据库连接池）
	后端：SpringBoot + mybatis

四、基础架构搭建
	创建空工程
		模块一：通用模块miaosha_commons（vo，工具类等）
		模块二：秒杀商城模块
