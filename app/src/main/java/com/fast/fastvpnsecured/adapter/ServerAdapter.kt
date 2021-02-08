package com.fast.fastvpnsecured.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fast.fastvpnsecured.R
import com.fast.fastvpnsecured.app.inflate
import com.fast.fastvpnsecured.app.setImage
import com.fast.fastvpnsecured.model.ProxyServer
import kotlinx.android.synthetic.main.server_view.view.*


class ServerAdapter(private val list: ArrayList<ProxyServer>, private val listener: (Int) -> Unit) :
    RecyclerView.Adapter<ServerAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(inflate(parent.context, R.layout.server_view, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(list[position], listener)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(proxyServer: ProxyServer, listener: (Int) -> Unit) = with(itemView) {
            setImage(proxyServer, country_img, itemView)

            if (proxyServer.current) {
                itemView.current_radio.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_radioon
                    )
                )
            }

            country_tv.text = proxyServer.name

            itemView.setOnClickListener { listener(adapterPosition) }
        }
    }


}
