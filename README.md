# Lab3
## Gradle插件
Gradle插件与Gradle是2个概念，Gradle提供了一套核心构建机制，而Gradle插件是运行在该框架上的具体逻辑。
Gradle插件：
 - 逻辑复用: 可以将一些通用的逻辑封装成插件，然后在其他项目中使用
 - 组件发布: 可以将一些组件发布到公共仓库中，其他项目可以直接使用
 - 配置构建: Gradle插件可以声明插件扩展来暴露可配置的属性，提供定制化能力
### 1.脚本插件
脚本插件就是对某个script文件的引用，使用from加载
```groovy
// script.gradle
afterEvaluate {
    println tasks.getByName("packageDebug")
    task ("scriptTask") {
        println "afterEvaluate - scriptTask"
    }
}
```
在app.gradle中引用
```groovy
apply from: '../script.gradle'
```
构建时会有如下输出，说明脚本插件生效
```shell
afterEvaluate - scriptTask
```
### 2.二进制插件
二进制插件本质就是实现了org.gradle.api.Plugin接口的jar包,分为内部插件和第三方插件
#### <1>内部插件
内部插件，由Gradle提供，使用 `apply plugin: pluginId` ,内部插件都有其唯一Id
#### <2>第三方插件
第三方插件，由第三方开发者提供，可以是jar包，也可以是目录.要想在构建脚本中使用第三方插件，需要在buildscript里配置对应的classpath。  
buildscript{}主要用于在项目构建之前配置项目相关依赖。  
## AGP(Android Gradle Plugin)
AGP是Android Gradle插件，是Android Studio中提供的构建工具，它提供了对Android项目的构建和管理能力。  
常见如build、assemble、installDebug...  
只需在项目中引用该插件并配置相关属性，就能快速使用。  
AGP常用扩展属性:  
```groovy
android {
    // 设置编译时用的 Android 版本
    compileSdkVersion 31
    
    // 设置编译时使用的构建工具的版本，Android Studio3.0 后去除此项配置
    buildToolsVersion '30.0.3'

    // 没有配置 productFlavors 时的默认配置
    defaultConfig {
        // 项目的包名
        applicationId "com.billy.myapplication"
        // 项目最低兼容的版本
        minSdkVersion 16
        // 项目的目标版本
        targetSdkVersion 31
        // 版本号
        versionCode 1
        // 版本名称
        versionName "1.0"
        // 表明要使用 AndroidJUnitRunner 进行单元测试
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    
    // 构建环境配置，一般有两种 release、debug，也可以自定义
    buildTypes {
        // 正式
        release {
            // 配置 Log 日志
            buildConfigField("boolean", "LOG_DEBUG", "false")
            
            // 配置 URL 前缀
            buildConfigField("String", "URL_PERFIX", "\"https://release.cn/\"")
            
            // 是否对代码进行混淆
            minifyEnabled false
            
            // 指定混淆的规则文件
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            
            // 设置签名信息
            signingConfig signingConfigs.release
            
            // 是否在 APK 中生成伪语言环境，帮助国际化的东西，一般使用的不多
            pseudoLocalesEnabled false
            
            // 是否对 APK 包执行 ZIP 对齐优化，减小 zip 体积，增加运行效率
            zipAlignEnabled true
            
            // 在 applicationId 中添加了一个后缀，一般使用的不多
            applicationIdSuffix 'test'
            
            // 在 versionName 中添加了一个后缀，一般使用的不多
            versionNameSuffix 'test'
        }
        
        // 开发
        debug {
            // 配置 Log 日志
            buildConfigField("boolean", "LOG_DEBUG", "true")
            
            // 配置 URL 前缀
            buildConfigField("String", "URL_PERFIX", "\"https://test.com/\"")
            
            // 是否对代码进行混淆
            minifyEnabled false
            
            // 指定混淆的规则文件
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            
            // 设置签名信息
            signingConfig signingConfigs.debug
            
            // 是否支持断点调试
            debuggable false
            
            // 是否可以调试NDK代码
            jniDebuggable false
            
            // 是否开启渲染脚本就是一些c写的渲染方法
            renderscriptDebuggable false
            
            // 是否对 APK 包执行 ZIP 对齐优化，减小 zip 体积，增加运行效率
            zipAlignEnabled true
            
            // 是否在 APK 中生成伪语言环境，帮助国际化的东西，一般使用的不多
            pseudoLocalesEnabled false
            
            // 在 applicationId 中添加了一个后缀，一般使用的不多
            applicationIdSuffix 'test'
            
            // 在 versionName 中添加了一个后缀，一般使用的不多
            versionNameSuffix 'test'
        }
        
        custom {
            // 继承上面 release 的配置
            initWith release
            applicationIdSuffix ".releaseCutsom"
        }
    }
    
    // 打包签名配置
    signingConfigs {
        // 正式
        release {
            keyAlias 'test'
            keyPassword '123456'
            storeFile file('test.keystore')
            storePassword '123456'
        }
        // 开发
        debug {
            keyAlias 'test'
            keyPassword '123456'
            storeFile file('test.keystore')
            storePassword '123456'
        }
    }
    
    // 目录指向配置
    sourceSets {
        main {
            // 指定 lib 库目录
            jniLibs.srcDirs = ['libs']
            
            // 根据条件指定 manifest 文件
            if (isDebug.toBoolean()) {
                manifest.srcFile 'src/main/debug/AndroidManifest.xml'
            } else {
                manifest.srcFile 'src/main/release/AndroidManifest.xml'
            }
        }
    }
    
    // 打包时的相关配置
    packagingOptions{
        // pickFirsts 作用是当有重复文件时，打包会报错，这样配置会使用第一个匹配的文件打包进入 apk
        // 表示当 apk 中有重复的 META-INF 目录下有重复的 LICENSE 文件时，只用第一个，这样打包就不会报错
        pickFirsts = ['META-INF/LICENSE']
    
        // merges 合并，当出现重复文件时，合并重复的文件，然后打包入 apk
        // 这个是有默认值的 merges = [] 这样会把默认值去掉，所以我们用下面这种方式，在默认值后添加
        merge 'META-INF/LICENSE'
    
        // 这个是在同时使用 butterknife、dagger2 做的一个处理。同理，遇到类似的问题，只要根据 gradle 的提示，做类似处理即可
        exclude 'META-INF/services/javax.annotation.processing.Processor'
    }
    
    
    /**
        这个配置是经常会使用到的，通常在适配多个渠道的时候，需要为特定的渠道做部分特殊的处理，
        比如设置不同的包名、应用名等。场景：当我们使用友盟统计时，通常需要设置一个渠道ID，那么
        我们就可以利用productFlavors来生成对应渠道信息的包
     */
    productFlavors {
        wandoujia {
            // 豌豆荚渠道包配置，manifestPlaceholders（AndroidManifest里的占位符）
            manifestPlaceholders = [UMENG_CHANNEL_VALUE: "wandoujia"]
        }
        xiaomi {
            manifestPlaceholders = [UMENG_CHANNEL_VALUE: "xiaomi"]
            // 配置包名
            applicationId "com.wiky.gradle.xiaomi"
        }
        _360 {
            manifestPlaceholders = [UMENG_CHANNEL_VALUE: "_360"]
        }
        ***
    }

    /**
        Lint 是Android Studio 提供的 代码扫描分析工具，它可以帮助我们发现代码结构/质量问题，
        同时提供一些解决方案，而且这个过程不需要我们手写测试用例。
        Lint 发现的每个问题都有描述信息和等级（和测试发现 bug 很相似），我们可以很方便地定位问题，
        同时按照严重程度进行解决。
     */
    lintOptions {
        // 即使报错也不会停止打包
        abortOnError false
        // 打包 release 版本的时候进行检测
        checkReleaseBuilds false
    }
}

/**
     该闭包定义了项目的依赖关系，一般项目都有三种依赖方式：本地依赖、库依赖和远程依赖。
     本地依赖可以对本地的jar包或目录添加依赖关系，库依赖可以对项目中的库模块添加依赖关系，
     远程依赖可以对jcener库上的开源项目添加依赖关系。从Android Studio3.0后compile引入库不在使用，
     而是通过 api 和 implementation，api 完全等同于以前的 compile，用 api 引入的库整个项目都
     可以使用，用 implementation 引入的库只有对应的 Module 能使用，其他 Module 不能使用，
     由于之前的项目统一用 compile 依赖，导致的情况就是模块耦合性太高，不利于项目拆解，
     使用 implementation 之后虽然使用起来复杂了但是做到降低偶合兴提高安全性。
 */
dependencies {
    // 本地 jar 包依赖
    implementation fileTree(include: ['*.jar'], dir: 'libs')
    // 远程依赖
    implementation 'com.android.support:appcompat-v7:27.1.1'
    implementation 'com.android.support.constraint:constraint-layout:1.1.2'
    // 声明测试用例库
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'
}
```
## 自定义Gradle插件
### 1.buildscript
在build.gradle中直接编写 `不推荐`
### 2.buildSrc
在项目根目录下创建buildSrc目录，会自动识别并构建。sync后生成其build目录，groovy/java/kotlin也会变成源文件目录，之后编写代码。  
 之后可以通过插件类的全类名来使用。
### 3.独立项目  
Todo  

新建一个module工程，只保留build.gradle和src/main目录
 - 编写插件代码
 - 配置plugin属性
 - 配置build.gradle
 - 发布插件
 - 在其他项目中使用