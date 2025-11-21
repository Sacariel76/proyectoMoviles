package com.example.proyectomovil

import Controller.BookController
import Entity.Book
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.time.LocalDate
import java.util.Calendar

class RegisterBooksActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var txtId: EditText
    private lateinit var txtName: EditText
    private lateinit var txtAuthor: EditText
    private lateinit var txtCountry: EditText
    private lateinit var lbPublishYear: TextView
    private lateinit var  txtGenre: EditText
    private var day: Int = 0
    private var month: Int = 0
    private var year: Int = 0
    private var IsEditMode: Boolean = false
    private lateinit var bookController: BookController
    private lateinit var menuItemDelete: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_books)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->

            bookController = BookController(this)
            txtId = findViewById<EditText>(R.id.textId)
            txtName = findViewById<EditText>(R.id.textBookName)
            txtAuthor = findViewById<EditText>(R.id.textAuthor)
            lbPublishYear = findViewById<TextView>(R.id.lbPublishYear)
            txtCountry = findViewById<EditText>(R.id.textCountry)
            txtGenre = findViewById<EditText>(R.id.textGenre)

            val btnSave = findViewById<Button>(R.id.btnRegisterBook)
            btnSave.setOnClickListener { view -> saveBook() }

            val btnSelectDate = findViewById<ImageButton>(R.id.btnSelectDate)
            btnSelectDate.setOnClickListener { view -> showDatePickerDialog() }

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
            }
        }

    fun validationData(book: Book): Boolean{
        return true
    }

    //Gets the date on the datePicker
    private fun getDateString(dayValue: Int, monthValue: Int, yearValue: Int): String {
        return "${if (dayValue < 10) "0" else ""}$dayValue/" +
                "${if (monthValue < 10) "0" else ""}$monthValue/" +
                "$yearValue"
    }

    //Sets the result of the datePicker
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        lbPublishYear.text = getDateString(dayOfMonth, month + 1, year)
    }

    //Shows the datePicker for the setting of dates in a new range
    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(this, this, year, month - 1, day)

        //Date limits
        val minDate = Calendar.getInstance()
        minDate.set(1300, Calendar.JANUARY, 1)//Min year
        datePickerDialog.datePicker.minDate = minDate.timeInMillis

        val maxDate = Calendar.getInstance()
        maxDate.set(2100, Calendar.DECEMBER, 31)//Max year
        datePickerDialog.datePicker.maxDate = maxDate.timeInMillis

        // Show dialog
        datePickerDialog.show()
    }

    //Reset the date
    private fun ResetDay(){
        val calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)
    }

    //Clear all text spaces
    private fun cleanScreen(){
        IsEditMode = false
        ResetDay()
        txtId.isEnabled = true
        txtId.setText("")
        txtName.setText("")
        txtAuthor.setText("")
        txtGenre.setText("")
        txtCountry.setText("")
        invalidateMenu()
    }

    //Checks if any space is empty
    fun isValidationData(): Boolean {
        val dateparse =
            Util.Util.parseStringToDateModern(lbPublishYear.text.toString(), "dd/MM/yyyy")
        return txtId.text.trim().isNotEmpty() && txtName.text.trim().isNotEmpty()
                && txtAuthor.text.trim().isNotEmpty() && txtGenre.text.trim().isNotEmpty()
                && lbPublishYear.text.trim().isNotEmpty() && txtCountry.text.trim().isNotEmpty()
                && dateparse != null
    }

    //Calls other functions to verify data and saves books
    fun saveBook() {
        try {
            if (isValidationData()) {
                if (bookController.getById(txtId.text.toString().trim()) != null
                    && !IsEditMode
                ) {
                    Toast.makeText(this, getString(R.string.MsgDuplicateData)
                        , Toast.LENGTH_LONG).show()
                }else{
                    val book = Book()
                    book.Id = txtId.text.toString()
                    book.Name = txtName.text.toString()
                    book.Author = txtAuthor.text.toString()
                    book.Status = true
                    book.Genre = txtGenre.text.toString()
                    book.Country = txtCountry.text.toString()
                    val bDateParse = Util.Util.parseStringToDateModern(lbPublishYear.text.toString(),
                        "dd/MM/yyyy")
                    book.PublishYear = LocalDate.of(bDateParse?.year!!, bDateParse.month.value
                        , bDateParse?.dayOfMonth!!)

                    if (IsEditMode == false){
                        bookController.addBook(book)
                    }
                    cleanScreen()
                    Toast.makeText(this, getString(R.string.MsgSaveSuccess)
                        , Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this, "Datos incompletos"
                    , Toast.LENGTH_LONG).show()
            }
        }catch (e: Exception){
            Toast.makeText(this, e.message.toString()
                , Toast.LENGTH_LONG).show()
        }
    }
}
