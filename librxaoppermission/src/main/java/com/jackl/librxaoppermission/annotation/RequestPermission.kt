package com.jackl.librxaoppermission.annotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * 权限申请注解
 * Created jackl on 2021/4/6
 * @param perms 权限列表
 *  @param isBlock true 阻塞型：只有权限全部申请通过，被注解代码才会执行，拒绝权限或拒绝不在提示，不执行注解代码
 *                false 非阻塞型：无论权限是否申请通过，被注解代码都会被执行 (即权限申请和后续业务逻辑不存在因果关系,例如app启动时申请权限等应用场景)
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
annotation class RequestPermission(
    vararg val perms: String,val isBlock:Boolean=true
)