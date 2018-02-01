package com.fefe.otamane.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.fefe.otamane.R
import com.fefe.otamane.datas.Product
import com.fefe.otamane.fragments.AddEventsFragment
import com.fefe.otamane.fragments.ProductDetailFragment
import com.fefe.otamane.fragments.ProductListFragment
import kotlinx.android.synthetic.main.activity_addschedule.*
import kotlinx.android.synthetic.main.activity_product_detail.*

class ProductDetailActivity : AppCompatActivity(), ProductListFragment.OnChooseProductListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        setSupportActionBar(detail_toolbar)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.detail_container, ProductListFragment.getInstance())
                .commit()
    }

    override fun onChoose(product: Product) {
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.detail_container, ProductDetailFragment.getInstance(product))
                .addToBackStack("product_detail")
                .commit()
    }

}
