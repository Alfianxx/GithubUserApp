package com.alfian.githubuserapp.helper

import android.database.Cursor
import com.alfian.githubuserapp.db.UserContract
import com.alfian.githubuserapp.entity.User

object MappingHelper {
    fun mapCursorToArrayList(gitCursor: Cursor?): ArrayList<User> {
        val listUser = ArrayList<User>()
        gitCursor?.apply {
            while (moveToNext()) {
                val user = User()
                user.id = getInt(getColumnIndexOrThrow(UserContract.UserColumns._ID))
                user.username = getString(getColumnIndexOrThrow(UserContract.UserColumns.USERNAME))
                user.url = getString(getColumnIndexOrThrow(UserContract.UserColumns.URL))
                user.avatar = getString(getColumnIndexOrThrow(UserContract.UserColumns.AVATAR))
                listUser.add(user)
            }
        }
        return listUser
    }
}