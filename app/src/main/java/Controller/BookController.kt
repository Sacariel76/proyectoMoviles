package Controller

import Data.IDataManager
import Data.MemoryDataManager
import Entity.Book
import android.content.Context
import com.example.proyectomovil.R

class BookController(private val context: Context) {

    private var dataManager: IDataManager = MemoryDataManager

    fun addBook(book: Book) {
        try {
            dataManager.add(book)
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgAdd))
        }
    }

    fun updateBook(book: Book) {
        try {
            dataManager.update(book)
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgUpdate))
        }
    }

    fun getAll(): List<Book> {
        return try {
            dataManager.getAll()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getById(id: String): Book? {
        return try {
            dataManager.getById(id)
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgGetById))
        }
    }

    fun getByName(name: String): Book? {
        return try {
            dataManager.getByName(name)
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgGetByName))
        }
    }

    fun getByAuthor(author: String): Book? {
        return try {
            dataManager.getByAuthor(author)
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgGetByAuthor))
        }
    }

    fun getByCountry(country: String): Book? {
        return try {
            dataManager.getByCountry(country)
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgGetByCountry))
        }
    }

    fun getByGenre(genre: String): Book? {
        return try {
            dataManager.getByGenre(genre)
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgGetByGenre))
        }
    }

    fun removeBook(id: String) {
        try {
            val result = dataManager.getById(id)
            if (result == null) {
                throw Exception(context.getString(R.string.MsgDataNotFound))
            }
            dataManager.remove(id)
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgRemove))
        }
    }
}
