package com.jackl.librxaoppermission.RxPermission

/**
 * 权限结果实体类
 * Created jackl on 2021/4/7
 */
class Permission {

    var name: String //权限名称
    var status: String //权限状态

    constructor(
        name: String,
        status: String
    ) {
        this.name = name
        this.status = status
    }

    override fun toString(): String {
        return "Permission(name='$name', status='$status')"
    }

}
