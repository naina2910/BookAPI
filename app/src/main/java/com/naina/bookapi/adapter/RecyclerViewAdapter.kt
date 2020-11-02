package com.naina.bookapi.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.naina.bookapi.R
import com.naina.bookapi.model.Book
import com.squareup.picasso.Picasso


class RecyclerViewAdapter(val mContext: Context, val mdata: ArrayList<Book>) :
    RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {
    /* private val mData: List<Book>
     private val options: RequestOptions*/
    override fun onCreateViewHolder(
        parent: ViewGroup,
        i: Int
    ): MyViewHolder {
        val view=LayoutInflater.from(mContext).inflate(R.layout.book_raw_item,parent,false)
        /*val viewHolder = MyViewHolder(view)
        viewHolder.container.setOnClickListener {
            val i = Intent(mContext, BookActivity::class.java)
            val pos = viewHolder.adapterPosition
            i.putExtra("book_title", mData[pos].getTitle())
            i.putExtra("book_author", mData[pos].getAuthors())
            i.putExtra("book_desc", mData[pos].getDescription())
            i.putExtra("book_categories", mData[pos].getCategories())
            i.putExtra("book_publish_date", mData[pos].getPublishedDate())
            i.putExtra("book_info", mData[pos].getmUrl())
            i.putExtra("book_buy", mData[pos].getBuy())
            i.putExtra("book_preview", mData[pos].getPerview())
            i.putExtra("book_thumbnail", mData[pos].getThumbnail())
            mContext.startActivity(i)
        }
        return viewHolder*/
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        i: Int
    ) {
        val book: Book = mdata[i]
        holder.tvTitle.setText(book.title)
        holder.tvAuthor.setText(book.authors)
        holder.tvPrice.setText(book.price)
        holder.tvCategory.setText(book.categories)
        Picasso.get().load(book.thumbnail).error(R.drawable.ic_thumbnail).into(holder.ivThumbnail)
    }

    override fun getItemCount(): Int {
        return mdata.size
    }

    class MyViewHolder(itemView: View) : ViewHolder(itemView) {
        var ivThumbnail: ImageView = itemView.findViewById(R.id.thumbnail)
        var tvTitle: TextView = itemView.findViewById(R.id.title)
        var tvCategory: TextView = itemView.findViewById(R.id.category)
        var tvPrice: TextView = itemView.findViewById(R.id.price)
        var tvAuthor: TextView = itemView.findViewById(R.id.author)
        /*var container: LinearLayout = itemView.findViewById(R.id.container)*/
    }
}