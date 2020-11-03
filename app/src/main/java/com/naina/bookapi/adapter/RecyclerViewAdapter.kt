package com.naina.bookapi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.naina.bookapi.R
import com.naina.bookapi.model.Book
import com.squareup.picasso.Picasso


class RecyclerViewAdapter(val context: Context, val list: ArrayList<Book>) :
    RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        i: Int
    ): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.book_raw_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, i: Int) {
        val book: Book = list[i]
        holder.tvTitle.setText(book.title)
        holder.tvAuthor.setText(book.author)
        Picasso.get().load(book.thumbnail).error(R.drawable.ic_thumbnail).into(holder.ivThumbnail)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(itemView: View) : ViewHolder(itemView) {
        var ivThumbnail: ImageView = itemView.findViewById(R.id.thumbnail)
        var tvTitle: TextView = itemView.findViewById(R.id.title)
        var tvAuthor: TextView = itemView.findViewById(R.id.author)
    }
}