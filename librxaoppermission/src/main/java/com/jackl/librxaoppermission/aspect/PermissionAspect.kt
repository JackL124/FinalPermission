package com.jackl.librxaoppermission.aspect

import android.app.Activity
import com.jackl.librxaoppermission.PermissionEngine
import com.jackl.librxaoppermission.RxPermission.Permissions
import com.jackl.librxaoppermission.annotation.RequestPermission
import com.jackl.librxaoppermission.enum.PermissionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.*
import java.lang.ref.WeakReference

/**
 * 切面类 用于处理申请动态权限流程
 * Created jackl on 2021/4/6
 */
@Aspect
class PermissionAspect {

    /**
     * 定义一个切入点,匹配包自定义注解标记的连接点
     * */
    @Pointcut("execution(@com.jackl.librxaoppermission.annotation.RequestPermission  * *(..)) && @annotation(permission)")
    fun Pointcut(permission: RequestPermission) {
    }


    /**
     * 环绕织入，在被注解标记的目标切入点函数执行前处理动态权限申请相关操作
     * */
    @Around("Pointcut(permission)")
    fun AroundAdvice(joinPoint: ProceedingJoinPoint, permission: RequestPermission) {
        val targetActivity = joinPoint.target as Activity
        val arg = joinPoint.args
        val hasArg =arg!=null && arg.size>0 && arg[0]!=null && !arg[0].equals("") && arg[0] is Permissions
        if(permission.perms.isEmpty() || targetActivity==null || targetActivity.isDestroyed || targetActivity.isFinishing){
            return
        }
        GlobalScope.launch(Dispatchers.Main){
            //如果已经申请过权限,执行执行原始函数
            val  weakActivity=WeakReference(targetActivity)
            if (PermissionEngine.checkPermissions(weakActivity, *permission.perms!!)){
                if(hasArg){
                    val mPermissions = arg[0] as Permissions
                    mPermissions.update(PermissionStatus.GRANTED.name)
                    joinPoint.proceed(joinPoint.args)
                }else{
                    joinPoint.proceed()
                }
                return@launch
            }
            //申请权限并返回结果
            val requestPermissions =
                PermissionEngine.requestPermissions(
                    weakActivity,
                    *permission.perms!!
                )
            if(hasArg){
                //被切入点希望自行处理权限结果
                val mPermissions = arg[0] as Permissions
                mPermissions.update(requestPermissions)
                joinPoint.proceed(joinPoint.args)
            }else{
                if (permission.isBlock){
                    //权限申请通过执行目标切入点函数
                    when(requestPermissions.finalStatus){
                        PermissionStatus.GRANTED.name->{
                            joinPoint.proceed()
                        }
                        PermissionStatus.REFUSENOTPROMPT.name->{
                            PermissionEngine.showSetPermissionDialog(weakActivity)
                        }
                    }
                }else{
                    //权限申请结束执行目标切入点函数
                    joinPoint.proceed()
                }
            }
        }
    }
}