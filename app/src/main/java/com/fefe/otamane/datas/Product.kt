package com.fefe.otamane.datas

import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable

/**
 * Created by fefe on 2018/01/25.
 */
//　作品ごとのデータ（ラブライブ！やバンド名などが入る）
open class Product(
        @PrimaryKey
        open var id: Long = 0,
        open var name: String = "",
        open var image: ByteArray? = null,
        open var events: RealmList<Event>? = null
) : RealmObject(), Serializable, RealmModel{}