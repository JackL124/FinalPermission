package com.jackl.finalpermission

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.ContextCompat
import com.jackl.finalpermission.RxPermission.Permissions
import com.jackl.finalpermission.RxPermission.RxPermissions
import java.lang.ref.WeakReference
import kotlin.coroutines.suspendCoroutine
import android.provider.Settings


/**
 * 动态权限申请管理入口类
 * Created jackl on 2021/4/6
 */
object PermissionEngine {

    /**
    * 检查是否申请过权限
     * @param activity context
     * @param perms 权限列表
     * @return true 权限已经申请或不需要权限, false 权限未申请
    * */
    fun checkPermissions (context: WeakReference<Context>, vararg perms: String): Boolean{
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        //遍历权限是否存在
        for (perm in perms) {
            if (ContextCompat.checkSelfPermission(context.get()!!, perm)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    /**
     * 申请权限
     * @param context context弱引用
     * @param perms 权限列表
     * @return Permission 申请结果回调
     * */
    suspend fun requestPermissions (context: WeakReference<Context>,vararg perms: String): Permissions =
        suspendCoroutine { continuation->
        val rxPermissions =
            RxPermissions(context)
        rxPermissions.request(*perms)
            ?.subscribe {
                it?.let {
                continuation.resumeWith(Result.success(it))
                }
            }
    }

    /**
     * 弹出引导手动开启权限弹框
     * @param context activity弱引用
     * */
     fun showSetPermissionDialog(context: WeakReference<Context>) {
            val dialog = AlertDialog.Builder(context.get())
                .setTitle(context.get()!!.getString(R.string.dialog_title))
                .setMessage(context.get()!!.getString(R.string.dialog_Message))
                .setPositiveButton(context.get()!!.getString(R.string.dialog_Positivetext), {
                        dialog, which ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:" + context.get()!!.getPackageName())
                    context.get()!!.startActivity(intent)
                })
                .setNegativeButton(context.get()!!.getString(R.string.dialog_Negativetext),  {
                        dialog, which ->
                })
                .show()
    }


}