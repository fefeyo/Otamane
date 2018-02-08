package com.fefe.otamane.fragments.dialog

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.widget.EditText
import android.widget.ImageButton
import com.fefe.otamane.R
import com.fefe.otamane.Utils.DBUtils
import com.fefe.otamane.Utils.ImageUtils
import com.fefe.otamane.activities.AddscheduleActivity
import com.fefe.otamane.datas.Product
import io.realm.Realm

/**
 * Created by fefe on 2018/01/25.
 */
class ProductAddDialogFragment : DialogFragment() {
    private var imageButton: ImageButton? = null
    private var saveImage: Bitmap? = null
    private var listener: OnProductCommitListener? = null

    interface OnProductCommitListener {
        fun onCommit()
    }

    companion object {
        fun getInstance() = ProductAddDialogFragment()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity as Activity)
        val content = activity?.layoutInflater?.inflate(R.layout.product_add_dialog, null)
        val titleText = content?.findViewById<EditText>(R.id.title)
        imageButton = content?.findViewById<ImageButton>(R.id.image)
        imageButton?.setOnClickListener({
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, 0);
        })
        builder.setView(content)
        builder.setMessage("作品情報入力")
        builder.setPositiveButton("決定", null)
        builder.setNegativeButton("キャンセル", null)
        val dialog = builder.show()
        val button = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        button.setOnClickListener({
            if (!TextUtils.isEmpty(titleText?.text.toString())) {
                val realm = AddscheduleActivity.realm
                realm?.executeTransaction {
                    val product = it.createObject(Product::class.java, DBUtils.initProductId())
                    product.name = titleText?.text.toString()
                    saveImage?.let { product.image = ImageUtils.createImageData(saveImage) }
                }
                dialog.dismiss()
                listener?.onCommit()
            } else {
                titleText?.error = "名前の入力は必須です"
            }
        })

        return dialog
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        listener = parentFragment as OnProductCommitListener
        if (listener !is OnProductCommitListener) throw ClassCastException("リスナー登録失敗")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            imageButton?.setImageURI(data.data)
            saveImage = MediaStore.Images.Media.getBitmap(context?.contentResolver, data.data)
        }
    }
}