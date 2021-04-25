package com.jackl.app

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jackl.finalpermission.RxPermission.Permissions
import com.jackl.finalpermission.annotation.RequestPermission
import com.jackl.finalpermission.enum.PermissionStatus
import kotlinx.android.synthetic.main.fragment_permission.*

class PermissionFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_permission, container, false)
        return v

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button1.setOnClickListener {
            RequestBlockPermission()
        }

        button2.setOnClickListener {
            RequestNonblockPermission()
        }

        button3.setOnClickListener {
            RequestPermissionForResult()
        }
    }


    /**
     * 用法1 阻塞型
     * 只有全部权限都同意之后才执行方法内部代码块
     * */
    @RequestPermission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private fun RequestBlockPermission() {
        Log.d(MainActivity.TAG,"权限都同意啦，继续撸代码...")

    }


    /**
     * 用法2 非阻塞型
     * 不管权限是否同意都会执行方法内部代码块
     * */
    @RequestPermission(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,isBlock = false)
    private fun RequestNonblockPermission() {
        Log.d(MainActivity.TAG,"申请权限操作结束啦，继续撸代码...")
    }


    /**
     * 用法3 自定义结果型
     * 如果需要自定义权限处理结果，可以使用一下形式
     * 声明一个Permissions类型的入参用于接收权限申请结果
     * 注意： 1 入参需要设置在第一个参数位置，否则无效  2 Permissions和 isBlock互斥 只能二选一，使用Permissions后isBlock属性无效
     * */
    @RequestPermission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private fun RequestPermissionForResult(permissions: Permissions = Permissions()) {
        Log.d(MainActivity.TAG,"permission=${permissions}")
        when(permissions.finalStatus){
            PermissionStatus.GRANTED.name->{
                Log.d(MainActivity.TAG,"用户已经同意该权限")
            }
            PermissionStatus.REFUSED.name->{
                Log.d(MainActivity.TAG,"权限被拒绝")
            }
            PermissionStatus.REFUSENOTPROMPT.name->{
                Log.d(MainActivity.TAG,"权限拒绝不再提示")
            }
        }
    }

}