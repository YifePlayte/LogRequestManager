package com.yifeplayte.logrequestmanager.activity.pages

import android.annotation.SuppressLint
import cn.fkj233.ui.activity.annotation.BMMainPage
import com.yifeplayte.logrequestmanager.R
import com.yifeplayte.logrequestmanager.activity.pages.data.BaseSelectApplicationsPage

@SuppressLint("NonConstantResourceId")
@BMMainPage(titleId = R.string.allow_device_log_access_request_whitelist)
class AllowDeviceLogAccessRequestWhitelistPage :
    BaseSelectApplicationsPage("allow_device_log_access_request_whitelist")