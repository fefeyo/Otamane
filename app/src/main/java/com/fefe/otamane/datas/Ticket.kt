package com.fefe.otamane.datas

import io.realm.RealmModel
import io.realm.annotations.RealmClass

/**
 * Created by fefe on 2018/01/25.
 */

@RealmClass
// チケットデータの保存（発券コード等）
open class Ticket(
        open var id: Long = 0,
        open var name: String = "",
        open var price: Int = 0,
        open var image: ByteArray? = null,
        open var event: Event? = null
) : RealmModel {}