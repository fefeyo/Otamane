package com.fefe.otamane.datas

import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Created by fefe on 2018/02/01.
 */
@RealmClass
open class Place(
        @PrimaryKey
        open var id: Long = 0,
        open var name: String? = "",
        open var address: String? = "",
        open var phoneNumber: String? = "",
        open var lat: Double? = 0.0,
        open var lng: Double? = 0.0,
        open var url: String? = ""
) : RealmModel {}