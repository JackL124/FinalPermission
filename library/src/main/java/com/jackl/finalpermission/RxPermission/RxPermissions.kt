package com.jackl.finalpermission.RxPermission

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.jackl.finalpermission.enum.PermissionStatus
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.subjects.PublishSubject
import java.lang.ref.WeakReference
import java.util.*

/**
 * rxjava权限申请类
 * Created jackl on 2021/4/7
 */
internal class RxPermissions(activity: WeakReference<Activity>) {

    var mRxPermissionsFragment: Lazy<RxPermissionsFragment>? = null

    private companion object{
        val TRIGGER = Any()
        val TAG = RxPermissions::class.java.simpleName
    }

    init {
        mRxPermissionsFragment = getLazySingleton((activity.get()as FragmentActivity).supportFragmentManager)
    }

    private fun getLazySingleton(fragmentManager: FragmentManager): Lazy<RxPermissionsFragment> {
        return object :
            Lazy<RxPermissionsFragment> {
            private var rxPermissionsFragment: RxPermissionsFragment? = null
            @Synchronized
            override fun get(): RxPermissionsFragment {
                if (rxPermissionsFragment == null) {
                    rxPermissionsFragment = getRxPermissionsFragment(fragmentManager)
                }
                return rxPermissionsFragment!!

            }
        }
    }

    private fun getRxPermissionsFragment(fragmentManager: FragmentManager): RxPermissionsFragment? {
        var rxPermissionsFragment: RxPermissionsFragment? =
            findRxPermissionsFragment(fragmentManager)
        val isNewInstance = rxPermissionsFragment == null
        if (isNewInstance) {
            rxPermissionsFragment =
                RxPermissionsFragment()
            fragmentManager
                .beginTransaction()
                .add(rxPermissionsFragment,
                    TAG
                )
                .commitNow()
        }
        return rxPermissionsFragment
    }

    private fun findRxPermissionsFragment(fragmentManager: FragmentManager): RxPermissionsFragment? {
        val fragment = fragmentManager.findFragmentByTag(TAG)
        if (fragment !=null ){
            return fragment as RxPermissionsFragment
        }
        return null
    }

    fun request(vararg permissions: String): Observable<Permissions>? {
        return Observable.just(TRIGGER)
            .compose(ensureEachCombined(*permissions))
    }

    private fun <T> ensureEachCombined(vararg permissions: String): ObservableTransformer<T, Permissions> {
        return ObservableTransformer { o ->
            request(o, *permissions)
                ?.buffer(permissions.size)
                ?.flatMap { permissions->
                    if (permissions.isEmpty()) {
                        Observable.empty()
                    } else Observable.just(
                        Permissions(
                            permissions as List<Permission>
                        )
                    )
                }
        }
    }

    private fun request(
        trigger: Observable<*>,
        vararg permissions: String
    ): Observable<Permission?>? {
        require(!(permissions == null || permissions.size == 0)) { "RxPermissions.request/requestEach requires at least one input permission" }
        return oneOf(trigger, pending(*permissions))
            ?.flatMap({ requestImplementation(*permissions) })
    }

    private fun pending(vararg permissions: String): Observable<*>{
        for (p in permissions) {
            if (!mRxPermissionsFragment?.get()!!.containsByPermission(p)) {
                return Observable.empty<Any>()
            }
        }
        return Observable.just(TRIGGER)
    }

    private fun oneOf(
        trigger: Observable<*>,
        pending: Observable<*>
    ): Observable<*>? {
        return if (trigger == null) {
            Observable.just(TRIGGER)
        } else Observable.merge(trigger, pending)
    }



    private fun requestImplementation(vararg permissions: String): Observable<Permission?>? {
        val list: MutableList<Observable<Permission>?> =
            ArrayList(permissions.size)
        val unrequestedPermissions: MutableList<String> =
            ArrayList()

        for (permission in permissions) {
            if (isGranted(permission)) {
                list.add(
                    Observable.just(
                        Permission(
                            permission,
                            PermissionStatus.GRANTED.name
                        )
                    )
                )
                continue
            }
            if (isRevoked(permission)) {
                list.add(
                    Observable.just(
                        Permission(
                            permission,
                            PermissionStatus.REFUSENOTPROMPT.name
                        )
                    )
                )
                continue
            }
            var subject: PublishSubject<Permission>? =
                mRxPermissionsFragment?.get()?.getSubjectByPermission(permission)
            if (subject == null) {
                unrequestedPermissions.add(permission)
                subject = PublishSubject.create()
                mRxPermissionsFragment?.get()!!.setSubjectForPermission(permission, subject)
            }
            list.add(subject)
        }
        if (!unrequestedPermissions.isEmpty()) {
            val unrequestedPermissionsArray =
                unrequestedPermissions.toTypedArray()
            requestPermissionsFromFragment(unrequestedPermissionsArray)
        }
        return Observable.concat(Observable.fromIterable(list))
    }

    private fun isGranted(permission: String): Boolean {
        return  mRxPermissionsFragment?.get()!!.isGranted(permission)
    }

    private fun isRevoked(permission: String): Boolean {
        return  mRxPermissionsFragment?.get()!!.isRevoked(permission)
    }

    private fun requestPermissionsFromFragment(permissions: Array<String>) {
        mRxPermissionsFragment?.get()!!.requestPermissions(permissions)
    }

    @FunctionalInterface
    interface Lazy<V> {
        fun get(): V
    }

}