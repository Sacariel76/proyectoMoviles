package api

import retrofit2.Call
import retrofit2.http.*

interface BookApi {

    @GET("books")
    fun getBooks(): Call<List<BookApiModel>>

    @GET("books/{id}")
    fun getBook(@Path("id") id: String): Call<BookApiModel>

    @POST("books")
    fun createBook(@Body book: BookApiModel): Call<BookApiModel>

    @PUT("books/{id}")
    fun updateBook(
        @Path("id") id: String,
        @Body book: BookApiModel
    ): Call<BookApiModel>

    @DELETE("books/{id}")
    fun deleteBook(@Path("id") id: String): Call<Void>
}
