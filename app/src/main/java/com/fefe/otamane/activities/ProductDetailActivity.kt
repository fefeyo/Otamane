package com.fefe.otamane.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fefe.otamane.R
import com.fefe.otamane.fragments.EventDetailFragment
import com.fefe.otamane.fragments.ProductDetailFragment
import com.fefe.otamane.fragments.ProductListFragment
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_product_detail.*

class ProductDetailActivity : AppCompatActivity(), ProductListFragment.OnChooseProductListener, ProductDetailFragment.OnSelectEvent {

    companion object {
        var realm : Realm? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        setSupportActionBar(detail_toolbar)
        val eventId = intent.getLongExtra("eventId", -1)
        realm = Realm.getDefaultInstance()

        if(eventId == -1L) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.detail_container, ProductListFragment.getInstance())
                    .commit()
        }else {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.detail_container, EventDetailFragment.getInstance(eventId))
                    .commit()        }
    }

    override fun onChoose(productId: Long) {
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.detail_container, ProductDetailFragment.getInstance(productId))
                .addToBackStack("product_detail")
                .commit()
    }

    override fun onSelected(eventId: Long) {
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.detail_container, EventDetailFragment.getInstance(eventId))
                .addToBackStack("event_detail")
                .commit()
    }


    override fun onResume() {
        super.onResume()
        realm = Realm.getDefaultInstance()
    }

    override fun onPause() {
        super.onPause()
        realm?.close()
    }


}
