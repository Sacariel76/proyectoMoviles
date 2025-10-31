package com.example.proyectomovil

import Controller.BookController
import Entity.Book
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterBooksActivity : AppCompatActivity() {

    private lateinit var txtId: EditText
    private lateinit var txtName: EditText
    private lateinit var txtAuthor: EditText
    private lateinit var txtPublishYear: EditText
    private lateinit var txtImage: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_books)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->

            //El ID irá de forma auto incremental al ingresar un nuevo libro
            //txtId.toString()
            txtName = findViewById<EditText>(R.id.textNombreLibro)
            txtAuthor = findViewById<EditText>(R.id.textNombreAutor)
            txtPublishYear = findViewById<EditText>(R.id.textAnhoPublicacion)
            txtImage = findViewById<EditText>(R.id.textImagen)

            val btnSave = findViewById<Button>(R.id.btnRegistrarLibro)
            btnSave.setOnClickListener { view -> saveBook() }

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
            }
        }

    fun validationData(book: Book): Boolean{
        return true
    }
    fun saveBook(){
        try {
            val book = Book()
            book.Id = txtId.text.toString()
            book.Name = txtName.text.toString()
            book.Author = txtAuthor.text.toString()
            //book.PublishYear = txtPublishYear.text.toString().toDate()
            //book.Image = txtImage.text.toString().toImage()

            if(validationData(book)){
                val personController = BookController(this)
                //Aquí va el metodo para verificar el auto incremento del ID
                personController.addBook(book)
                Toast.makeText(this, "El libro se ha guardado", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this, "Datos incompletos", Toast.LENGTH_LONG).show()
            }
        }catch (e: Exception){
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
        }
    }
}
