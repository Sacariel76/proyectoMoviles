package Data

import Entity.Book

object MemoryDataManager: IDataManager {
    private var bookList = mutableListOf<Book>()
    override fun add(book: Book) {
        bookList.add(book)
    }

    override fun remove(id: String){
        bookList.removeIf { it.Id.trim() == id.trim() }
    }

    override fun update(book: Book) {
        remove(book.Id)
        add(book)
    }

    override fun getAll() = bookList

    override fun getById(id: String): Book? {
        val result = bookList.filter { it.Id.trim() == id.trim() }
        return if(result.any()) result[0] else null
    }

    override fun getByName(name: String): Book? {
        val result = bookList.filter { it.Name.trim() == name.trim() }
        return if(result.any()) result[0] else null
    }

    override fun getByAuthor(author: String): Book? {
        val result = bookList.filter { it.Author.trim() == author.trim() }
        return if (result.any()) result[0] else null
    }

    override fun getByGenre(genre: String): Book? {
        val result = bookList.filter { it.Genre.trim() == genre.trim() }
        return if(result.any()) result[0] else null
    }

    override fun getByCountry(country: String): Book? {
        val result = bookList.filter { it.Country.trim() == country.trim() }
        return if (result.any()) result[0] else null
    }

}