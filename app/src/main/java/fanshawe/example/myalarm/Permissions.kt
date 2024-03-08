package fanshawe.example.myalarm

import android.Manifest
import android.app.Activity
import android.content.Context

import fanshawe.example.myalarm.Constants.PERMISSION_NOTIFICATION_REQUEST_CODE
import com.vmadalin.easypermissions.EasyPermissions

object Permissions {

    fun hasNotificationPermission(context: Context) =
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        )

    fun requestNotificationPermission(activity: Activity){
        EasyPermissions.requestPermissions(
            activity,
            "This application cannot work without POST NOTIFICATION Permission",
            PERMISSION_NOTIFICATION_REQUEST_CODE,
            Manifest.permission.POST_NOTIFICATIONS
        )
    }


}
