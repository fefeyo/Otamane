package com.fefe.otamane.datas

import android.view.View

import com.roomorama.caldroid.CaldroidFragment
import com.roomorama.caldroid.CaldroidListener

import java.util.Date

/**
 * Created by fefe on 2018/02/01.
 */

class Sample {
    internal fun main() {
        val fragment = CaldroidFragment()
        fragment.caldroidListener = object : CaldroidListener() {
            override fun onSelectDate(date: Date, view: View) {

            }
        }
    }
}
