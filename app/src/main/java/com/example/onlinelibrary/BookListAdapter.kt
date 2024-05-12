package com.example.onlinelibrary

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView

class BookListAdapter(
    context: Context,
    private val resource: Int,
    private var originalBooks: List<Book>
) : ArrayAdapter<Book>(context, resource, originalBooks) {
    private var filteredBooks = ArrayList<Book>()

    init {
        filteredBooks.addAll(originalBooks)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        val holder: ViewHolder

        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(resource, parent, false)
            holder = ViewHolder()
            holder.bookText = itemView.findViewById(R.id.book_text)
            holder.editButton = itemView.findViewById(R.id.edit_button)
            itemView.tag = holder
        } else {
            holder = itemView.tag as ViewHolder
        }

        val book = filteredBooks[position]
        holder.bookText?.text = "${book.id} \t ${book.title} \t ${book.author} \t ${book.genre} \t ${book.fee}"

        holder.editButton?.setOnClickListener {
            val intent = Intent(context, LibraryEditActivity::class.java).apply {
                putExtra("id", book.id)
                putExtra("title", book.title)
                putExtra("author", book.author)
                putExtra("genre", book.genre)
                putExtra("fee", book.fee)
            }
            context.startActivity(intent)
        }

        return itemView!!
    }

    fun filter(query: String) {
        filteredBooks.clear()
        if (query.isEmpty()) {
            filteredBooks.addAll(originalBooks)
        } else {
            for (book in originalBooks) {
                if (book.author?.contains(query, ignoreCase = true) == true) {
                    filteredBooks.add(book)
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return filteredBooks.size
    }

    private class ViewHolder {
        var bookText: TextView? = null
        var editButton: Button? = null
    }
}
