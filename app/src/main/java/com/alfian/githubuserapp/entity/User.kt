package com.alfian.githubuserapp.entity

class User(
    var id: Int = 0,
    var username: String = "",
    var name: String = "",
    var avatar: String = "",
    var company: String = "",
    var location: String = "",
    var repository: Int = 0,
    var follower: Int = 0,
    var following: Int = 0,
    var url: String = ""
)
