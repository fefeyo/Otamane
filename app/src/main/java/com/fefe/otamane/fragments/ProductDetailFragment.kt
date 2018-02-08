package com.fefe.otamane.fragments


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fefe.otamane.R
import com.fefe.otamane.activities.ProductDetailActivity
import com.fefe.otamane.datas.Event
import com.fefe.otamane.datas.Product
import com.fefe.otamane.fragments.dialog.EventListDialogFragment
import io.realm.Realm
import io.realm.Sort
import kotlinx.android.synthetic.main.fragment_product_detail.*
import java.text.SimpleDateFormat


/**
 * A simple [Fragment] subclass.
 */
class ProductDetailFragment : Fragment() {
    var product: Product? = null
    var realm: Realm? = null
    var listener: OnSelectEvent? = null

    companion object {
        fun getInstance(productId: Long) = ProductDetailFragment().apply {
            arguments = Bundle().apply {
                putLong("productId", productId)
            }
        }
    }

    interface OnSelectEvent {
        fun onSelected(eventId: Long)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_product_detail, container, false)
        realm = ProductDetailActivity.realm
        product = realm?.where(Product::class.java)?.equalTo("id", arguments?.getLong("productId"))?.findFirst()
        (activity as ProductDetailActivity).supportActionBar?.title = product?.name + "のイベント一覧"

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val events = product?.events?.sort("date", Sort.DESCENDING)?.toTypedArray()!!
        if (events.isNotEmpty()) {
            detail_event_list.adapter = EventDetailListAdapter(activity?.applicationContext!!, events)
            detail_event_list.visibility = View.VISIBLE
            detail_event_empty.visibility = View.GONE
        } else {
            detail_event_list.visibility = View.GONE
            detail_event_empty.visibility = View.VISIBLE
        }
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        listener = activity as OnSelectEvent
        if(listener !is OnSelectEvent) throw ClassCastException("リスナー登録失敗")
    }

    inner class EventDetailListAdapter(val context: Context, val datas: Array<Event>) : RecyclerView.Adapter<EventDetailListAdapter.EventDetailViewHolder>() {
        private val sdf = SimpleDateFormat("yyyy年MM月dd日")

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): EventDetailViewHolder {
            val v: View = LayoutInflater.from(parent?.context).inflate(R.layout.detail_event_row, parent, false)
            return EventDetailViewHolder(v)
        }

        override fun onBindViewHolder(holder: EventDetailViewHolder?, position: Int) {
            holder?.name?.text = datas[position].name
            holder?.type?.text = when (datas[position].eventType) {
                1 -> "ライブ・イベント"
                3 -> "CD・BD・DVD発売日"
                5 -> "その他"
                else -> ""
            }
            if (!datas[position].memo.isNullOrBlank()) {
                holder?.memo?.text = datas[position].memo
            } else {
                holder?.memo?.visibility = View.GONE
            }
            holder?.date?.text = sdf.format(datas[position].date)
            holder?.place?.text = datas[position].place?.name
            holder?.itemView?.setOnClickListener {
                listener?.onSelected(datas[position].id)
            }
//            datas[position].place?.let { place ->
//                holder?.initPlace(place)
//            }
        }

        override fun getItemCount() = datas.size

        inner class EventDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var name = itemView.findViewById<TextView>(R.id.detail_name)
            var type = itemView.findViewById<TextView>(R.id.detail_type)
            var memo = itemView.findViewById<TextView>(R.id.detail_memo)
            var date = itemView.findViewById<TextView>(R.id.detail_date)
            var place = itemView.findViewById<TextView>(R.id.detail_place)

        }
    }
}
