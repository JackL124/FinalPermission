package com.jackl.finalpermission.aspect

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import com.jackl.finalpermission.PermissionEngine
import com.jackl.finalpermission.RxPermission.Permissions
import com.jackl.finalpermission.annotation.RequestPermission
import com.jackl.finalpermission.enum.PermissionStatus
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
    @Pointcut("execution(@com.jackl.finalpermission.annotation.RequestPermission  * *(..)) && @annotation(permission)")
    fun Pointcut(permission: RequestPermission) {
    }


    /**
     * 环绕织入，在被注解标记的目标切入点函数执行前处理动态权限申请相关操作
     * */
    @Around("Pointcut(permission)")
    fun AroundAdvice(joinPoint: ProceedingJoinPoint, permission: RequestPermission) {
        var targetContext:Context?=null
        if(joinPoint.target is Activity)
            targetContext = joinPoint.target as Activity
        else if(joinPoint.target is Fragment){
            targetContext = (joinPoint.target as Fragment).context
        }else if (joinPoint.target is View)
            targetContext = (joinPoint.target as View).context

        val arg = joinPoint.args
        val hasArg =arg!=null && arg.size>0 && arg[0]!=null && !arg[0].equals("") && arg[0] is Permissions
        if(permission.perms.isEmpty() || targetContext==null ){
            return
        }
        GlobalScope.launch(Dispatchers.Main){
            //如果已经申请过权限,执行执行原始函数
            val  weakActivity=WeakReference(targetContext)
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