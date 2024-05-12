package com.example.onlinelibrary

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.database.sqlite.SQLiteDatabase

class LibraryEditActivity : AppCompatActivity() {
    private var titleEditText: EditText? = null
    private var authorEditText: EditText? = null
    private var genreEditText: EditText? = null
    private var feeEditText: EditText? = null
    private var saveButton: Button? = null
    private var deleteButton: Button? = null
    private var backButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library_edit)

        titleEditText = findViewById(R.id.title_edit)
        authorEditText = findViewById(R.id.author_edit)
        genreEditText = findViewById(R.id.genre_edit)
        feeEditText = findViewById(R.id.fee_edit)
        saveButton = findViewById(R.id.save_button)
        deleteButton = findViewById(R.id.delete_button)
        backButton = findViewById(R.id.back_button)

        val intent = intent
        val id = intent.getStringExtra("id").toString()
        val title = intent.getStringExtra("title").toString()
        val author = intent.getStringExtra("author").toString()
        val genre = intent.getStringExtra("genre").toString()
        val fee = intent.getStringExtra("fee").toString()

        titleEditText!!.setText(title)
        authorEditText!!.setText(author)
        genreEditText!!.setText(genre)
        feeEditText!!.setText(fee)

        deleteButton!!.setOnClickListener { deleteBook(id) }
        backButton!!.setOnClickListener { onBackPressed() }
        saveButton!!.setOnClickListener { editBook(id) }
    }

    override fun onBackPressed() {
        // Set the result to Activity.RESULT_OK before finishing the activity
        setResult(Activity.RESULT_OK)
        super.onBackPressed()
    }

    @SuppressLint("Range")
    private fun deleteBook(id: String) {
        try {
            val db = openOrCreateDatabase("LibraryDb", Context.MODE_PRIVATE, null)
            val cursor = db.rawQuery("SELECT * FROM books WHERE id = ?", arrayOf(id))

            if (cursor.moveToFirst()) {
                val title = cursor.getString(cursor.getColumnIndex("title"))
                val author = cursor.getString(cursor.getColumnIndex("author"))
                val genre = cursor.getString(cursor.getColumnIndex("genre"))
                val fee = cursor.getString(cursor.getColumnIndex("fee"))

                val insertSql = "INSERT INTO deleted_books(title, author, genre, fee) VALUES (?, ?, ?, ?)"
                val insertStatement = db.compileStatement(insertSql)
                insertStatement.bindString(1, title)
                insertStatement.bindString(2, author)
                insertStatement.bindString(3, genre)
                insertStatement.bindString(4, fee)
                insertStatement.execute()
            }

            cursor.close()

            val deleteSql = "DELETE FROM books WHERE id = ?"
            val deleteStatement = db.compileStatement(deleteSql)
            deleteStatement.bindString(1, id)
            deleteStatement.execute()

            Toast.makeText(this, "Book moved to recycle bin", Toast.LENGTH_LONG).show()
            setResult(Activity.RESULT_OK)
            finish()
        } catch (ex: Exception) {
            Toast.makeText(this, "Failed to delete book", Toast.LENGTH_LONG).show()
        }
    }

    private fun editBook(id: String) {
        try {
            val title = titleEditText!!.text.toString()
            val author = authorEditText!!.text.toString()
            val genre = genreEditText!!.text.toString()
            val fee = feeEditText!!.text.toString()

            val db = openOrCreateDatabase("LibraryDb", Context.MODE_PRIVATE, null)
            val sql = "UPDATE books SET title = ?, author = ?, genre = ?, fee = ? WHERE id = ?"
            val statement = db.compileStatement(sql)
            statement.bindString(1, title)
            statement.bindString(2, author)
            statement.bindString(3, genre)
            statement.bindString(4, fee)
            statement.bindString(5, id)
            statement.execute()

            Toast.makeText(this, "Book updated", Toast.LENGTH_LONG).show()
            setResult(Activity.RESULT_OK)
        } catch (ex: Exception) {
            Toast.makeText(this, "Failed to update book", Toast.LENGTH_LONG).show()
        }
    }
}
