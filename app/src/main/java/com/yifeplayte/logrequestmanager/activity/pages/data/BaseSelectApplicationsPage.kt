package com.yifeplayte.logrequestmanager.activity.pages.data

import android.content.pm.ApplicationInfo
import android.graphics.drawable.BitmapDrawable
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.dp2px
import cn.fkj233.ui.activity.fragment.MIUIFragment
import cn.fkj233.ui.activity.view.LineV
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextSummaryWithArrowV
import cn.fkj233.ui.activity.view.TextSummaryWithSwitchV
import cn.fkj233.ui.dialog.MIUIDialog
import com.yifeplayte.logrequestmanager.R
import com.yifeplayte.logrequestmanager.activity.view.ImageTextSummaryWithSwitchV
import com.yifeplayte.logrequestmanager.utils.SharedPreferences.addStringToStringSet
import com.yifeplayte.logrequestmanager.utils.SharedPreferences.generateStringSetFromTempBoolean
import com.yifeplayte.logrequestmanager.utils.SharedPreferences.prepareTempBooleanFromStringSet
import com.yifeplayte.logrequestmanager.utils.SharedPreferences.removeStringFromStringSet
import io.github.ranlee1.jpinyin.PinyinFormat
import io.github.ranlee1.jpinyin.PinyinHelper
import me.zhanghai.android.appiconloader.AppIconLoader

abstract class BaseSelectApplicationsPage(val key: String) : BasePage() {
    @get:Synchronized
    @set:Synchronized
    @Volatile
    private var isLoading = true

    init {
        skipLoadItem = true
    }

    override fun asyncInit(fragment: MIUIFragment) {
        initApplicationList(fragment)
    }

    open fun initApplicationList(
        fragment: MIUIFragment,
        keyword: String = "",
        showSystemApplications: Boolean = MIUIActivity.safeSP.mSP?.getBoolean(
            "temp_show_system_applications", false
        ) == true
    ) {
        val sharedPreferences = activity.getSP()
        fragment.showLoading()
        fragment.clearAll()
        fragment.addItem(
            TextSummaryWithArrowV(TextSummaryV(
                textId = R.string.search
            ) {
                MIUIDialog(activity) {
                    setTitle(R.string.search)
                    setEditText("", keyword)
                    setLButton(R.string.clear) {
                        reloadApplicationList(fragment, "", showSystemApplications)
                        dismiss()
                    }
                    setRButton(R.string.done) {
                        reloadApplicationList(fragment, getEditText(), showSystemApplications)
                        dismiss()
                    }
                }.show()
            })
        )
        fragment.addItem(
            TextSummaryWithSwitchV(TextSummaryV(
                textId = R.string.show_system_applications
            ), SwitchV("temp_show_system_applications") {
                reloadApplicationList(fragment, keyword, it)
            })
        )
        fragment.addItem(LineV())
        runCatching {
            sharedPreferences?.prepareTempBooleanFromStringSet(key)
            val applicationsInfo = activity.packageManager.getInstalledApplications(0)
                .filter { showSystemApplications || (it.flags and ApplicationInfo.FLAG_SYSTEM) != 1 }
                .filter {
                    val labelLowercase =
                        it.loadLabel(activity.packageManager).toString().lowercase()
                    val packageNameLowercase = it.packageName.lowercase()
                    val keywordLowercase = keyword.lowercase()
                    labelLowercase.contains(keywordLowercase) || packageNameLowercase.contains(
                        keywordLowercase
                    )
                }.associateWith {
                    val label = it.loadLabel(activity.packageManager).toString()
                    PinyinHelper.convertToPinyinString(label, "", PinyinFormat.WITHOUT_TONE)
                        .lowercase()
                }.entries.sortedBy { it.value }.map { it.key }
            val appIconLoader = AppIconLoader(dp2px(activity, 50f), false, activity)
            applicationsInfo.forEach { applicationInfo ->
                fragment.addItem(
                    ImageTextSummaryWithSwitchV(
                        TextSummaryV(
                            text = applicationInfo.loadLabel(activity.packageManager).toString(),
                            tips = applicationInfo.packageName
                        ),
                        SwitchV("temp_${key}_" + applicationInfo.packageName) { switchValue ->
                            sharedPreferences?.generateStringSetFromTempBoolean(key)
                            if (switchValue) {
                                sharedPreferences?.addStringToStringSet(
                                    key, applicationInfo.packageName
                                )
                            } else {
                                sharedPreferences?.removeStringFromStringSet(
                                    key, applicationInfo.packageName
                                )
                            }
                        },
                        BitmapDrawable(activity.resources, appIconLoader.loadIcon(applicationInfo))
                    )
                )
            }
        }
        fragment.closeLoading()
        isLoading = false
    }

    private fun reloadApplicationList(
        fragment: MIUIFragment,
        keyword: String = "",
        showSystemApplications: Boolean = MIUIActivity.safeSP.mSP?.getBoolean(
            "temp_show_system_applications", false
        ) == true
    ) {
        if (!isLoading) {
            isLoading = true
            Thread { initApplicationList(fragment, keyword, showSystemApplications) }.start()
        }
    }

    override fun onCreate() {}
}