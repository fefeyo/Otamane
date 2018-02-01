package com.fefe.otamane.fragments.dialog

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.*
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import com.fefe.otamane.R
import com.fefe.otamane.Utils.DBUtils
import com.fefe.otamane.datas.Event
import com.fefe.otamane.datas.Product
import com.fefe.otamane.fragments.MyMapView
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import io.realm.Realm
import kotlinx.android.synthetic.main.product_add_dialog.*
import java.util.*

/**
 * Created by fefe on 2018/02/01.
 */
class EventInputDialogFragment : DialogFragment(), OnMapReadyCallback {
    private var listener: OnCommitEventListener? = null
    private val REQUEST_CODE = 100
    private var place: com.fefe.otamane.datas.Place? = com.fefe.otamane.datas.Place()
    private var map: GoogleMap? = null

    companion object {
        fun getInstance(productId: Long, type: Int) = EventInputDialogFragment().apply {
            arguments = Bundle().apply {
                putLong("productId", productId)
                putInt("type", type)
            }
        }
    }

    interface OnCommitEventListener {
        fun onCommit(event: Event)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(activity)
        dialog.setCancelable(false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.event_input_dialog, container, false)
        val eventType = arguments?.get("type").toString().toInt()
        val productId = arguments?.get("productId").toString().toInt()
        val map = SupportMapFragment.newInstance()
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.map, map)
        transaction.commit()
        map.getMapAsync(this)
        dialog.setCancelable(false)
        val titleText = view.findViewById<EditText>(R.id.input_title)
        val memoText = view.findViewById<EditText>(R.id.input_memo)
        val datePicker = view.findViewById<DatePicker>(R.id.input_date)
        view.findViewById<Button>(R.id.input_search).setOnClickListener {
            val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(activity)
            startActivityForResult(intent, REQUEST_CODE)
        }

        view.findViewById<Button>(R.id.input_commit).setOnClickListener {
            if(!TextUtils.isEmpty(titleText.text.toString())) {
                Realm.getDefaultInstance().use {
                    it.executeTransaction {
                        val product = it.where(Product::class.java).equalTo("id", productId).findFirst()
                        val event = it.createObject(Event::class.java, DBUtils.initEventId())
                        event.name = titleText.text.toString()
                        event.productid = productId
                        event.memo = memoText.text.toString()
                        event.eventType = eventType
                        val cal = Calendar.getInstance()
                        cal.clear()
                        cal.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
                        event.date = cal.time
                        if(!TextUtils.isEmpty(place?.name)) {
                            val savePlace = it.copyToRealmOrUpdate(place)
                            event.place = savePlace
                        }
                        product?.events?.add(event)
                        listener?.onCommit(event)
                        dialog.dismiss()
                    }
                }
            }else {
                titleText.error = "イベント名の入力は必須です"
            }
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            val p = PlaceAutocomplete.getPlace(activity?.applicationContext, data)
            place?.id = DBUtils.initPlaceId()
            place?.name = p.name.toString()
            place?.address = p.address.toString()
            place?.phoneNumber = p.phoneNumber.toString()
            place?.lat = p.latLng.latitude
            place?.lng = p.latLng.longitude
            place?.url = p.websiteUri.toString()
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(p.latLng, 15f))
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val lp = dialog.window.attributes
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window.attributes = lp
    }

    override fun onMapReady(map: GoogleMap?) {
        this.map = map
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(35.6983548, 139.7733992), 15f))
    }


    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        listener = parentFragment as OnCommitEventListener
        if (listener !is OnCommitEventListener) throw ClassCastException("リスナー登録失敗")
    }
}