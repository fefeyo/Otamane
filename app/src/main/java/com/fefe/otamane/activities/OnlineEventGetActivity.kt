package com.fefe.otamane.activities

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.fefe.otamane.R
import com.fefe.otamane.datas.Event
import com.fefe.otamane.datas.Product
import com.fefe.otamane.datas.api.CDData
import com.fefe.otamane.datas.api.EventData
import com.google.gson.Gson
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.roomorama.caldroid.CaldroidFragment
import com.roomorama.caldroid.CaldroidListener
import cz.msebera.android.httpclient.Header
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_online_event_get.*
import java.util.*
import kotlin.collections.HashSet

class OnlineEventGetActivity : AppCompatActivity() {
    private var caldroid: CaldroidFragment? = null
    private var dataSet: HashMap<Date, ArrayList<CDData>> = HashMap()
    private var cdDatas = arrayOf<CDData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_event_get)

        val cal = Calendar.getInstance()
        caldroid = CaldroidFragment()

        caldroid?.arguments = Bundle().apply {
            putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1)
            putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR))
        }

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.api_calendar_container, caldroid)
            commit()
        }

        AsyncHttpClient().get(
                applicationContext,
                "http://192.168.100.102:2525/getCDSchedule/2018/2",
                object : AsyncHttpResponseHandler() {
                    override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                        val result = String(responseBody)
                        val gson = Gson()
                        cdDatas = gson.fromJson(result, Array<CDData>::class.java)
                        cdDatas.forEach {
                            val cal = Calendar.getInstance()
                            cal.clear()
                            cal.set(2018, 1, it.day)
                            if (dataSet.containsKey(cal.time)) {
                                dataSet[cal.time]?.add(it)
                            } else {
                                dataSet?.put(cal.time, arrayListOf(it))
                            }
                        }
                        val scheduleMap = mutableMapOf<Date, Drawable>()
                        dataSet.forEach { date, _ ->
                            scheduleMap.put(date, getDrawable(R.drawable.event_live))
                        }
                        caldroid?.setBackgroundDrawableForDates(scheduleMap)
                        caldroid?.refreshView()
                    }

                    override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                        error.printStackTrace()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        caldroid?.caldroidListener = object : CaldroidListener() {
                            override fun onSelectDate(date: Date, view: View) {
                                val dayCal = Calendar.getInstance()
                                dayCal.time = date
                                val schedule = cdDatas.filter {
                                    it.day == dayCal.get(Calendar.DATE)
                                }.toTypedArray()
                                if (schedule.isNotEmpty()) {
                                    val adapter = DayEventListAdapter(applicationContext, schedule!!)
                                    api_day_schedule.adapter = adapter
                                    api_day_schedule.visibility = View.VISIBLE
                                    api_day_event_empty.visibility = View.GONE
                                } else {
                                    api_day_schedule.visibility = View.GONE
                                    api_day_event_empty.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }
        )
    }

    inner class DayEventListAdapter(val context: Context, val datas: Array<CDData>) : RecyclerView.Adapter<DayEventListAdapter.DayEventViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DayEventViewHolder {
            val v: View = LayoutInflater.from(parent?.context).inflate(R.layout.day_event_row, parent, false)
            val holder = DayEventViewHolder(v)
            v.setOnClickListener({

            })
            return holder
        }

        override fun onBindViewHolder(holder: DayEventViewHolder?, position: Int) {
            holder?.day_title?.text = datas[position].title
            holder?.day_time?.text = ""
            Glide.with(context).load(datas[position].thumbnail).into(holder?.day_image!!)
            holder?.itemView?.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(datas[position].link)))
            }

        }

        override fun getItemCount() = datas.size

        inner class DayEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var day_title = itemView.findViewById<TextView>(R.id.day_event_title)
            var day_time = itemView.findViewById<TextView>(R.id.day_event_place)
            var day_image = itemView.findViewById<ImageView>(R.id.day_event_image)
        }
    }
}
