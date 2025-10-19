package Entity

import android.graphics.Bitmap
import java.sql.Date

class Book {

    private var id: String = ""
    private var name: String = ""
    private lateinit var publishYear: Date
    private var author: String = ""
    private var status: Boolean = true
    private var image: Bitmap?

    constructor(id: String, name: String, publishYear: Date, author: String,
                status: Boolean, image: Bitmap?)
    {
        this.id = id
        this.name = name
        this.publishYear = publishYear
        this.author = author
        this.status = status
        this.image = image
    }

    var Id: String
        get() = this.id
        set(value) {this.id = value}

    var Name: String
        get() = this.name
        set(value) {this.name = value}

    var PublishYear: Date
        get() = this.publishYear
        set(value) {this.publishYear = value}

    var Author: String
        get() = this.Author
        set(value) {this.Author = value}

    var Status: Boolean
        get() = this.status
        set(value) {this.status = value}

    var Image: Bitmap?
        get() = this.image
        set(value) {this.image = value}

}