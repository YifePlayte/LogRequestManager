package com.yifeplayte.logrequestmanager.hook.hooks.singlepackage.android

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.ObjectUtils.getObjectOrNullAs
import com.github.kyuubiran.ezxhelper.ObjectUtils.invokeMethodBestMatch
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yifeplayte.logrequestmanager.hook.hooks.BaseHook
import com.yifeplayte.logrequestmanager.hook.utils.XSharedPreferences.getStringSet

@Suppress("unused")
object ProcessDeviceLogAccessRequest : BaseHook() {
    override val key = "process_device_log_access_request"
    override val isEnabled = true

    override fun hook() {
        loadClass("com.android.server.logcat.LogcatManagerService").methodFinder()
            .filterByName("processNewLogAccessRequest").filterNonAbstract().single().createHook {
                replace { param ->
                    val mLogcatManagerService = param.thisObject
                    val client = param.args[0]!!
                    val packageName = getObjectOrNullAs<String>(client, "mPackageName")
                    val allowDeviceLogAccessRequestList =
                        getStringSet("allow_device_log_access_request_whitelist", mutableSetOf())
                    val methodName =
                        if (allowDeviceLogAccessRequestList.contains(packageName)) "onAccessApprovedForClient" else "onAccessDeclinedForClient"
                    invokeMethodBestMatch(mLogcatManagerService, methodName, null, client)
                    Log.i("$methodName called for $packageName")
                    return@replace null
                }
            }
    }
}
