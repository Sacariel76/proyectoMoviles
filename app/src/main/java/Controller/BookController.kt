package Controller

import Data.IDataManager
import Data.MemoryDataManager
import Entity.Book
import android.content.Context
import com.example.proyectomovil.R

class BookController {
    private var dataManager: IDataManager = MemoryDataManager
    private var context: Context

    constructor(context: Context){
        this.context = context
    }

    fun addBook(book: Book){
        try {
            dataManager.add(book)
        }catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgAdd))
        }
    }

    fun updateBook(book: Book){
        try {
            dataManager.update(book)
        }catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgUpdate))
        }
    }

    fun getById(id: String): Book{
        try {
            var result = dataManager.getById(id)
            if (result == null){
                throw Exception(context.getString(R.string.MsgDataNotFound))
            }
            return result
        }catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgUpdate))
        }
    }

    fun getByName(name: String): Book?{
        try {
            var result = dataManager.getByName(name)
            return result

        }catch (e: Exception){
            throw Exception(context.getString(R.string.MsgDataNotFound))
        }
    }

    fun getByAuthor(author: String): Book?{
        try {
            var result = dataManager.getByAuthor(author)
            return result

        }catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgGetByAuthor))
        }
    }

    fun removeBook(id: String){
        try {
            val result = dataManager.getById(id)
            if(result == null){
                throw Exception(context.getString(R.string.MsgDataNotFound))
            }
            dataManager.remove(id)
        }catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgRemove))
        }
    }
}