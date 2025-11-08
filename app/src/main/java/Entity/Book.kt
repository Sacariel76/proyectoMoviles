package Entity

import android.graphics.Bitmap
import java.sql.Date
import java.time.LocalDate

class Book {

    private var id: String = ""
    private var name: String = ""
    private lateinit var publishYear: LocalDate
    private var author: String = ""
    private var genre: String = ""
    private var country: String = ""
    private var status: Boolean = true
    private var image: Bitmap? = null

    constructor()


    constructor(id: String, name: String, publishYear: LocalDate, author: String,
                country: String, genre: String,
                status: Boolean, image: Bitmap?)
    {
        this.id = id
        this.name = name
        this.publishYear = publishYear
        this.author = author
        this.genre = genre
        this.country = country
        this.status = status
        this.image = image
    }

    var Id: String
        get() = this.id
        set(value) {this.id = value}

    var Name: String
        get() = this.name
        set(value) {this.name = value}

    var PublishYear: LocalDate
        get() = this.publishYear
        set(value) {this.publishYear = value}

    var Author: String
        get() = this.author
        set(value) {this.author = value}

    var Country: String
        get() = this.country
        set(value) {this.country = value}

    var Genre: String
        get() = this.genre
        set(value) {this.genre = value}

    var Status: Boolean
        get() = this.status
        set(value) {this.status = value}

    var Image: Bitmap?
        get() = this.image
        set(value) {this.image = value}

}