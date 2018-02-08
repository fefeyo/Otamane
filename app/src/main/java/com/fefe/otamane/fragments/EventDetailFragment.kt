package com.fefe.otamane.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*

import com.fefe.otamane.R
import com.fefe.otamane.activities.ProductDetailActivity
import com.fefe.otamane.datas.Event
import com.fefe.otamane.fragments.dialog.EventListDialogFragment
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import io.realm.kotlin.deleteFromRealm
import kotlinx.android.synthetic.main.fragment_event_detail.*
import java.text.SimpleDateFormat


/**
 * A simple [Fragment] subclass.
 */
class EventDetailFragment : Fragment(), OnMapReadyCallback {
    private var event: Event? = null

    companion object {
        fun getInstance(eventId: Long) = EventDetailFragment().apply {
            arguments = Bundle().apply {
                putLong("eventId", eventId)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_event_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        event = ProductDetailActivity.realm?.where(Event::class.java)?.equalTo("id", arguments?.getLong("eventId"))?.findFirst()
        event_detail_name.text = event?.name
        event_detail_type.text = when(event?.eventType) {
            1 -> "ライブ・イベント"
            3 -> "CD・BD・DVD発売日"
            5 -> "その他"
            else -> ""
        }
        val sdf = SimpleDateFormat("yyyy年MM月dd日")
        event_detail_date.text = sdf.format(event?.date)
        event_detail_place.onCreate(null)
        event_detail_place.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.uiSettings?.isMapToolbarEnabled = false
        map?.uiSettings?.isScrollGesturesEnabled = false
        map?.uiSettings?.isZoomGesturesEnabled = false
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(event?.place?.lat!!, event?.place?.lng!!), 18f))
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.event_detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.event_delete -> {
                ProductDetailActivity.realm?.executeTransaction {
                    event?.deleteFromRealm()
                }
                true
            }
            else -> false
        }
    }

}