package com.example.proyectomovil

import Controller.BookController
import Entity.Book
import Util.Util
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.util.Calendar

class RegisterBooksActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private var db = FirebaseFirestore.getInstance()

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
            if (imageUri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                ivBookImage.setImageBitmap(bitmap)
                selectedImageBitmap = bitmap
            }
        }
    }

    // Triggers the aparition of the calendar
    private fun showDatePickerDialog() {
        // month ya viene 0-based desde ResetDay, así que se usa directo
        val datePickerDialog = DatePickerDialog(this, this, year, month, day)

        val minDate = Calendar.getInstance()
        minDate.set(1300, Calendar.JANUARY, 1)
        datePickerDialog.datePicker.minDate = minDate.timeInMillis

        val maxDate = Calendar.getInstance()
        maxDate.set(2100, Calendar.DECEMBER, 31)
        datePickerDialog.datePicker.maxDate = maxDate.timeInMillis

        datePickerDialog.show()
    }

    // Resets the date
    private fun ResetDay() {
        val calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH) // 0-based
        day = calendar.get(Calendar.DAY_OF_MONTH)
    }

    // Cleans the data in the screen
    private fun cleanScreen() {
        IsEditMode = false
        ResetDay()
        txtName.setText("")
        txtAuthor.setText("")
        txtGenre.setText("")
        txtCountry.setText("")
        lbPublishYear.text = ""
        ivBookImage.setImageResource(android.R.color.darker_gray)
        selectedImageBitmap = null
        invalidateMenu()
    }

    // Validates empty fileds
    fun isValidationData(): Boolean {
        val dateparse =
            Util.parseStringToDateModern(lbPublishYear.text.toString(), "dd/MM/yyyy")
        return txtName.text.trim().isNotEmpty()
                && txtAuthor.text.trim().isNotEmpty()
                && txtGenre.text.trim().isNotEmpty()
                && lbPublishYear.text.trim().isNotEmpty()
                && txtCountry.text.trim().isNotEmpty()
                && dateparse != null
    }

    // Picks all data to and sends it to Firestore
    fun saveBook() {
        if (!isValidationData()) {
            Toast.makeText(this, "Complete todos los campos correctamente", Toast.LENGTH_LONG).show()
            return
        }

        val bDateParse = Util.parseStringToDateModern(
            lbPublishYear.text.toString(), "dd/MM/yyyy"
        )

        if (bDateParse == null) {
            Toast.makeText(this, "Fecha inválida", Toast.LENGTH_LONG).show()
            return
        }

        val publishLocalDate = LocalDate.of(
            bDateParse.year, bDateParse.month.value, bDateParse.dayOfMonth
        )

        val book = Book().apply {
            Image = selectedImageBitmap
            Name = txtName.text.toString()
            Author = txtAuthor.text.toString()
            Status = true
            Genre = txtGenre.text.toString()
            Country = txtCountry.text.toString()
            PublishYear = publishLocalDate
        }

        // Gets the publish year text in dd/MM/yyyy format
        val publishYearText = lbPublishYear.text.toString()

        // Saves in Firestore + Storage (now it uploads the image to Storage and then sends data to the API)
        val bitmap = selectedImageBitmap
        if (bitmap != null) {
            val storageRef = FirebaseStorage.getInstance()
                .reference
                .child("books/${System.currentTimeMillis()}.jpg")

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
            val data = baos.toByteArray()

            val uploadTask = storageRef.putBytes(data)

            uploadTask
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        throw task.exception ?: Exception("Error uploading image")
                    }
                    storageRef.downloadUrl
                }
                .addOnSuccessListener { uri ->
                    saveBookToApi(
                        imageUrl = uri.toString(),
                        name = book.Name,
                        author = book.Author,
                        status = book.Status,
                        genre = book.Genre,
                        country = book.Country,
                        publishYear = publishYearText
                    )
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Error uploading image: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        } else {
            // If there is no image, the book is sent without image URL
            saveBookToApi(
                imageUrl = null,
                name = book.Name,
                author = book.Author,
                status = book.Status,
                genre = book.Genre,
                country = book.Country,
                publishYear = publishYearText
            )
        }
    }

    // Saves the book in Firestore
    private fun saveBookToApi(
        imageUrl: String?,
        name: String,
        author: String,
        status: Boolean,
        genre: String,
        country: String,
        publishYear: String
    ) {
        val bookApiModel = api.BookApiModel(
            status = status,
            name = name,
            author = author,
            genre = genre,
            country = country,
            publishYear = publishYear,
            imageUrl = imageUrl
        )

        api.ApiClient.bookApi.createBook(bookApiModel)
            .enqueue(object : retrofit2.Callback<api.BookApiModel> {
                override fun onResponse(
                    call: retrofit2.Call<api.BookApiModel>,
                    response: retrofit2.Response<api.BookApiModel>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@RegisterBooksActivity,
                            "Libro registrado correctamente en la API",
                            Toast.LENGTH_SHORT
                        ).show()
                        cleanScreen()
                    } else {
                        Toast.makeText(
                            this@RegisterBooksActivity,
                            "Error al registrar en API: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<api.BookApiModel>,
                    t: Throwable
                ) {
                    Toast.makeText(
                        this@RegisterBooksActivity,
                        "Fallo al llamar API: ${t.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}
