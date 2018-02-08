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
import com.fefe.otamane.R
import com.fefe.otamane.Utils.DBUtils
import com.fefe.otamane.activities.AddscheduleActivity
import com.fefe.otamane.datas.Event
import com.fefe.otamane.datas.Place
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
class EventEditDialogFragment : DialogFragment(), OnMapReadyCallback {
    private var listener: OnEditEventListener? = null
    private val REQUEST_CODE = 100
    private var event: Event? = Event()
    private var place: Place? = Place()
    private var map: GoogleMap? = null

    companion object {
        fun getInstance(eventId: Long) = EventEditDialogFragment().apply {
            arguments = Bundle().apply {
                putLong("eventId", eventId)
            }
        }
    }

    interface OnEditEventListener {
        fun onEdit(event: Event)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(activity)
        dialog.setCancelable(false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.event_input_dialog, container, false)
        val realm = AddscheduleActivity.realm
        event = realm?.copyFromRealm(realm?.where(Event::class.java)?.equalTo("id", arguments?.getLong("eventId"))?.findFirst())
        place = event?.place
        val map = view.findViewById<MapView>(R.id.map)
        map.onCreate(null)
        map.getMapAsync(this)
        dialog.setCancelable(false)
        val titleText = view.findViewById<BootstrapEditText>(R.id.input_title)
        titleText.setText(event?.name)
        val memoText = view.findViewById<BootstrapEditText>(R.id.input_memo)
        memoText.setText(event?.memo)
        val datePicker = view.findViewById<DatePicker>(R.id.input_date)
        val cal = Calendar.getInstance()
        cal.time = event?.date
        datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE))
        when (event?.eventType) {
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
                    event?.name = titleText.text.toString()
                    event?.memo = memoText.text.toString()
                    val cal = Calendar.getInstance()
                    cal.clear()
                    cal.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
                    event?.date = cal.time
                    if (!TextUtils.isEmpty(place?.name)) {
                        val savePlace = it.copyToRealmOrUpdate(place)
                        event?.place = savePlace
                    }
                    it.copyToRealmOrUpdate(event)
                    listener?.onEdit(event!!)
                    dialog.dismiss()
                }
            } else {
                titleText.error = "イベント名の入力は必須です"
            }
        }
        view.findViewById<BootstrapButton>(R.id.input_cancel).setOnClickListener {
            listener?.onEdit(event!!)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            val p = PlaceAutocomplete.getPlace(activity?.applicationContext, data)
            place?.name = p.name.toString()
            place?.address = p.address.toString()
            place?.phoneNumber = p.phoneNumber.toString()
            place?.lat = p.latLng.latitude
            place?.lng = p.latLng.longitude
            place?.url = p.websiteUri.toString()
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
        if (event?.place != null) {
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(event?.place?.lat!!, event?.place?.lng!!), 18f))
        } else {
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(35.6983548, 139.7733992), 18f))
        }
    }


    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        listener = parentFragment as OnEditEventListener
        if (listener !is OnEditEventListener) throw ClassCastException("リスナー登録失敗")
    }
}