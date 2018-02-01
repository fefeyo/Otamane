package com.fefe.otamane.Utils

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import android.provider.MediaStore



/**
 * Created by fefe on 2018/01/26.
 */
class ImageUtils {
    companion object {
        /**
         * 画像データをByteArrayに変換する
         */
        fun createImageData(bitmap: Bitmap?) : ByteArray {
            val baos = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, baos)
            return baos.toByteArray()
        }
    }
}