package Data

import Entity.Book

interface IDataManager {

    fun add (book: Book)
    fun update (book: Book)
    fun remove (id: String)
    fun getAll(): List<Book>
    fun getById(id: String): Book?
    fun getByName(name: String): Book?
    fun getByAuthor(author: String): Book?
    fun getByCountry(country: String): Book?
    fun getByGenre(genre: String): Book?
}