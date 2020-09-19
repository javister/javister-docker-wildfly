# Сервер JBoss Wildfly 8

[ ![Download](https://api.bintray.com/packages/javister/docker/javister%3Ajavister-docker-wildfly/images/download.svg?version=wildfly-8.2.1.krista24-jdk8-1.0) ](https://bintray.com/javister/docker/javister%3Ajavister-docker-wildfly/wildfly-8.2.1.krista24-jdk8-1.0/link)
[ ![Download](https://api.bintray.com/packages/javister/docker/javister%3Ajavister-docker-wildfly/images/download.svg?version=bas-8.2.1.krista47-switchyard-jdk8-1.0) ](https://bintray.com/javister/docker/javister%3Ajavister-docker-wildfly/bas-8.2.1.krista47-switchyard-jdk8-1.0/link)
[ ![Download](https://api.bintray.com/packages/javister/docker/javister%3Ajavister-docker-wildfly/images/download.svg?version=bas-8.2.1.krista47-switchyard.modeshape-jdk8-1.0) ](https://bintray.com/javister/docker/javister%3Ajavister-docker-wildfly/bas-8.2.1.krista47-switchyard.modeshape-jdk8-1.0/link)
[ ![Download](https://api.bintray.com/packages/javister/dockertesting/javister-docker-wildfly/images/download.svg?version=1.0) ](https://bintray.com/javister/dockertesting/javister-docker-wildfly/1.0/link)
![Build master branch](https://github.com/javister/javister-docker-wildfly/workflows/Build%20master%20branch/badge.svg)

Данный образ базируется на образе [javister-docker-openjdk:1.0.java8](https://github.com/javister/javister-docker-openjdk).

## Варианты образа

Формирование тега образа происходит следующим образом:

```
${wildfly.version}-${wildfly.layers}-${jdk.version}-${project.version}
```

1. ${wildfly.version} - Версии Wildfly
    1. 8.2.1.krista43
2. ${wildfly.layers} - Дополнительные слои Wildfly
    1. (Чистая, позиция не заполнена)
    2. SwitchYard
    3. SwitchYard.Modeshape5
3. ${jdk.version} - Версии Java
    1. jdk8
    2. jdk11
4. ${project.version} - Собственные версии образа (версии данного проекта)

Примеры:

* `8.2.1.krista43-SwitchYard-jdk8-1.0`
* `8.2.1.krista43-jdk11-1.0`
* `8.2.1.krista43-SwitchYard.Modeshape5-jdk8-1.0`

## Содержимое, добавляемое данным образом

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
    3. JVM_OPTS - параметры JVM, которые необходимо добавить к общим параметрам JAVA_OPTS
    4. SERVER_PARAMS - параметры запуска сервера Wildfly

В образе используется точка монтирования `/config` для хранения конфигурации и персистентных данных. Если на момент старта контейнера данный
каталог пуст, то его содержимое будет создано автоматически, влючая настройки Wildfly по умолчанию (standalone.xml и т.п.).

## Полезные ссылки

* [Wildfly 8 Command line parameters](https://docs.jboss.org/author/display/WFLY8/Command+line+parameters)
