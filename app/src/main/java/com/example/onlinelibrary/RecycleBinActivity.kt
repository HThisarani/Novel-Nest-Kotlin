package com.example.onlinelibrary

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class RecycleBinActivity : AppCompatActivity() {

    private lateinit var bookListView: ListView
    private val deletedBooks = ArrayList<Book>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycle_bin)

        bookListView = findViewById(R.id.recycle_book_list)

        loadDeletedBooksFromDatabase()

        val backButton: Button = findViewById(R.id.backButton1)
        backButton.setOnClickListener {
            goBackToLibraryViewActivity()
        }
    }

    private fun loadDeletedBooksFromDatabase() {
        val db = openOrCreateDatabase("LibraryDb", MODE_PRIVATE, null)
        val cursor = db.rawQuery("SELECT * FROM deleted_books", null)

        val idIndex = cursor.getColumnIndex("id")
        val titleIndex = cursor.getColumnIndex("title")
        val authorIndex = cursor.getColumnIndex("author")
        val genreIndex = cursor.getColumnIndex("genre")
        val feeIndex = cursor.getColumnIndex("fee")

        deletedBooks.clear()

        if (cursor.moveToFirst()) {
            do {
                val book = Book(
                    cursor.getString(idIndex),
                    cursor.getString(titleIndex),
                    cursor.getString(authorIndex),
                    cursor.getString(genreIndex),
                    cursor.getString(feeIndex)
                )
                deletedBooks.add(book)
            } while (cursor.moveToNext())
        }

        val adapter = BookListAdapter(this, R.layout.list_item_layout, deletedBooks)
        bookListView.adapter = adapter

        cursor.close()
    }

    private fun goBackToLibraryViewActivity() {
        val intent = Intent(this, LibraryViewActivity::class.java)
        startActivity(intent)
        finish()
    }
}
