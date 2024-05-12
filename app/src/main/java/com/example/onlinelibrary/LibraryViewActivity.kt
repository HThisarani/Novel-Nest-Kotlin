package com.example.onlinelibrary

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher

class LibraryViewActivity : AppCompatActivity() {
    private var bookListView: ListView? = null
    private var allBooks = ArrayList<Book>()
    private var deletedBooksVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library_view)

        bookListView = findViewById(R.id.book_list)

        // Setup the back button
        val backButton: Button = findViewById(R.id.button3)
        backButton.setOnClickListener {
            goBackToMainActivity()
        }

        val searchEditText: EditText = findViewById(R.id.searchEditText)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString()
                if (deletedBooksVisible) {
                    loadDeletedBooks(searchText)
                } else {
                    searchByAuthor(searchText)
                }
            }
        })

        loadDataFromDatabase()

        bookListView?.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedBook: Book = allBooks[position]
            val intent = Intent(this@LibraryViewActivity, LibraryEditActivity::class.java)
            intent.putExtra("id", selectedBook.id)
            intent.putExtra("title", selectedBook.title)
            intent.putExtra("author", selectedBook.author)
            intent.putExtra("genre", selectedBook.genre)
            intent.putExtra("fee", selectedBook.fee)
            startActivityForResult(intent, EDIT_REQUEST_CODE)
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload the data from the database whenever the activity is resumed
        if (deletedBooksVisible) {
            loadDeletedBooks()
        } else {
            loadDataFromDatabase()
        }
    }

    private fun loadDataFromDatabase() {
        val db = openOrCreateDatabase("LibraryDb", MODE_PRIVATE, null)
        val cursor = db.rawQuery("SELECT * FROM books", null)

        val idIndex = cursor.getColumnIndex("id")
        val titleIndex = cursor.getColumnIndex("title")
        val authorIndex = cursor.getColumnIndex("author")
        val genreIndex = cursor.getColumnIndex("genre")
        val feeIndex = cursor.getColumnIndex("fee")

        allBooks.clear()

        if (cursor.moveToFirst()) {
            do {
                val book = Book(
                    cursor.getString(idIndex),
                    cursor.getString(titleIndex),
                    cursor.getString(authorIndex),
                    cursor.getString(genreIndex),
                    cursor.getString(feeIndex)
                )
                allBooks.add(book)
            } while (cursor.moveToNext())
        }

        val adapter = BookListAdapter(this, R.layout.list_item_layout, allBooks)
        bookListView?.adapter = adapter

        cursor.close()
    }

    private fun searchByAuthor(author: String) {
        val db = openOrCreateDatabase("LibraryDb", MODE_PRIVATE, null)
        val cursor = db.rawQuery("SELECT * FROM books WHERE author LIKE '%$author%'", null)

        val idIndex = cursor.getColumnIndex("id")
        val titleIndex = cursor.getColumnIndex("title")
        val authorIndex = cursor.getColumnIndex("author")
        val genreIndex = cursor.getColumnIndex("genre")
        val feeIndex = cursor.getColumnIndex("fee")

        allBooks.clear()

        if (cursor.moveToFirst()) {
            do {
                val book = Book(
                    cursor.getString(idIndex),
                    cursor.getString(titleIndex),
                    cursor.getString(authorIndex),
                    cursor.getString(genreIndex),
                    cursor.getString(feeIndex)
                )
                allBooks.add(book)
            } while (cursor.moveToNext())
        }

        val adapter = BookListAdapter(this, R.layout.list_item_layout, allBooks)
        bookListView?.adapter = adapter

        cursor.close()
    }

    private fun loadDeletedBooks(searchText: String = "") {
        val db = openOrCreateDatabase("LibraryDb", MODE_PRIVATE, null)
        val cursor = db.rawQuery("SELECT * FROM deleted_books WHERE author LIKE '%$searchText%'", null)

        val idIndex = cursor.getColumnIndex("id")
        val titleIndex = cursor.getColumnIndex("title")
        val authorIndex = cursor.getColumnIndex("author")
        val genreIndex = cursor.getColumnIndex("genre")
        val feeIndex = cursor.getColumnIndex("fee")

        allBooks.clear()

        if (cursor.moveToFirst()) {
            do {
                val book = Book(
                    cursor.getString(idIndex),
                    cursor.getString(titleIndex),
                    cursor.getString(authorIndex),
                    cursor.getString(genreIndex),
                    cursor.getString(feeIndex)
                )
                allBooks.add(book)
            } while (cursor.moveToNext())
        }

        val adapter = BookListAdapter(this, R.layout.list_item_layout, allBooks)
        bookListView?.adapter = adapter

        cursor.close()
    }

    fun toggleDeletedBooksVisibility(view: View) {
        val intent = Intent(this, RecycleBinActivity::class.java)
        startActivity(intent)
    }

    fun goBackToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val EDIT_REQUEST_CODE = 1001
    }
}
