package com.example.proyectomovil

import Controller.BookController
import Entity.Book
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.*
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
    private lateinit var txtGenre: EditText
    private lateinit var ivBookImage: ImageView
    private lateinit var btnPickImage: Button
    private var selectedImageBitmap: Bitmap? = null

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

        bookController = BookController(this)

        ivBookImage   = findViewById(R.id.ivBookImage)
        btnPickImage  = findViewById(R.id.btnPickImage)
        txtId         = findViewById(R.id.textId)
        txtName       = findViewById(R.id.textBookName)
        txtAuthor     = findViewById(R.id.textAuthor)
        lbPublishYear = findViewById(R.id.lbPublishYear)
        txtCountry    = findViewById(R.id.textCountry)
        txtGenre      = findViewById(R.id.textGenre)

        ResetDay()

        val btnSave = findViewById<Button>(R.id.btnRegisterBook)
        btnSave.setOnClickListener { saveBook() }

        val btnSelectDate = findViewById<ImageButton>(R.id.btnSelectDate)
        btnSelectDate.setOnClickListener { showDatePickerDialog() }

        btnPickImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
            startActivityForResult(intent, 100)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sysBars.left, sysBars.top, sysBars.right, sysBars.bottom)
            insets
        }
    }

    fun validationData(book: Book): Boolean {
        return true
    }

    private fun getDateString(dayValue: Int, monthValue: Int, yearValue: Int): String {
        return "${if (dayValue < 10) "0" else ""}$dayValue/" +
                "${if (monthValue < 10) "0" else ""}$monthValue/" +
                "$yearValue"
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        lbPublishYear.text = getDateString(dayOfMonth, month + 1, year)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            ivBookImage.setImageBitmap(bitmap)
            selectedImageBitmap = bitmap
        }
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(this, this, year, month - 1, day)

        val minDate = Calendar.getInstance()
        minDate.set(1300, Calendar.JANUARY, 1)
        datePickerDialog.datePicker.minDate = minDate.timeInMillis

        val maxDate = Calendar.getInstance()
        maxDate.set(2100, Calendar.DECEMBER, 31)
        datePickerDialog.datePicker.maxDate = maxDate.timeInMillis

        datePickerDialog.show()
    }

    private fun ResetDay() {
        val calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)
    }

    private fun cleanScreen() {
        IsEditMode = false
        ResetDay()
        txtId.isEnabled = true
        txtId.setText("")
        txtName.setText("")
        txtAuthor.setText("")
        txtGenre.setText("")
        txtCountry.setText("")
        ivBookImage.setImageResource(android.R.color.darker_gray)
        selectedImageBitmap = null
        invalidateMenu()
    }

    fun isValidationData(): Boolean {
        val dateparse =
            Util.Util.parseStringToDateModern(lbPublishYear.text.toString(), "dd/MM/yyyy")
        return txtId.text.trim().isNotEmpty() && txtName.text.trim().isNotEmpty()
                && txtAuthor.text.trim().isNotEmpty() && txtGenre.text.trim().isNotEmpty()
                && lbPublishYear.text.trim().isNotEmpty() && txtCountry.text.trim().isNotEmpty()
                && dateparse != null
    }

    fun saveBook() {
        try {
            if (isValidationData()) {
                if (bookController.getById(txtId.text.toString().trim()) != null && !IsEditMode) {
                    Toast.makeText(this, getString(R.string.MsgDuplicateData), Toast.LENGTH_LONG).show()
                } else {
                    val book = Book().apply {
                        Image = selectedImageBitmap
                        Id = txtId.text.toString()
                        Name = txtName.text.toString()
                        Author = txtAuthor.text.toString()
                        Status = true
                        Genre = txtGenre.text.toString()
                        Country = txtCountry.text.toString()
                        val bDateParse = Util.Util.parseStringToDateModern(
                            lbPublishYear.text.toString(), "dd/MM/yyyy"
                        )
                        PublishYear = LocalDate.of(
                            bDateParse!!.year, bDateParse.month.value, bDateParse.dayOfMonth
                        )
                    }

                    if (!IsEditMode) {
                        bookController.addBook(book)
                    }
                    cleanScreen()
                    Toast.makeText(this, getString(R.string.MsgSaveSuccess), Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Datos incompletos", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
        }
    }
}
