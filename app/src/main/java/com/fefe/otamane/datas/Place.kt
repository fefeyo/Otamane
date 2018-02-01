package com.fefe.otamane.datas

import io.realm.RealmList
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable
import java.util.*

/**
 * Created by fefe on 2018/02/01.
 */
open class Place(
        @PrimaryKey
        open var id: Long = 0,
        open var name: String? = "",
        open var address: String? = "",
        open var phoneNumber: String? = "",
        open var lat: Double? = 0.0,
        open var lng: Double? = 0.0,
        open var url: String? = ""
) : RealmObject(), Serializable, RealmModel {}