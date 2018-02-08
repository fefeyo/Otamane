package com.fefe.otamane.activities

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.fefe.otamane.R
import com.fefe.otamane.datas.Event
import com.fefe.otamane.datas.Product
import com.getbase.floatingactionbutton.FloatingActionButton
import com.roomorama.caldroid.CaldroidFragment
import com.roomorama.caldroid.CaldroidListener
import io.realm.Realm
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class MainActivity : AppCompatActivity() {
    private var caldroid: CaldroidFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val cal = Calendar.getInstance()
        setDaySchedule(cal)
        caldroid = CaldroidFragment()

        caldroid?.arguments = Bundle().apply {
            putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1)
            putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR))
        }

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.calendar_container, caldroid)
            commit()
        }

        initSchedule()
        initButtons()

        val drawerToggle = ActionBarDrawerToggle(
                this,
                main_drawer,
                toolbar,
                R.string.open,
                R.string.close
        )
        main_drawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.week_schedule -> {
                    Log.d("週間スケジュール", "だよ")
                    true
                }
                R.id.product_schedule -> {
                    startActivity(Intent(applicationContext, ProductDetailActivity::class.java))
                    true
                }
                R.id.goods_list -> {
                    Log.d("グッズ一覧", "だよ")
                    true
                }
                R.id.money_setting -> {
                    Log.d("予算設定", "だよ")
                    true
                }
                R.id.product_schedule -> {
                    Log.d("ヘッダー画像の変更", "だよ")
                    true
                }
            }
            false
        }
        caldroid?.caldroidListener = object : CaldroidListener() {
            override fun onSelectDate(date: Date, view: View) {
                val cal = Calendar.getInstance()
                cal.time = date
                setDaySchedule(cal)
            }
        }
        day_schedule.addItemDecoration(DividerItemDecoration(day_schedule.context, DividerItemDecoration.VERTICAL))
    }

    override fun onResume() {
        super.onResume()
        initSchedule()
    }

    private fun initSchedule() {
            Realm.getDefaultInstance().use {
                val events = it.where(Event::class.java)
                        .sort("date", Sort.DESCENDING)
                        .findAll()
                println(events)
                val dateSet = HashMap<Date, ArrayList<Event>>()
                // ループを回してTreeSet<date, ArrayList<Event>>を作成
                // TreeSetのループを回してフラグを回収していく
                events.forEach {
                    if (dateSet.containsKey(it.date)) {
                        dateSet[it.date]?.add(it)
                    } else {
                        dateSet?.put(it.date!!, arrayListOf(it))
                    }
                }
                val scheduleMap = mutableMapOf<Date, Drawable>()
                dateSet.forEach { date, events ->
                    val typeSet = HashSet<Int>()
                    events.forEach {
                        typeSet.add(it.eventType)
                    }
                    var typeSum = 0
                    typeSet.forEach { typeSum += it }
                    scheduleMap.put(date, when (typeSum) {
                        1 -> getDrawable(R.drawable.event_live)
                        3 -> getDrawable(R.drawable.event_cd)
                        5 -> getDrawable(R.drawable.event_general)
                        6 -> getDrawable(R.drawable.event_live_general)
                        8 -> getDrawable(R.drawable.event_general_cd)
                        4 -> getDrawable(R.drawable.event_live_cd)
                        9 -> getDrawable(R.drawable.event_all)
                        else -> getDrawable(R.drawable.event_all)
                    })
                }
                caldroid?.setBackgroundDrawableForDates(scheduleMap)
                caldroid?.refreshView()
            }
        }

        fun setDaySchedule(dayCal: Calendar) {
            val cal = Calendar.getInstance()
            cal.clear()
            cal.set(dayCal.get(Calendar.YEAR), dayCal.get(Calendar.MONTH), dayCal.get(Calendar.DATE))

            Realm.getDefaultInstance().use {
                val dayEvents = it.where(Event::class.java).equalTo("date", cal.time).findAll()
                val unmanagedDayEvents = it.copyFromRealm(dayEvents)
                if(unmanagedDayEvents.isNotEmpty()) {
                    val adapter = DayEventListAdapter(applicationContext, unmanagedDayEvents.toTypedArray())
                    day_schedule.adapter = adapter
                    day_schedule.visibility = View.VISIBLE
                    day_event_empty.visibility = View.GONE
                }else {
                    day_schedule.visibility = View.GONE
                    day_event_empty.visibility = View.VISIBLE
                }
            }
        }

        private fun initButtons() {
            floatingmenu.addButton(makeActionButton(
                    "スケジュール追加",
                    R.color.colorPrimary,
                    R.color.colorPrimaryDark,
                    R.drawable.ic_today_white_24dp,
                    View.OnClickListener {
                        val intent = Intent(applicationContext, AddscheduleActivity::class.java)
                        startActivity(intent)
                        floatingmenu.toggle()
                    }
            ))
            floatingmenu.addButton(makeActionButton(
                    "チケット情報追加",
                    R.color.colorPrimary,
                    R.color.colorPrimaryDark,
                    R.drawable.ic_bookmark_border_white_24dp,
                    View.OnClickListener { floatingmenu.toggle() }
            ))
        }

        private fun makeActionButton(title: String, colorNormal: Int, colorPressed: Int, iconRes: Int, listener: View.OnClickListener): FloatingActionButton {
            var colorNormal = colorNormal
            var colorPressed = colorPressed
            colorNormal = ContextCompat.getColor(applicationContext, colorNormal)
            colorPressed = ContextCompat.getColor(applicationContext, colorPressed)
            val button = FloatingActionButton(applicationContext)
            button.title = title
            button.size = FloatingActionButton.SIZE_NORMAL
            button.colorNormal = colorNormal
            button.colorPressed = colorPressed
            button.setIcon(iconRes)
            button.isStrokeVisible = true
            button.setOnClickListener(listener)

            return button
        }

        inner class DayEventListAdapter(val context: Context, val datas: Array<Event>) : RecyclerView.Adapter<DayEventListAdapter.DayEventViewHolder>() {

            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DayEventViewHolder {
                val v: View = LayoutInflater.from(parent?.context).inflate(R.layout.day_event_row, parent, false)
                val holder = DayEventViewHolder(v)
                v.setOnClickListener({

                })
                return holder
            }

            override fun onBindViewHolder(holder: DayEventViewHolder?, position: Int) {
                holder?.day_title?.text = datas[position].name
                holder?.day_place?.text = datas[position].place?.name
                Realm.getDefaultInstance().use {
                    val product = it.where(Product::class.java).equalTo("id", datas[position].productid).findFirst()
                    Glide.with(context).load(product?.image).into(holder?.day_image!!)
                }
                holder?.itemView?.setOnClickListener {
                    val intent = Intent(applicationContext, ProductDetailActivity::class.java)
                    intent.putExtra("eventId", datas[position].id)
                    startActivity(intent)
                }
            }

            override fun getItemCount() = datas.size

            inner class DayEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                var day_title = itemView.findViewById<TextView>(R.id.day_event_title)
                var day_place = itemView.findViewById<TextView>(R.id.day_event_place)
                var day_image = itemView.findViewById<ImageView>(R.id.day_event_image)
            }
        }
    }
