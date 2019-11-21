### 本地maven原型项目使用说明

* 将该压缩文件解压到本地maven仓库指定目录：com/chen

* 从archetype创建项目，执行命令 ：
```
  mvn archetype:generate -DarchetypeCatalog=local
```

* 选择java8-study-project，再输入新项目相应的包名


### 创建新的maven原型archetype项目
```
在项目所在目录执行：
mvn archetype:create-from-project
cd /target/generated-sources/archetype
mvn install
完成后可以在maven仓库中找到该包
```