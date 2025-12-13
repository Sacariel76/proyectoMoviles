package com.example.proyectomovil

import Controller.BookController
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import api.ApiClient
import api.BookApiModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookListActivity : AppCompatActivity() {

    private lateinit var tableBooks: TableLayout
    private lateinit var bookController: BookController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        bookController = BookController(this)
        tableBooks = findViewById(R.id.tableBooks)

        loadBooks()
    }

    // Loads all the existing books
    private fun loadBooks() {
        tableBooks.removeAllViews()

        ApiClient.bookApi.getBooks()
            .enqueue(object : Callback<List<BookApiModel>> {
                override fun onResponse(
                    call: Call<List<BookApiModel>>,
                    response: Response<List<BookApiModel>>
                ) {
                    if (!response.isSuccessful || response.body() == null) {
                        Toast.makeText(
                            this@BookListActivity,
                            getString(R.string.ErrorMsgGetAll),
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    val books = response.body()!!

                    if (books.isEmpty()) {
                        Toast.makeText(
                            this@BookListActivity,
                            getString(R.string.MsgDataNotFound),
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    for (book in books) {
                        val id = book.id ?: continue   // if id is null, skip the row
                        val name = book.name.ifBlank { getString(R.string.MsgDataNotFound) }
                        val image = book.imageUrl ?: ""

                        addBookRow(id, name, image)
                    }
                }

                override fun onFailure(call: Call<List<BookApiModel>>, t: Throwable) {
                    Toast.makeText(
                        this@BookListActivity,
                        getString(R.string.ApiErrorConnection) + ": ${t.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // Add rows with buttons to the activity with all the listed categories
    private fun addBookRow(docId: String, bookName: String, imageUrl: String) {

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(24, 24, 24, 24)
        }

        val img = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(120, 120)
        }

        Glide.with(this)
            .load(imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .skipMemoryCache(true)                         // No cache in RAM
            .diskCacheStrategy(DiskCacheStrategy.NONE)     // No cache in disk
            .into(img)

        val txt = TextView(this).apply {
            text = bookName
            textSize = 18f
            setPadding(24, 0, 0, 0)
        }

        row.addView(img)
        row.addView(txt)

        row.setOnClickListener {
            openBookInfo(docId)
        }

        tableBooks.addView(row)
    }

    // Opens the BookInfoActivity with the ID of the selected book to load the data in the next activity
    private fun openBookInfo(docId: String) {
        val intent = Intent(this, BookInfoActivity::class.java)
        intent.putExtra("bookDocId", docId)
        startActivity(intent)
    }
}
