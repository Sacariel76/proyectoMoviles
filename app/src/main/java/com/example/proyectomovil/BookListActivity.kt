package com.example.proyectomovil

import Controller.BookController
import Entity.Book
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import androidx.appcompat.app.AppCompatActivity

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


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        loadBooks()
    }

    //Load books in the activity
    private fun loadBooks() {
        tableBooks.removeAllViews()

        //Get all books in list
        val books: List<Book> = try {
            bookController.getAll()
        } catch (e: Exception) {
            emptyList()
        }

        //Create a button for each book
        for (book in books) {
            tableBooks.addView(makeBookRow(book))
        }
    }

    //Create a new row in the table for each book found
    private fun makeBookRow(book: Book): View {
        val row = TableRow(this).apply {
            val pad = dp(6)
            setPadding(pad, pad, pad, pad)
        }

        val btn = Button(this).apply {
            text = "${book.Name}\n${book.Author}"
            isAllCaps = false
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.START
            setPadding(dp(16), dp(12), dp(16), dp(12))
            // ancho match, alto wrap dentro de la fila
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, dp(8), 0, dp(8))
            }

            setOnClickListener {
                val intent = Intent(this@BookListActivity, BookInfoActivity::class.java)
                intent.putExtra("BOOK_ID", book.Id)
                startActivity(intent)
            }
        }

        row.addView(btn)
        return row
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}
