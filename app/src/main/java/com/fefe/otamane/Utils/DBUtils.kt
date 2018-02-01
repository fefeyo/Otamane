package com.fefe.otamane.Utils

import com.fefe.otamane.datas.*
import io.realm.Realm

/**
 * Created by fefe on 2018/01/26.
 */
class DBUtils {
    companion object {

        fun initProductId(): Long {
            Realm.getDefaultInstance().use { realm ->
                val maxId = realm.where(Product::class.java).max("id")
                return when (maxId) {
                    null -> 1
                    else -> maxId.toLong() + 1
                }
            }
        }

        fun initEventId(): Long {
            Realm.getDefaultInstance().use { realm ->
                val maxId = realm.where(Event::class.java).max("id")
                return when (maxId) {
                    null -> 1
                    else -> maxId.toLong() + 1
                }
            }
        }

        fun initStuffId(): Long {
            Realm.getDefaultInstance().use { realm ->
                val maxId = realm.where(Stuff::class.java).max("id")
                return when (maxId) {
                    null -> 1
                    else -> maxId.toLong() + 1
                }
            }
        }

        fun initTicketId(): Long {
            Realm.getDefaultInstance().use { realm ->
                val maxId = realm.where(Ticket::class.java).max("id")
                return when (maxId) {
                    null -> 1
                    else -> maxId.toLong() + 1
                }
            }
        }

        fun initPlaceId(): Long {
            Realm.getDefaultInstance().use { realm ->
                val maxId = realm.where(Place::class.java).max("id")
                return when (maxId) {
                    null -> 1
                    else -> maxId.toLong() + 1
                }
            }
        }
    }
}