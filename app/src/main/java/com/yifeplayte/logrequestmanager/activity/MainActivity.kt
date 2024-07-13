package com.yifeplayte.logrequestmanager.activity

import android.annotation.SuppressLint
import android.os.Bundle
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.dialog.MIUIDialog
import com.yifeplayte.logrequestmanager.R
import com.yifeplayte.logrequestmanager.activity.pages.AllowDeviceLogAccessRequestWhitelistPage
import com.yifeplayte.logrequestmanager.hook.utils.XSharedPreferences.PREFERENCES_FILE_NAME
import kotlin.system.exitProcess

class MainActivity : MIUIActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        checkLSPosed()
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("WorldReadableFiles")
    private fun checkLSPosed() {
        try {
            @Suppress("DEPRECATION")
            setSP(getSharedPreferences(PREFERENCES_FILE_NAME, MODE_WORLD_READABLE))
        } catch (exception: SecurityException) {
            isLoad = false
            MIUIDialog(this) {
                setTitle(R.string.warning)
                setMessage(R.string.not_support)
                setCancelable(false)
                setRButton(R.string.done) {
                    exitProcess(0)
                }
            }.show()
        }
    }

    init {
        activity = this
        registerPage(AllowDeviceLogAccessRequestWhitelistPage::class.java)
    }
}