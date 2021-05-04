package com.alfian.consumerapp.helper

import android.database.Cursor
import com.alfian.consumerapp.db.UserContract
import com.alfian.consumerapp.entity.User

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