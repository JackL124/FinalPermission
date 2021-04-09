package com.jackl.librxaoppermission.RxPermission

import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.jackl.librxaoppermission.enum.PermissionStatus
import io.reactivex.subjects.PublishSubject
import java.util.*

/**
 * 权限回调fragment
 * Created jackl on 2021/4/7
 */
internal class RxPermissionsFragment : Fragment() {

    private val mSubjects: MutableMap<String, PublishSubject<Permission>> =
        HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermissions(permissions: Array<String>) {
        requestPermissions(permissions, PERMISSIONS_REQUEST_CODE)
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != PERMISSIONS_REQUEST_CODE) {
            return
        }
        val shouldShowRequestPermissionRationale =
            BooleanArray(permissions.size)
        for (i in permissions.indices) {
            shouldShowRequestPermissionRationale[i] =
                shouldShowRequestPermissionRationale(permissions[i])
        }
        onRequestPermissionsResult(permissions, grantResults, shouldShowRequestPermissionRationale)
    }

    fun onRequestPermissionsResult(
        permissions: Array<String>,
        grantResults: IntArray,
        shouldShowRequestPermissionRationale: BooleanArray
    ) {
        var i = 0
        val size = permissions.size
        while (i < size) {
            val subject = mSubjects[permissions[i]]
            if (subject == null) {
                return
            }
            mSubjects.remove(permissions[i])
            val granted = grantResults[i] == PackageManager.PERMISSION_GRANTED
            var status = ""
            if (granted) {
                status = PermissionStatus.GRANTED.name
            } else if (shouldShowRequestPermissionRationale[i]) {
                status = PermissionStatus.REFUSED.name
            } else {
                status = PermissionStatus.REFUSENOTPROMPT.name
            }

            subject.onNext(
                Permission(
                    permissions[i],
                    status
                )
            )
            subject.onComplete()
            i++
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    internal fun isGranted(permission: String?): Boolean {
        val fragmentActivity = activity
            ?: throw IllegalStateException("This fragment must be attached to an activity.")
        return fragmentActivity.checkSelfPermission(permission!!) == PackageManager.PERMISSION_GRANTED
    }

    @TargetApi(Build.VERSION_CODES.M)
    internal fun isRevoked(permission: String?): Boolean {
        val fragmentActivity = activity
            ?: throw IllegalStateException("This fragment must be attached to an activity.")
        return fragmentActivity.packageManager
            .isPermissionRevokedByPolicy(permission!!, activity!!.packageName)
    }

    internal fun getSubjectByPermission(permission: String): PublishSubject<Permission>? {
        return mSubjects[permission]
    }

    internal fun containsByPermission(permission: String): Boolean {
        return mSubjects.containsKey(permission)
    }

    internal fun setSubjectForPermission(
        permission: String,
        subject: PublishSubject<Permission>
    ) {
        mSubjects[permission] = subject
    }

    private companion object {
        private const val PERMISSIONS_REQUEST_CODE = 42
    }
}