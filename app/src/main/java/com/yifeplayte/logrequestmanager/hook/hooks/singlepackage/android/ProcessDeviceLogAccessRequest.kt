package com.yifeplayte.logrequestmanager.hook.hooks.singlepackage.android

import android.os.Message
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.ObjectUtils.getObjectOrNull
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
        loadClass("com.android.server.logcat.LogcatManagerService\$LogAccessRequestHandler").methodFinder()
            .filterByName("handleMessage").filterNonAbstract().single().createHook {
                before { param ->
                    val mLogcatManagerService =
                        getObjectOrNull(param.thisObject, "mService") ?: return@before
                    val message = param.args[0] as Message
                    if (message.what != 0) return@before
                    val request = message.obj

                    val client = invokeMethodBestMatch(
                        mLogcatManagerService, "getClientForRequest", null, request
                    ) ?: run {
                        invokeMethodBestMatch(
                            mLogcatManagerService, "declineRequest", null, request
                        )
                        return@before
                    }

                    val packageName = getObjectOrNullAs<String>(client, "mPackageName")
                    val allowDeviceLogAccessRequestList =
                        getStringSet("allow_device_log_access_request_whitelist", mutableSetOf())
                    val methodName =
                        if (allowDeviceLogAccessRequestList.contains(packageName)) "onAccessApprovedForClient" else "onAccessDeclinedForClient"
                    invokeMethodBestMatch(mLogcatManagerService, methodName, null, client)
                    Log.i("$methodName called for $packageName")

                    param.result = null
                }
            }
    }
}
