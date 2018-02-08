package com.fefe.otamane.datas

import io.realm.RealmList
import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Created by fefe on 2018/01/25.
 */
@RealmClass
//　作品ごとのデータ（ラブライブ！やバンド名などが入る）
open class Product(
        @PrimaryKey
        open var id: Long = 0,
        open var name: String = "",
        open var image: ByteArray? = null,
        open var events: RealmList<Event>? = null
) : RealmModel {}