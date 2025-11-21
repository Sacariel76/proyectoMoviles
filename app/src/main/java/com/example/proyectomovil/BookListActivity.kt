package com.example.proyectomovil

import Controller.BookController
import Entity.Book
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import android.widget.TextView

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
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, dp(8), 0, dp(8)) }
            gravity = Gravity.CENTER_VERTICAL
        }

        // Contenedor horizontal (imagen + texto)
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(8), dp(12), dp(8))
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
            setBackgroundResource(android.R.drawable.btn_default) // efecto de clic tipo botón
            isClickable = true
            isFocusable = true
        }

        // Miniatura
        val thumbSize = dp(56)
        val iv = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(thumbSize, thumbSize).apply {
                rightMargin = dp(12)
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
            setBackgroundColor(0xFFBDBDBD.toInt())
        }

        // Si tiene imagen guardada en el objeto Book
        book.Image?.let { bmp ->
            val scaled = Bitmap.createScaledBitmap(bmp, thumbSize, thumbSize, true)
            iv.setImageBitmap(scaled)
        }

        // Texto con nombre + autor
        val tv = TextView(this).apply {
            text = "${book.Name}\n${book.Author}"
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(0xFF000000.toInt())
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Click en toda la fila
        container.setOnClickListener {
            val intent = Intent(this@BookListActivity, BookInfoActivity::class.java)
            intent.putExtra("BOOK_ID", book.Id)
            startActivity(intent)
        }

        container.addView(iv)
        container.addView(tv)
        row.addView(container)
        return row
    }


    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}
