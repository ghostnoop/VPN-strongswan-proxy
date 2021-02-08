package com.fast.fastvpnsecured

import android.app.Application
import io.branch.referral.Branch

public class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize the Branch SDK
        Branch.getAutoInstance(this);
    }
}