# TechBlogSystem
基于高并发、RabbitMQ、Redis、设计模式、JVM等技术的博客系统解决方案


【使用配置】<br>
消息队列：需要给rabbitmq设置/blog的host，然后让指定用户使用它<br>
redis：直接在启动项目时启动客户端即可<br>
其他：看项目的 [ 项目必需资源 ] 文件<br>
<br>

【项目特色】<br>
高并发实践：<br>
同步锁的应用（点赞、收藏、关注、转发、封禁消息添加）<br>
缓存 —— mysql与redis先写再删<br>
redis三大缓存问题解决（缓存雪崩：随机过期时间，缓存击穿穿透：互斥锁）<br>
限流 —— Tomcat最大线程数设置（server:tomcat:threads:max:300）<br>

消息队列实践：<br>
用户在特定时间发布博客、用户延时封禁——延迟队列<br>
【数据缓冲cache队列】邮箱队列、评论队列（两个交换机扔一个队列）<br>
【封禁延时delay队列】用户封禁延时（一个交换机管理队列和其死信队列，还有一个取消交换机与队列）<br>
【延时博客publish队列】用户在特定时间发布博客（一个交换机管理队列和其死信队列）<br>
*需要装入消息队列中的实体对象，都会实现MessageReact泛型接口（邮箱、博客、评论）<br>

redis进阶实现：<br>
缓存相关数据信息：指定id的博客内容、指定博客下的评论、博客与评论的分享数<br>
缓存待添加的数据并交给mysql处理：发博客、发邮件（单发）<br>
使用多种redis储存手段，比如hset和zset等等（点赞缓存使用的hash，点赞通知信息使用集合）<br>

定时任务实践：<br>
统计相关在缓存中的数据（比如点赞缓存）<br>

mysql实践：<br>
使用视图提高数据查询效率<br>
使用索引对数据库的数据进行约束（blog、comment的likes_id使用唯一索引）<br>
sql注入检查<br>
<br>

【技术Q&A】<br>
Q:如何确保用户/管理员登录时间的准确性？<br>
A:每一天第一次通过登录验证的时刻作为登录时间<br>

Q:如何给用户通知统计好了的点赞信息邮箱？（用户登录发邮件的想法不错，然后呢？或许可以不用邮箱？）<br>
A:可以考虑使用redis的set（用户id → [点赞者:点赞体，....]），点赞时就添加，取消点赞别管，这样既能方便记数也不怕重复点赞出问题，读完之后删除就行<br>

Q:一定得要将通知邮件一个个发给用户手里吗？或者可能有更好的方法？<br>
A:建立表notice_check(user_id,check_mail) 来确保用户对于通知邮件（mail的user_id为null）的读取状况<br>
用户读取通知邮件时，只需要获取比自己的LocalDateTime大的邮件即可<br>
建立视图uncheck_notice (user_id,num)来统计用户未读的通知邮件<br>

Q:如何将延时和手动解封用到ramq上，如果需要加时呢？<br>
A:在ramq中添加封禁延时交换机 管理 用户封禁信息队列、用户解封队列，添加取消封禁交换机 管理 取消封禁队列
用户封禁信息队列存储封禁用户、封禁时间戳的信息，封禁时间为其ttl，用户id+封禁结束时间戳为其信息id，当ttl耗尽后，该信息通过封禁延时交换机转至用户解封队列供消费者处理<br>
当需要即时取消封禁信息时，将从用户中得到的数据处理为封禁信息id作为信息传入到取消封禁队列供消费者处理，消费者收到消息后，
通过connectionFactory获取connection的channel，遍历读取用户封禁信息队列的信息，找到对应信息id的信息，通过basicAck接收，完成封禁信息的取消<br>
用户封禁：将用户封禁信息存入 用户封禁信息队列，封禁用户（用户mysql数据的封禁信息中存储时间戳）<br>
用户在封禁期间延时封禁：通过从用户中取出的时间戳取消原封禁信息，并将用户封禁信息存入 用户封禁信息队列，封禁用户（更新用户数据的时间戳）<br>
用户解封：通过从用户中取出的时间戳取消原封禁信息，解封用户（用户mysql封禁数据清除<br>

Q:封禁期间如果ramq突然失效，数据丢失了该怎么办？<br>
A:使用日志记住一定时间段的封禁队列消息增删情况，设置定时任务每12:00和0:00时刷新日志。<br>
日志排版：更新点时的所有封禁时消息 和 之后的消息增删<br>
日志单元格式：+/- 用户id 结束时间戳<br>
管理员使用崩溃修复接口修复ramq数据：读取日志，运算统计结果，之后统一把消息恢复，更新日志<br>
PS:所有的消息增加情况都已上锁<br>

Q:如果点赞数据被删除了，那些还在redis里的点赞id数据，归位时该如何处理？<br>
A:归位时多加一道程序，判定点赞likes的id是否存在于表中<br>
<br>

【作者的话】<br>
这是一个我自己用于练手的后端项目<br>
由于制作匆忙，可能有部分前后端对接、前端设计、后端细节等方面处理的没那么好，还请见谅<br>
如果大家有什么想法和建议，欢迎来沟通<br>
也欢迎来自你们的commit以继续强化、完善这个项目<br>
要使用的话也欢迎，但是请务必注明出处，谢谢！<br>