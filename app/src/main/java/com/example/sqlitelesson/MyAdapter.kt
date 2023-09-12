package com.example.sqlitelesson

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sqlitelesson.DB.ListItem
import com.example.sqlitelesson.DB.MyDbManager
import com.example.sqlitelesson.DB.MyIntentConstants


class MyAdapter(listMain: ArrayList<ListItem>, contextM: Context): RecyclerView.Adapter<MyAdapter.MyHolder>() {
    var listArray = listMain
    var context = contextM

    class MyHolder(itemView: View, contextV: Context) : RecyclerView.ViewHolder(itemView){
        val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
        val tvDate = itemView.findViewById<TextView>(R.id.tvDate)
        val context = contextV

        fun setData(item: ListItem){
            tvTitle.text = item.title
            tvDate.text = item.time
            itemView.setOnClickListener {
                val intent = Intent(context,EditActivity::class.java).apply {

                    putExtra(MyIntentConstants.I_TITLE_KEY,item.title)
                    putExtra(MyIntentConstants.I_CONTENT_KEY,item.content)
                    putExtra(MyIntentConstants.I_URI_KEY,item.uri)
                    putExtra(MyIntentConstants.I_ID_KEY,item.id)

                }
                context.startActivity(intent)

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyHolder(inflater.inflate(R.layout.rc_item,parent,false),context)
    }

    override fun getItemCount(): Int {
        return listArray.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.setData(listArray.get(position))
    }

    fun updateAdapter(listItems:List<ListItem>){
        listArray.clear()
        listArray.addAll(listItems)
        notifyDataSetChanged()

    }
    fun removeItem(position: Int,dbManager: MyDbManager){

        dbManager.removeItemFromDb(listArray[position].id.toString())
        listArray.removeAt(position)
        notifyItemRangeChanged(0,listArray.size)
        notifyItemRemoved(position)

    }
}