package com.fefe.otamane.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.fefe.otamane.R
import com.fefe.otamane.activities.AddscheduleActivity
import com.fefe.otamane.activities.ProductDetailActivity
import com.fefe.otamane.datas.Product
import com.fefe.otamane.fragments.dialog.ProductAddDialogFragment
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_product_list.*

class ProductListFragment : Fragment(), ProductAddDialogFragment.OnProductCommitListener {
    private var listener: OnChooseProductListener? = null

    interface OnChooseProductListener {
        fun onChoose(product: Product)
    }

    companion object {
        fun getInstance() = ProductListFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if(activity is AddscheduleActivity)setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = "作品・アーティスト一覧"
        return inflater.inflate(R.layout.fragment_product_list, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = activity as OnChooseProductListener
        if (listener !is OnChooseProductListener) throw ClassCastException("リスナー登録失敗")
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.add_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.add -> {
                ProductAddDialogFragment.getInstance().show(childFragmentManager, "作品追加")
                true
            }
            else -> false
        }
    }

    override fun onCommit() {
        initAdapter()
    }

    private fun initAdapter() {
        Realm.getDefaultInstance().use {
            val products = it.where(Product::class.java).findAll()
            val unmanagedProducts = it.copyFromRealm(products)
            if (products.isEmpty()) {
                empty.visibility = View.VISIBLE
            } else {
                empty.visibility = View.GONE
            }
            product_list.adapter = ProductListAdapter(activity?.applicationContext!!, unmanagedProducts.toTypedArray())

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        product_list.setHasFixedSize(true)
        initAdapter()
    }

    inner class ProductListAdapter(val context: Context, val datas: Array<Product>) : RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ProductListViewHolder {
            val v: View = LayoutInflater.from(parent?.context).inflate(R.layout.product_list_row, parent, false)
            val holder = ProductListViewHolder(v)
            v.setOnClickListener({
                listener?.onChoose(datas[holder.adapterPosition])
            })
            return holder
        }

        override fun onBindViewHolder(holder: ProductListViewHolder?, position: Int) {
            holder?.title?.text = datas[position].name
            Glide.with(context).load(datas[position].image).into(holder?.image!!)
        }

        override fun getItemCount() = datas.size

        inner class ProductListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var title = itemView.findViewById<TextView>(R.id.product_title)
            var image = itemView.findViewById<ImageView>(R.id.product_image)
        }
    }
}