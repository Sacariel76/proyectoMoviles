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
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.time.LocalDate
import java.util.Calendar

class BookInfoActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private val db = FirebaseFirestore.getInstance()

    // Views
    private lateinit var txtId: EditText
    private lateinit var txtName: EditText
    private lateinit var txtAuthor: EditText
    private lateinit var txtGenre: EditText
    private lateinit var lbPublishYear: TextView
    private lateinit var btnSelectDate: ImageButton
    private lateinit var txtCountry: EditText
    private lateinit var ivBookImage2: ImageView
    private lateinit var btnPickImage2: Button
    private lateinit var menuItemDelete: MenuItem

    // Remote data
    private lateinit var bookDocId: String
    private var currentImageUrl: String? = null
    private var pendingImageUri: Uri? = null

    // Local data
    private var selectedImageBitmap: Bitmap? = null
    private lateinit var bookController: BookController

    // Date for DatePicker
    private var day: Int = 1
    private var month: Int = 0 // 0-based
    private var year: Int = 2000

    private var isEditMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_book_info)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        bookController = BookController(this)

        ivBookImage2 = findViewById(R.id.ivBookImage2)
        btnPickImage2 = findViewById(R.id.btnPickImage2)

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

        // Document ID
        bookDocId = intent.getStringExtra("bookDocId") ?: ""
        if (bookDocId.isBlank()) {
            Toast.makeText(this, "No se pudo obtener el ID del libro", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Load data from Firestore
        loadBookData()

        // DatePicker
        btnSelectDate.setOnClickListener { showDatePickerDialog() }

        // Image selector
        btnPickImage2.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
            startActivityForResult(intent, 100)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Load book data from Firestore
    private fun loadBookData() {
        api.ApiClient.bookApi.getBook(bookDocId)
            .enqueue(object : retrofit2.Callback<api.BookApiModel> {
                override fun onResponse(
                    call: retrofit2.Call<api.BookApiModel>,
                    response: retrofit2.Response<api.BookApiModel>
                ) {
                    if (!response.isSuccessful || response.body() == null) {
                        Toast.makeText(this@BookInfoActivity, "Error al cargar libro", Toast.LENGTH_SHORT).show()
                        finish()
                        return
                    }

                    val book = response.body()!!

                    txtId.setText(book.id ?: "")
                    txtName.setText(book.name)
                    txtAuthor.setText(book.author)
                    txtCountry.setText(book.country)
                    txtGenre.setText(book.genre)
                    lbPublishYear.text = book.publishYear

                    // update DatePicker internal values
                    val parts = book.publishYear.split("/")
                    if (parts.size == 3) {
                        day = parts[0].toInt()
                        month = parts[1].toInt() - 1
                        year = parts[2].toInt()
                    }

                    currentImageUrl = book.imageUrl
                    currentImageUrl?.let { url ->
                        Glide.with(this@BookInfoActivity)
                            .load(url)
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .into(ivBookImage2)
                    }

                    // Enable edit mode so delete menu item is visible
                    isEditMode = true
                    invalidateOptionsMenu()
                }

                override fun onFailure(
                    call: retrofit2.Call<api.BookApiModel>,
                    t: Throwable
                ) {
                    Toast.makeText(
                        this@BookInfoActivity,
                        "Error al cargar libro: ${t.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            })
    }

    // Image selection
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            if (imageUri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                ivBookImage2.setImageBitmap(bitmap)
                selectedImageBitmap = bitmap
                pendingImageUri = imageUri   // 👈 marcar que hay imagen nueva para subir
            }
        }
    }

    // Validation
    private fun isValidationData(): Boolean {
        val dateparse = Util.parseStringToDateModern(lbPublishYear.text.toString(), "dd/MM/yyyy")
        return txtId.text.trim().isNotEmpty() &&
                txtName.text.trim().isNotEmpty() &&
                txtAuthor.text.trim().isNotEmpty() &&
                txtGenre.text.trim().isNotEmpty() &&
                lbPublishYear.text.trim().isNotEmpty() &&
                txtCountry.text.trim().isNotEmpty() &&
                dateparse != null
    }

    // Update the book
    private fun updateBook() {
        if (!isValidationData()) {
            Toast.makeText(this, "Datos incompletos", Toast.LENGTH_LONG).show()
            return
        }

        val name = txtName.text.toString().trim()
        val author = txtAuthor.text.toString().trim()
        val country = txtCountry.text.toString().trim()
        val genre = txtGenre.text.toString().trim()
        val publishYearText = lbPublishYear.text.toString().trim()

        // Por ahora usamos la imagenUrl ya guardada (no estamos subiendo nueva desde aquí)
        val imageUrlToSend = currentImageUrl

        val bookApiModel = api.BookApiModel(
            id = bookDocId,
            status = true,
            name = name,
            author = author,
            genre = genre,
            country = country,
            publishYear = publishYearText,
            imageUrl = imageUrlToSend
        )

        api.ApiClient.bookApi.updateBook(bookDocId, bookApiModel)
            .enqueue(object : retrofit2.Callback<api.BookApiModel> {
                override fun onResponse(
                    call: retrofit2.Call<api.BookApiModel>,
                    response: retrofit2.Response<api.BookApiModel>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@BookInfoActivity,
                            "Libro actualizado en la API",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@BookInfoActivity,
                            "Error al actualizar: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<api.BookApiModel>,
                    t: Throwable
                ) {
                    Toast.makeText(
                        this@BookInfoActivity,
                        "Fallo al llamar API: ${t.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    // ---------- Upload image and updates Firestore ----------
    private fun uploadImageAndUpdate(updates: MutableMap<String, Any>) {
        val fileUri = pendingImageUri ?: return

        val storageRef = FirebaseStorage.getInstance()
            .reference
            .child("bookImages/$bookDocId.jpg")

        storageRef.putFile(fileUri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception ?: Exception("Error al subir imagen")
                }
                storageRef.downloadUrl
            }
            .addOnSuccessListener { uri ->
                currentImageUrl = uri.toString()
                updates["imageUrl"] = currentImageUrl!!
                pendingImageUri = null
                updateBookInFirestore(updates)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show()
            }
    }

    // Updating document in Firestore
    private fun updateBookInFirestore(updates: Map<String, Any>) {
        db.collection("books")
            .document(bookDocId)
            .update(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Libro actualizado", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al actualizar en Firestore", Toast.LENGTH_SHORT).show()
            }
    }

    // Delete book (local + Firestore + Storage)
    private fun deleteBook() {
        api.ApiClient.bookApi.deleteBook(bookDocId)
            .enqueue(object : retrofit2.Callback<Void> {
                override fun onResponse(
                    call: retrofit2.Call<Void>,
                    response: retrofit2.Response<Void>
                ) {
                    if (response.isSuccessful) {
                        // si querés, también borrás local con bookController
                        bookController.removeBook(txtId.text.trim().toString())
                        Toast.makeText(
                            this@BookInfoActivity,
                            getString(R.string.MsgDeleteSuccess),
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@BookInfoActivity,
                            "Error al eliminar en API: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<Void>,
                    t: Throwable
                ) {
                    Toast.makeText(
                        this@BookInfoActivity,
                        "Fallo al llamar API: ${t.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    // Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_crud, menu)
        menuItemDelete = menu!!.findItem(R.id.menuDelete)
        menuItemDelete.isVisible = isEditMode
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

    // Datepicker
    private fun resetDay() {
        val c = Calendar.getInstance()
        year = c.get(Calendar.YEAR)
        month = c.get(Calendar.MONTH)
        day = c.get(Calendar.DAY_OF_MONTH)
    }

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

    private fun showDatePickerDialog() {
        val dialog = DatePickerDialog(this, this, year, month, day)
        val min = Calendar.getInstance().apply { set(1300, Calendar.JANUARY, 1) }
        val max = Calendar.getInstance().apply { set(2100, Calendar.DECEMBER, 31) }
        dialog.datePicker.minDate = min.timeInMillis
        dialog.datePicker.maxDate = max.timeInMillis
        dialog.show()
    }
}
