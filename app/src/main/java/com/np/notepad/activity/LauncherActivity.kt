package com.np.notepad.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.np.notepad.fragment.HomeFragment
import com.qmuiteam.qmui.arch.QMUIFragmentActivity
import com.qmuiteam.qmui.arch.QMUILatestVisit

class LauncherActivity : Activity() {

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 10
        private val PERMISSIONS_REQUIRED =
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            finish()
            return
        }
        if (allPermissionsGranted()) {
            doAfterPermissionsGranted()
        } else {
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_REQUIRED,
                PERMISSIONS_REQUEST_CODE
            )
        }
        //操
        doAfterPermissionsGranted()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (allPermissionsGranted()) {
                doAfterPermissionsGranted()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    private fun doAfterPermissionsGranted() {
        startActivity(QMUIFragmentActivity.intentOf(this,
            MainActivity::class.java,
            HomeFragment::class.java))
        finish()
    }

    private fun allPermissionsGranted(): Boolean {
        for (permission in PERMISSIONS_REQUIRED) {
            if (ContextCompat.checkSelfPermission(
                    baseContext,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}