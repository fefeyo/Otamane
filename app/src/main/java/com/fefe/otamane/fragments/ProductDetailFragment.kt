package com.fefe.otamane.fragments


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fefe.otamane.R
import com.fefe.otamane.activities.ProductDetailActivity
import com.fefe.otamane.datas.Event
import com.fefe.otamane.datas.Product
import kotlinx.android.synthetic.main.fragment_product_detail.*


/**
 * A simple [Fragment] subclass.
 */
class ProductDetailFragment : Fragment() {
    var product: Product? = null

    companion object {
        fun getInstance(product: Product) = ProductDetailFragment().apply {
            arguments = Bundle().apply {
                putSerializable("product", product)
            }
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_product_detail, container, false)
        product = arguments?.get("product") as Product
        (activity as ProductDetailActivity).supportActionBar?.title = product?.name + "のイベント一覧"

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val events = product?.events?.toTypedArray()!!
        if (events.isNotEmpty()) {
            detail_event_list.adapter = EventDetailListAdapter(activity?.applicationContext!!, events)
            detail_event_list.visibility = View.VISIBLE
            detail_event_empty.visibility = View.GONE
        } else {
            detail_event_list.visibility = View.GONE
            detail_event_empty.visibility = View.VISIBLE
        }
    }

    inner class EventDetailListAdapter(val context: Context, val datas: Array<Event>) : RecyclerView.Adapter<EventDetailListAdapter.EventDetailViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): EventDetailViewHolder {
            val v: View = LayoutInflater.from(parent?.context).inflate(R.layout.detail_event_row, parent, false)

            return EventDetailViewHolder(v)
        }

        override fun onBindViewHolder(holder: EventDetailViewHolder?, position: Int) {
            holder?.name?.text = datas[position].name
            holder?.type?.text = datas[position].eventType.toString()
            holder?.memo?.text = datas[position].memo
            holder?.date?.text = datas[position].date.toString()
        }

        override fun getItemCount() = datas.size

        inner class EventDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var name = itemView.findViewById<TextView>(R.id.detail_name)
            var type = itemView.findViewById<TextView>(R.id.detail_type)
            var memo = itemView.findViewById<TextView>(R.id.detail_memo)
            var date = itemView.findViewById<TextView>(R.id.detail_date)
        }
    }
}
