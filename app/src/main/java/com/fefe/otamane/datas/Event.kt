package com.fefe.otamane.datas

import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable
import java.util.*

/**
 * Created by fefe on 2018/01/25.
 */
//　各種イベントデータ(ファンミやライブなど)
open class Event(
        @PrimaryKey
        open var id: Long = 0,
        open var eventType: Int = 0,
        open var productid: Int = 0,
        open var name: String = "",
        open var memo: String = "",
        open var place: Place? = null,
        open var date: Date? = null,
        open var goods: RealmList<Stuff>? = null,
        open var ticket: Ticket? = null
) : RealmObject(), Serializable, RealmModel {}