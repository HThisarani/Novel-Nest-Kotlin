package com.example.onlinelibrary

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var titleEditText: EditText? = null
    private var authorEditText: EditText? = null
    private var genreEditText: EditText? = null
    private var feeEditText: EditText? = null
    private var addButton: Button? = null
    private var viewButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        titleEditText = findViewById(R.id.title)
        authorEditText = findViewById(R.id.author)
        genreEditText = findViewById(R.id.genre)
        feeEditText = findViewById(R.id.fee)
        addButton = findViewById(R.id.add_button)
        viewButton = findViewById(R.id.view_button)

        viewButton!!.setOnClickListener {
            val intent = Intent(this, LibraryViewActivity::class.java)
            startActivity(intent)
        }

        addButton!!.setOnClickListener { addBook() }
    }

    private fun addBook() {
        val title = titleEditText!!.text.toString().trim()
        val author = authorEditText!!.text.toString().trim()
        val genre = genreEditText!!.text.toString().trim()
        val feeStr = feeEditText!!.text.toString().trim()

        if (title.isEmpty() || author.isEmpty() || genre.isEmpty() || feeStr.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        val fee = try {
            feeStr.toInt() // Convert fee string to integer
        } catch (e: NumberFormatException) {
            // Show error message for invalid fee value
            feeEditText!!.error = "Invalid value"
            return
        }

        // Clear error message if fee is valid
        feeEditText!!.error = null

        try {
            val db: SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(this.getDatabasePath("LibraryDb"), null)
            db.execSQL("CREATE TABLE IF NOT EXISTS books(id INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR, author VARCHAR, genre VARCHAR, fee VARCHAR)")

            val sql = "INSERT INTO books(title, author, genre, fee) VALUES (?, ?, ?, ?)"
            val statement = db.compileStatement(sql)
            statement.bindString(1, title)
            statement.bindString(2, author)
            statement.bindString(3, genre)
            statement.bindString(4, fee.toString()) // Bind fee as string
            statement.execute()

            Toast.makeText(this, "Book added", Toast.LENGTH_LONG).show()
            titleEditText!!.setText("")
            authorEditText!!.setText("")
            genreEditText!!.setText("")
            feeEditText!!.setText("")
            titleEditText!!.requestFocus()
        } catch (ex: Exception) {
            Toast.makeText(this, "Failed to add book", Toast.LENGTH_LONG).show()
        }
    }
}
