package com.fefe.otamane.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.ArrayAdapter
import android.widget.TextView
import com.baoyz.swipemenulistview.SwipeMenuItem
import com.bumptech.glide.Glide
import com.fefe.otamane.R
import com.fefe.otamane.activities.AddscheduleActivity
import com.fefe.otamane.activities.OnlineEventGetActivity
import com.fefe.otamane.datas.Event
import com.fefe.otamane.datas.Product
import com.fefe.otamane.fragments.dialog.EventEditDialogFragment
import com.fefe.otamane.fragments.dialog.EventInputDialogFragment
import com.fefe.otamane.fragments.dialog.EventListDialogFragment
import io.realm.Realm
import io.realm.kotlin.deleteFromRealm
import kotlinx.android.synthetic.main.fragment_add_events.*
import java.text.SimpleDateFormat

class AddEventsFragment : Fragment(), EventListDialogFragment.OnChooseTypeListener, EventInputDialogFragment.OnCommitEventListener, EventEditDialogFragment.OnEditEventListener {

    private var product: Product? = null
    private var adapter: AddEventListAdapter? = null
    private var realm: Realm? = null
    private var addEvents = arrayListOf<Event>()

    companion object {
        fun getInstance(productId: Long) = AddEventsFragment().apply {
            arguments = Bundle().apply {
                putLong("productId", productId)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        realm = AddscheduleActivity.realm
        product = realm?.copyFromRealm(realm?.where(Product::class.java)?.equalTo("id", arguments?.getLong("productId"))?.findFirst())
        (activity as AddscheduleActivity).supportActionBar?.title = product?.name + "のイベント追加"
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
        syncAdapter()
        add_event_list.setOnItemClickListener { parent, view, position, id ->
            val event = parent.getItemAtPosition(position) as Event
            addEvents.remove(event)
            EventEditDialogFragment.getInstance(event.id).show(childFragmentManager, "eventedit")
        }
        add_event_list.setOnMenuItemClickListener { position, menu, index ->
            realm = AddscheduleActivity.realm
            realm?.executeTransaction {
                val event = realm?.copyToRealmOrUpdate(adapter?.getItem(position))
                event?.deleteFromRealm()
                addEvents.remove(adapter?.getItem(position))
            }
            syncAdapter()
            true
        }
    }

    private fun syncAdapter() {
        if (addEvents.isEmpty()!!) {
            add_events_empty.visibility = View.VISIBLE
        } else {
            add_events_empty.visibility = View.GONE
        }
        adapter?.clear()
        adapter?.addAll(addEvents)
        adapter?.notifyDataSetChanged()
    }

    //    イベントタイプ選択
    override fun onChoose(type: Int) {
        if(type == 100) {
            startActivity(Intent(activity?.applicationContext, OnlineEventGetActivity::class.java))
        }else {
            EventInputDialogFragment.getInstance(product?.id!!, type).show(childFragmentManager, "eventinput")
        }
    }

    //    イベント入力完了
    override fun onCommit(event: Event) {
        addEvents.add(event)
        syncAdapter()
    }


    override fun onEdit(event: Event) {
        addEvents.add(event)
        syncAdapter()
    }


    inner class AddEventListAdapter(context: Context) : ArrayAdapter<Event>(context, 0) {
        private val sdf = SimpleDateFormat("yyyy年MM月dd日")

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
            vh?.eventDate?.text = sdf.format(item.date)

            return v!!
        }
    }
}