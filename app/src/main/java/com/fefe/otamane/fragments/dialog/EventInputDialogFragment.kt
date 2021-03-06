package com.fefe.otamane.fragments.dialog

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.TextUtils
import android.view.*
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import com.beardedhen.androidbootstrap.BootstrapButton
import com.beardedhen.androidbootstrap.BootstrapEditText
import com.beardedhen.androidbootstrap.BootstrapLabel
import com.beardedhen.androidbootstrap.api.view.BootstrapTextView
import com.fefe.otamane.R
import com.fefe.otamane.Utils.DBUtils
import com.fefe.otamane.activities.AddscheduleActivity
import com.fefe.otamane.datas.Event
import com.fefe.otamane.datas.Product
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import io.realm.Realm
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
        val map = view.findViewById<MapView>(R.id.map)
        map.onCreate(null)
        map.getMapAsync(this)
        dialog.setCancelable(false)
        val titleText = view.findViewById<BootstrapEditText>(R.id.input_title)
        val memoText = view.findViewById<BootstrapEditText>(R.id.input_memo)
        val datePicker = view.findViewById<DatePicker>(R.id.input_date)
        when (eventType) {
            1 -> {
                view.findViewById<BootstrapLabel>(R.id.input_date_title).text = "イベント日程"
                view.findViewById<BootstrapLabel>(R.id.input_place_title).text = "イベント会場"
            }
            3 -> {
                view.findViewById<BootstrapLabel>(R.id.input_date_title).text = "発売日"
                view.findViewById<BootstrapLabel>(R.id.input_place_title).text = "購入（予約）店舗"
            }
        }


        view.findViewById<BootstrapButton>(R.id.input_commit).setOnClickListener {
            if (!TextUtils.isEmpty(titleText.text.toString())) {
                val realm = AddscheduleActivity.realm
                realm?.executeTransaction {
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
                    if (!TextUtils.isEmpty(place?.name)) {
                        val savePlace = it.copyToRealmOrUpdate(place)
                        event.place = savePlace
                    }
                    product?.events?.add(event)
                    listener?.onCommit(realm.copyFromRealm(event))
                    dismiss()
                }
            } else {
                titleText.error = "イベント名の入力は必須です"
            }
        }
        view.findViewById<BootstrapButton>(R.id.input_cancel).setOnClickListener {
            dismiss()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            val p = PlaceAutocomplete.getPlace(activity?.applicationContext, data)
            place?.id = DBUtils.initPlaceId()
            place?.name = p.name?.let { it.toString() }
            place?.address = p.address?.let { it.toString() }
            place?.phoneNumber = p.phoneNumber?.let { it.toString() }
            place?.lat = p.latLng.latitude
            place?.lng = p.latLng.longitude
            place?.url = p.websiteUri?.let { it.toString() }
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(p.latLng, 18f))
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
        map?.setOnMapClickListener {
            val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(activity)
            startActivityForResult(intent, REQUEST_CODE)
        }
        map?.uiSettings?.isMapToolbarEnabled = false
        map?.uiSettings?.isScrollGesturesEnabled = false
        map?.uiSettings?.isZoomGesturesEnabled = false
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(35.6983548, 139.7733992), 18f))
    }


    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        listener = parentFragment as OnCommitEventListener
        if (listener !is OnCommitEventListener) throw ClassCastException("リスナー登録失敗")
    }
}