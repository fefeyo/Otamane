package com.fefe.otamane.fragments.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog

/**
 * Created by fefe on 2018/01/28.
 */
class EventListDialogFragment : DialogFragment() {
    private var listener: OnChooseTypeListener? = null

    companion object {
        fun getInstance() = EventListDialogFragment()
    }

    interface OnChooseTypeListener {
        fun onChoose(type: Int)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val items = arrayOf("ライブ・イベント", "CD・BD・DVD発売日", "その他", "閉じる")
        val builder = AlertDialog.Builder(activity as Activity)
        builder.setItems(items, { _, which ->
            when(which) {
                0 -> listener?.onChoose(1)
                1 -> listener?.onChoose(3)
                2 -> listener?.onChoose(5)
            }
        })

        return builder.create()
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        listener = parentFragment as OnChooseTypeListener
        if(listener !is OnChooseTypeListener) throw ClassCastException("リスナー登録失敗")
    }
}