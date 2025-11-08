package com.example.proyectomovil

import Controller.BookController
import Entity.Book
import Util.Util
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.time.LocalDate
import java.util.Calendar

class BookInfoActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    // Views
    private lateinit var txtId: EditText
    private lateinit var txtName: EditText
    private lateinit var txtAuthor: EditText
    private lateinit var txtGenre: EditText
    private lateinit var lbPublishYear: TextView
    private lateinit var btnSelectDate: ImageButton
    private lateinit var txtCountry: EditText
    private lateinit var state: EditText

    private lateinit var menuItemDelete: MenuItem

    private var IsEditMode: Boolean = false

    private var day: Int = 1
    private var month: Int = 0 // 0-based
    private var year: Int = 2000

    private lateinit var bookController: BookController
    private var currentBook: Book? = null
    private var currentId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_book_info)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->

            bookController = BookController(this)

            txtId = findViewById(R.id.textId2)
            txtName = findViewById(R.id.textBookName2)
            txtAuthor = findViewById(R.id.textAuthor2)
            txtGenre = findViewById(R.id.textGenre2)
            lbPublishYear = findViewById(R.id.lbPublishYear2)
            btnSelectDate = findViewById(R.id.btnSelectDate2)
            txtCountry = findViewById(R.id.textCountry2)

            val btnUpdateBook = findViewById<Button>(R.id.btnUpdate)
            btnUpdateBook.setOnClickListener { updateBook() }

            txtId.isEnabled = false
            resetDay()
            currentId = intent.getStringExtra("BOOK_ID") ?: ""

            if (currentId.isBlank()) {
                Toast.makeText(this, "No se recibió el ID del libro", Toast.LENGTH_LONG).show()
                finish()
            }

            //Load book data
            loadBook(currentId)

            //DatePicker
            btnSelectDate.setOnClickListener { showDatePickerDialog() }

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    //Load book from the controller and fil the activity
    private fun loadBook(id: String) {
        try {
            val book = bookController.getById(id) ?: throw Exception(getString(R.string.MsgDataNotFound))
            currentBook = book
            populateFrom(book)
        } catch (e: Exception) {
            Toast.makeText(this, e.message ?: "No se pudo cargar el libro", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    // Filling spaces of book
    private fun populateFrom(book: Book) {
        txtId.setText(book.Id)
        txtName.setText(book.Name)
        txtAuthor.setText(book.Author)
        txtGenre.setText(book.Genre)
        txtCountry.setText(book.Country)

        val date = book.PublishYear
        lbPublishYear.text = getDateString(date.dayOfMonth, date.month.value, date.year)

        year = date.year
        month = date.month.value - 1 // 0-based
        day = date.dayOfMonth

        IsEditMode = true
        invalidateOptionsMenu()
    }

    //Checks if any space is empty
    fun isValidationData(): Boolean {
        val dateparse =
            Util.parseStringToDateModern(lbPublishYear.text.toString(), "dd/MM/yyyy")
        return txtId.text.trim().isNotEmpty() && txtName.text.trim().isNotEmpty()
                && txtAuthor.text.trim().isNotEmpty() && txtGenre.text.trim().isNotEmpty()
                && lbPublishYear.text.trim().isNotEmpty() && txtCountry.text.trim().isNotEmpty()
                && dateparse != null
    }

    //Calls other functions to verify data and updates books
    fun updateBook() {
        try {
            if (isValidationData()) {
                val book = Book()
                book.Id = txtId.text.toString()
                book.Name = txtName.text.toString()
                book.Author = txtAuthor.text.toString()
                book.Status = true
                book.Genre = txtGenre.text.toString()
                book.Country = txtCountry.text.toString()
                val bDateParse = Util.parseStringToDateModern(
                    lbPublishYear.text.toString(),
                    "dd/MM/yyyy"
                )
                book.PublishYear = LocalDate.of(
                    bDateParse?.year!!,
                    bDateParse.month.value,
                    bDateParse.dayOfMonth
                )

                if (IsEditMode == false) {
                    bookController.updateBook(book)
                }
                Toast.makeText(
                    this,
                    getString(R.string.MsgUpdateSucces),
                    Toast.LENGTH_LONG
                ).show()

            } else {
                Toast.makeText(
                    this,
                    "Datos incompletos",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(
                this,
                e.message.toString(),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    //Delete book in activity
    fun deleteBook() {
        try {
            bookController.removeBook(txtId.text.trim().toString())
            Toast.makeText(
                this,
                getString(R.string.MsgDeleteSuccess),
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_crud, menu)
        menuItemDelete = menu!!.findItem(R.id.menuDelete)
        menuItemDelete.isVisible = IsEditMode
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            R.id.menuDelete -> {
                Util.showDialogCondition(
                    this,
                    getString(R.string.TextDeleteQuestion)
                ) { deleteBook() }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //DatePicker to reset the dates
    private fun resetDay() {
        val c = Calendar.getInstance()
        year = c.get(Calendar.YEAR)
        month = c.get(Calendar.MONTH)
        day = c.get(Calendar.DAY_OF_MONTH)
    }

    //Gets the dates
    private fun getDateString(dayValue: Int, monthValue: Int, yearValue: Int): String {
        val dd = if (dayValue < 10) "0$dayValue" else "$dayValue"
        val mm = if (monthValue < 10) "0$monthValue" else "$monthValue"
        return "$dd/$mm/$yearValue"
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        lbPublishYear.text = getDateString(dayOfMonth, month + 1, year)
        this.year = year
        this.month = month
        this.day = dayOfMonth
    }

    //Shows the datePicker with a new range of dates
    private fun showDatePickerDialog() {
        val dialog = DatePickerDialog(this, this, year, month, day)
        val min = Calendar.getInstance().apply { set(1300, Calendar.JANUARY, 1) }
        val max = Calendar.getInstance().apply { set(2100, Calendar.DECEMBER, 31) }
        dialog.datePicker.minDate = min.timeInMillis
        dialog.datePicker.maxDate = max.timeInMillis

        dialog.show()
    }
}
