package com.fefe.otamane.datas

import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmObject
import java.io.Serializable

/**
 * Created by fefe on 2018/01/25.
 */
// チケットデータの保存（発券コード等）
open class Ticket(
        open var id: Long = 0,
        open var name: String = "",
        open var price: Int = 0,
        open var image: ByteArray? = null,
        open var event: Event? = null
) : RealmObject(), Serializable, RealmModel {}