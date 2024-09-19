# Lab1
## 环境配置
MacOs，使用homebrew下载gradle
```
brew install gradle 
```
使用 gradle -v 查看版本,输出如下
```
------------------------------------------------------------
Gradle 8.10.1
------------------------------------------------------------

Build time:    2024-09-09 07:42:56 UTC
Revision:      8716158d3ec8c59e38f87a67f1f311f297b79576

Kotlin:        1.9.24
Groovy:        3.0.22
Ant:           Apache Ant(TM) version 1.10.14 compiled on August 16 2023
Launcher JVM:  1.8.0_412 (Azul Systems, Inc. 25.412-b08)
Daemon JVM:    /Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home (no JDK specified, using current Java home)
OS:            Mac OS X 14.4.1 aarch64
```
## Hello World
使用gradle构建一个hello world程序
 * 首先在工程主目录下创建build.gradle文件,Gradle配置核心文件
 * 在build.gradle中添加如下内容
```gradle
task hello {
    doLast {
        println 'Hello world!'
    }
}
```
 * 执行gradle -q hello 命令,输出如下
```
> gradle -q hello
Hello world!
```

## Gradle Wrapper
使用gradle wrapper 命令生成一个标准的gradle工程目录结构
 * gradlew和gradlew.bat是gradle的wrapper脚本(对应类Unix系统和Windows系统)
 * gradle-wrapper.properties是gradle的配置文件,配置了gradle的版本和下载地址
 * gradle-wrapper.jar是gradle的jar包,用于执行gradle命令 

gradle-wrapper.properties 文件内容如下 
```
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.10.1-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```
逐一解读这段内容:
 * distributionBase=GRADLE_USER_HOME 表示Gradle的压缩包将被存储在GRADLE_USER_HOME目录下
 * distributionPath=wrapper/dists 表示相对于distributionBase的路径,存储解压后的Gradle压缩包
 * distributionUrl=https\://services.gradle.org/distributions/gradle-8.10.1-bin.zip 表示Gradle的下载地址
 * networkTimeout=10000 表示下载Gradle压缩包的超时时间,单位为毫秒
 * validateDistributionUrl=true 表示在下载Gradle压缩包之前,先验证distributionUrl是否有效
 * zipStoreBase=GRADLE_USER_HOME 同distributionBase,但是是zip压缩包的存储路径
 * zipStorePath=wrapper/dists 同distributionPath,但是是zip压缩包的存储路径

## settings.gradle
settings.gradle 文件是Gradle的配置文件,用于配置Gradle的项目结构和依赖关系 
 * Gradle支持多工程结构,每个工程可以有自己的build.gradle文件,也可以有自己的settings.gradle文件，使用settings.gradle文件可以配置工程之间的依赖关系，配置添加子工程
 * settings文件在初始化阶段执行,创建Settings对象,并调用Settings#initialize()方法
 * 在settings文件中可以使用include()方法添加子工程,使用exclude()方法排除子工程
 
 ## Gradle生命周期
 Gradle生命周期分为三个阶段:
  * 初始化阶段:initialization,初始化阶段确定哪些工程参与构建，并为每个工程创建Project实例
  * 配置阶段: configuration,配置阶段解析每个工程的build.gradle文件，为每个Project实例创建Task实例，确定各任务之间关系，并初始化配置
  * 执行阶段: execution,执行阶段执行配置阶段确定的任务

  ## Gradle执行流
  开始 Gradle.buildStarted()
  ### 初始化阶段
   * setting.gradle 执行
   * Gradle.settingsEvaluated()
   * Gradle.projectsLoaded()
  ### 配置阶段
   * Gradle.beforeEvaluate() & Project.beforeEvaluate()
   * build.gradle 确定任务子集，配置Task
   * Gradle.afterEvaluate() & Project.afterEvaluate()
   * Gradle.projectsEvaluated() & Gradle.taskGraph.whenReady()
  ### 执行阶段
   * Gradle.taskGraph.beforeTask()
   * Task.taskAction() 执行Task中的Actions
   * Gradle.taskGraph.afterTask()
   

结束 Gradle.buildFinish()  
## Gradle的任务
Gradle的任务是执行的最小单元,每个任务可以有自己的输入和输出,可以有自己的依赖关系,可以有自己的执行逻辑
 * 任务的输入和输出可以是文件,也可以是其他对象,比如数据库,服务器,缓存等
 * 任务的执行逻辑可以是脚本,也可以是代码,比如groovy,java,kotlin等
 * 可以使用命令
    * ./gradlew tasks --all 查看所有任务
    * ./gradlew tasks 查看可运行任务
    * ./gradlew A B 表示同时执行任务A和任务B

## Coding
```gradle
this.beforeEvaluate {

    println "beforeEvaluate.."

}

this.gradle.beforeProject {

    println "beforeProject.."

}

this.gradle.afterProject {

    println "afterProject.."

}

this.gradle.taskGraph.whenReady {
    println "whenReady.."
}

task A {

    println "Configuration: A.." 

    doLast {
        println actions
        println "doLast A"
    }
}

task B {

    println "Configuration: B.."

    doLast {
        println actions
        println "doLast B"
    }
}

task C {

    println "Configuration: C.."

    doLast {
        println actions
        println "doLast C"
    }
}

task allTasks(dependsOn: [A, B, C]){

    println "Configuration: allTasks.."

    doLast {
        println "doLast allTasks"
    }
}

task finalized{
    doLast {
        println "clearing allTasks.."
    }
}

allTasks.finalizedBy finalized

this.gradle.buildFinished {
    println "buildFinished.."
}
```
执行gradle allTasks结果如下
```
> Configure project :
Configuration: A..
Configuration: B..
Configuration: C..
Configuration: allTasks..
afterProject..
whenReady..

> Task :A
[org.gradle.api.internal.AbstractTask$ClosureTaskAction@735ea400]
doLast A

> Task :B
[org.gradle.api.internal.AbstractTask$ClosureTaskAction@3639ad80]
doLast B

> Task :C
[org.gradle.api.internal.AbstractTask$ClosureTaskAction@c6150e7]
doLast C

> Task :allTasks
doLast allTasks

> Task :finalized
clearing allTasks..
buildFinished..
```
这里beforeEvaluate和beforeProject方法并没有打印，伏笔。

