# Сервер JBoss Wildfly 8

Данный образ базируется на образе [javister-docker-openjdk:1.0.java8](https://github.com/javister/javister-docker-openjdk).

Содержимое, добавляемое данным образом:

1. Сервер [JBoss Wildfly 8](http://wildfly.org/) с исправлениями для обеспечения стабильности работы и некоторыми дополнительными подсистемами:
    1. [JBoss SwitchYard 2.0](http://switchyard.jboss.org/)
    2. (Скоро!!!) [JBoss ModeShape 4](http://modeshape.jboss.org/)
2. Используемые сетевые порты:
    1. 8080 - http
    2. 8443 - https
    3. 9990 - management http
    4. 9993 - management https
    5. 8009 - ajp (если активировано)
    6. 8787 - debug (если активировано)
3. Добавлены переменные окружения:
    1. JAVA_XMS - минимальный размер кучи JVM
    2. JAVA_XMX - максимальный размер кучи JVM
    3. SERVER_PARAMS - параметры запуска сервера Wildfly
