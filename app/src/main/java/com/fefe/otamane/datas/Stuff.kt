package com.fefe.otamane.datas

import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable

/**
 * Created by fefe on 2018/01/25.
 */
// イベントグッズ
open class Stuff(
        @PrimaryKey
        open var id: Long = 0,
        open var name: String = "",
        open var price: Int = 0,
        open var num: Int = 0
) : RealmObject(), Serializable, RealmModel {}