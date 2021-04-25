
 # **🔥FinalPermission🔥** #
 [![License](https://img.shields.io/badge/License%20-Apache%202-337ab7.svg)](https://www.apache.org/licenses/LICENSE-2.0)
 [![](https://jitpack.io/v/JackL124/FinalPermission.svg)](https://jitpack.io/#JackL124/FinalPermission)
 [![MinSdk](https://img.shields.io/badge/%20MinSdk%20-%2019%2B%20-f0ad4e.svg)](https://android-arsenal.com/api?level=19)


## 这也许可能是用法最简单且功能强大的Android动态权限申请框架之一 ###

 ## 为何要使用FinalPermission
- FinalPermission最快只需一行注解即可完成复杂的权限申请过程，同时对开发者自身业务场景零侵入、零耦合。
- 除此之外您还可以使用自定义结果型用法，针对不同业务场景进行定制化开发，扩展性极强。

## 基本原理
基于AOP编程思想，使用自定义注解标记Pointcut，通过Around环形织入在目标标记代码执行前或执行后插入动态权限申请代码，权限申请在
RxPermissions基础进行修改并加入kotlin协程+suspend函数实现

## Demo
[Download APK-Demo](apk/app-debug.apk)

## 使用步骤

#### 1.aspectjx插件引用
在项目根目录的build.gradle 中依赖AspectJX
```
 dependencies {
        classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.10'
        }
```
在需要使用的Module build.gradle 中应用插件

```
apply plugin: 'android-aspectjx'
```
如集成aspectjx报错或想了解更多aspectjx相关内容，请移步至[AspectJX](https://github.com/HujiangTechnology/gradle_plugin_android_aspectjx)

#### 2.添加 Gradle 依赖
在项目根目录的build.gradle 中添加
```
repositories {
       google()
       jcenter()
       maven { url 'https://jitpack.io' }
   }
```
 在app build.gradle 中添加依赖
```
 dependencies {
 	        implementation 'com.github.JackL124:FinalPermission:v1.0.1'
 	}
```
#### 3.使用

* ##### 阻塞型：
```
    /**
     * 只有全部权限都同意之后才执行方法内部代码块
     * */
    @RequestPermission(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private fun RequestBlockPermission() {
  
        Log.d(TAG,"权限都同意啦，继续撸代码...")
    }
```

* ##### 非阻塞型:
```
  /**
   * 不管权限是否同意都会执行方法内部代码块
   * */
  @RequestPermission(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,isBlock = false)
  private fun RequestNonblockPermission() {
  
      Log.d(TAG,"申请权限操作结束啦，继续撸代码...")
  }
```

* ##### 自定义结果型:
```
   /**
    * 如果需要自定义权限处理结果，可以使用一下形式
    * 声明一个Permissions类型的入参用于接收权限申请结果
    * 注意： 1 入参需要设置在第一个参数位置，否则无效 
    *        2 Permissions和 isBlock互斥,只能二选一，使用Permissions后isBlock属性无效
    * */
   @RequestPermission(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
   private fun RequestPermissionForResult(permissions: Permissions =Permissions()) {

       Log.d(TAG,"permission=${permissions}")
       when(permissions.finalStatus){
           PermissionStatus.GRANTED.name->{
               Log.d(TAG,"用户已经同意该权限")
           }
           PermissionStatus.REFUSED.name->{
               Log.d(TAG,"权限被拒绝")
           }
           PermissionStatus.REFUSENOTPROMPT.name->{
               Log.d(TAG,"权限拒绝不再提示")
           }
       }
   }
```

Permissions返回结果示例：

```
{
    "finalStatus":"REFUSED",//多组权限最终结果
    "permissionlist":[ 
        {
            "finalStatus":"android.permission.CAMERA", //权限名称
            "status":"REFUSED" //单个权限结果
        },
        {
            "finalStatus":"android.permission.WRITE_EXTERNAL_STORAGE",
            "status":"GRANTED"
        }
    ]
}
```

##### 如果觉得的还不错.....欢迎大家Star 👍

## thanks
[AspectJX](https://github.com/HujiangTechnology/gradle_plugin_android_aspectjx)</br>
[RxPermissions](https://github.com/tbruyelle/RxPermissions)

## License

    Copyright 2015 bingoogolapple

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


