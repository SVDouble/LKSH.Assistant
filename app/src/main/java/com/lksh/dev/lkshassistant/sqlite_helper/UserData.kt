package com.lksh.dev.lkshassistant.sqlite_helper

data class UserData(var _id: Int,
               var login: String,
               var password: String,
               var house: String,
               var parallel: String,
               var name: String,
               var surname: String,
               var admin: Int,
               var room: String,
               var grade: String,
               var city: String,
               var school: String)
