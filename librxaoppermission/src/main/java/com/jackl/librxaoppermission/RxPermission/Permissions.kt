package com.jackl.librxaoppermission.RxPermission

import com.jackl.librxaoppermission.enum.PermissionStatus

/**
 * 权限结果集合实体类
 * Created jackl on 2021/4/7
 */
class Permissions {

    var finalStatus: String="" //最终的权限结果状态
    var permissionlist= listOf<Permission>()  //权限详情

    constructor()

    internal constructor(permissions: List<Permission>) {
        this.finalStatus=setFinalStatus(permissions)
        this.permissionlist=permissions
    }

    internal fun update(permissions:Permissions) {
        this.permissionlist=permissions.permissionlist
        this.finalStatus=setFinalStatus(permissions.permissionlist)
    }

    internal fun update(status: String) {
        this.finalStatus=status
    }

    override fun toString(): String {
        return "Permissions(finalStatus='$finalStatus', permissionlist=$permissionlist)"
    }


    private fun setFinalStatus(permissions: List<Permission>?): String {
        var status=PermissionStatus.GRANTED.name
        permissions?.forEach {
            if (it.status.equals(PermissionStatus.REFUSED.name)){
                return PermissionStatus.REFUSED.name
            }
            if (it.status.equals(PermissionStatus.REFUSENOTPROMPT.name)){
                status= PermissionStatus.REFUSENOTPROMPT.name
            }
        }
        return status
    }
}


