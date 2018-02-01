package com.fefe.otamane.fragments

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import com.bumptech.glide.Glide

import com.fefe.otamane.R
import com.fefe.otamane.activities.AddscheduleActivity
import com.fefe.otamane.datas.Product
import kotlinx.android.synthetic.main.fragment_add_events.*
import android.view.KeyEvent.KEYCODE_BACK
import android.widget.ArrayAdapter
import android.widget.TextView
import com.baoyz.swipemenulistview.SwipeMenuItem
import com.fefe.otamane.fragments.dialog.EventListDialogFragment
import com.bumptech.glide.request.RequestOptions.centerCropTransform
import com.fefe.otamane.datas.Event
import com.fefe.otamane.fragments.dialog.EventInputDialogFragment
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_product_list.*
import org.w3c.dom.Text
import java.text.SimpleDateFormat

class AddEventsFragment : Fragment(), EventListDialogFragment.OnChooseTypeListener, EventInputDialogFragment.OnCommitEventListener {
    private var product: Product? = null
    private val eventList = ArrayList<Event>()
    private var adapter: AddEventListAdapter? = null

    companion object {
        fun getInstance(product: Product) = AddEventsFragment().apply {
            arguments = Bundle().apply {
                putSerializable("product", product)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        (activity as AddscheduleActivity).supportActionBar?.title = "イベント追加"
        product = arguments?.getSerializable("product") as Product
        adapter = AddEventListAdapter(activity?.applicationContext!!)
        return inflater.inflate(R.layout.fragment_add_events, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.add_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.add -> {
                EventListDialogFragment.getInstance().show(childFragmentManager, "list")
                true
            }
            else -> false
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        event_title.text = product?.name
        Glide.with(activity?.applicationContext!!)
                .load(product?.image)
                .into(event_image)
        add_event_list.setMenuCreator { menu ->
            val deleteMenu = SwipeMenuItem(activity?.applicationContext!!)
            deleteMenu.background = (ColorDrawable(Color.parseColor("#e84118")))
            deleteMenu.width = 200
            deleteMenu.title = "削除"
            deleteMenu.titleSize = 20
            deleteMenu.titleColor = Color.WHITE
            menu.addMenuItem(deleteMenu)
        }
        add_event_list.adapter = adapter
        initAdapter()
    }

    private fun initAdapter() {
        Realm.getDefaultInstance().use {
            val events = it.where(Product::class.java).equalTo("id", product?.id).findFirst()?.events
            val unmanagedEvents = it.copyFromRealm(events)
            if (events?.isEmpty()!!) {
                add_events_empty.visibility = View.VISIBLE
            } else {
                add_events_empty.visibility = View.GONE
            }
            adapter?.clear()
            adapter?.addAll(unmanagedEvents)
            adapter?.notifyDataSetChanged()
        }
    }

    //    イベントタイプ選択
    override fun onChoose(type: Int) {
        EventInputDialogFragment.getInstance(product?.id!!, type).show(childFragmentManager, "eventinput")
    }

    //    イベント入力完了
    override fun onCommit(event: Event) {
        initAdapter()
    }


    inner class AddEventListAdapter(context: Context) : ArrayAdapter<Event>(context, 0) {
        inner class ViewHolder {
            var eventTitle: TextView? = null
            var eventPlace: TextView? = null
            var eventDate: TextView? = null
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var v = convertView
            var vh: ViewHolder?
            if (v == null) {
                v = layoutInflater.inflate(R.layout.add_event_list_row, null)
                vh = ViewHolder()
                vh?.eventTitle = v?.findViewById<TextView>(R.id.add_event_title)
                vh?.eventPlace = v?.findViewById<TextView>(R.id.add_event_place)
                vh?.eventDate = v?.findViewById<TextView>(R.id.add_event_date)
                v?.tag = vh
            } else {
                vh = v.tag as ViewHolder
            }
            val item = getItem(position)
            vh?.eventTitle?.text = item.name
            vh?.eventPlace?.text = item.place?.name
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            vh?.eventDate?.text = sdf.format(item.date)

            return v!!
        }
    }
}