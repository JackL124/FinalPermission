package com.jackl.finalpermission

import android.app.Activity
import android.app.AlertDialog
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
     * @param activity activity弱引用
     * @param perms 权限列表
     * @return true 权限已经申请或不需要权限, false 权限未申请
    * */
    fun checkPermissions (activity: WeakReference<Activity>, vararg perms: String): Boolean{
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        //遍历权限是否存在
        for (perm in perms) {
            if (ContextCompat.checkSelfPermission(activity.get()!!, perm)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    /**
     * 申请权限
     * @param activity activity弱引用
     * @param perms 权限列表
     * @return Permission 申请结果回调
     * */
    suspend fun requestPermissions (activity: WeakReference<Activity>,vararg perms: String): Permissions =
        suspendCoroutine { continuation->
        val rxPermissions =
            RxPermissions(activity)
        rxPermissions.request(*perms)
            ?.subscribe {
                it?.let {
                continuation.resumeWith(Result.success(it))
                }
            }
    }

    /**
     * 弹出引导手动开启权限弹框
     * @param activity activity弱引用
     * */
     fun showSetPermissionDialog(activity: WeakReference<Activity>) {
            val dialog = AlertDialog.Builder(activity.get())
                .setTitle(activity.get()!!.getString(R.string.dialog_title))
                .setMessage(activity.get()!!.getString(R.string.dialog_Message))
                .setPositiveButton(activity.get()!!.getString(R.string.dialog_Positivetext), {
                        dialog, which ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:" + activity.get()!!.getPackageName())
                    activity.get()!!.startActivity(intent)
                })
                .setNegativeButton(activity.get()!!.getString(R.string.dialog_Negativetext),  {
                        dialog, which ->
                })
                .show()
    }


}