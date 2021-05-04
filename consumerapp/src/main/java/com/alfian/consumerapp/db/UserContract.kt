package com.alfian.consumerapp.db

import android.net.Uri
import android.provider.BaseColumns
import com.alfian.consumerapp.db.UserContract.UserColumns.Companion.TABLE_NAME

object UserContract {
    const val AUTHORITY = "com.alfian.githubuserapp"
    private const val SCHEME = "content"
    internal class UserColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "favorite_user"
            const val _ID = "_id"
            const val USERNAME = "username"
            const val URL = "url"
            const val AVATAR = "avatar"
        }
    }

    val CONTENT_URI : Uri = Uri.Builder().scheme(SCHEME)
        .authority(AUTHORITY)
        .appendPath(TABLE_NAME)
        .build()
}