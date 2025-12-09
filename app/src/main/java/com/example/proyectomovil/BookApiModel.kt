package api

data class BookApiModel(
    val id: String? = null,
    val status: Boolean = true,
    val name: String,
    val author: String,
    val genre: String,
    val country: String,
    val publishYear: String,
    val imageUrl: String? = null
)