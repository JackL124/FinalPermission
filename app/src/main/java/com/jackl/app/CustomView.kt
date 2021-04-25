package com.jackl.app

import android.Manifest
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.jackl.finalpermission.annotation.RequestPermission
import kotlinx.android.synthetic.main.view_customview.view.*

 class CustomView : RelativeLayout{

    constructor(context: Context) : super(context)


    constructor(context: Context,attrs : AttributeSet) : super(context)

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.view_customview, this)
        view.button.setOnClickListener {
            RequestPermission()
        }
    }

    @RequestPermission(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,isBlock = false)
    private fun RequestPermission() {
        Log.d(MainActivity.TAG,"申请权限操作结束啦，继续撸代码...")
    }



}