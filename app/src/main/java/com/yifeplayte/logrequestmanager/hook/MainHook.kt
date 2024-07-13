package com.yifeplayte.logrequestmanager.hook

import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.Log
import com.yifeplayte.logrequestmanager.hook.hooks.BaseMultiHook
import com.yifeplayte.logrequestmanager.hook.hooks.BasePackage
import com.yifeplayte.logrequestmanager.hook.hooks.BaseSubPackage
import com.yifeplayte.logrequestmanager.hook.hooks.BaseUniversalHook
import com.yifeplayte.logrequestmanager.utils.ClassScanner.scanObjectOf
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage

private const val TAG = "LogRequestManager"
private val singlePackagesHooked by lazy {
    scanObjectOf<BasePackage>("com.yifeplayte.logrequestmanager.hook.hooks.singlepackage")
}
private val multiPackagesHooked by lazy {
    scanObjectOf<BaseMultiHook>("com.yifeplayte.logrequestmanager.hook.hooks.multipackage")
}
private val subPackagesHooked by lazy {
    scanObjectOf<BaseSubPackage>("com.yifeplayte.logrequestmanager.hook.hooks.subpackage")
}
private val universalHooks by lazy {
    scanObjectOf<BaseUniversalHook>("com.yifeplayte.logrequestmanager.hook.hooks.universal")
}
val PACKAGE_NAME_HOOKED: Set<String>
    get() {
        val packageNameHooked = mutableSetOf<String>()
        singlePackagesHooked.forEach { packageNameHooked.add(it.packageName) }
        multiPackagesHooked.forEach { packageNameHooked.addAll(it.hooks.keys) }
        subPackagesHooked.forEach { packageNameHooked.add(it.packageName) }
        return packageNameHooked
    }

@Suppress("unused")
class MainHook : IXposedHookLoadPackage, IXposedHookZygoteInit {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        // init EzXHelper
        if (lpparam.isFirstApplication) {
            EzXHelper.initHandleLoadPackage(lpparam)
            EzXHelper.setLogTag(TAG)
        }

        // single package
        singlePackagesHooked.forEach { it.init() }

        // multiple package
        multiPackagesHooked.forEach { it.init() }

        // single sub-package
        subPackagesHooked.forEach { it.init() }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {

        // init EzXHelper
        EzXHelper.initZygote(startupParam)
        EzXHelper.setLogTag(TAG)

        // universal hook
        universalHooks.forEach { it.init() }
    }
}
