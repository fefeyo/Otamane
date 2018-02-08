package com.fefe.otamane.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import com.fefe.otamane.R
import com.fefe.otamane.datas.Product
import com.fefe.otamane.fragments.AddEventsFragment
import com.fefe.otamane.fragments.ProductListFragment
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_addschedule.*

class AddscheduleActivity : AppCompatActivity(), ProductListFragment.OnChooseProductListener {
    companion object {
        var realm :Realm? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addschedule)
        setSupportActionBar(toolbar)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.add_schedule_container, ProductListFragment.getInstance())
                .commit()
    }

    override fun onChoose(productId: Long) {
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.add_schedule_container, AddEventsFragment.getInstance(productId))
                .addToBackStack("add_product")
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
