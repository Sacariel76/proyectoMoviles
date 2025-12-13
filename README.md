# Proyecto MiBiblioteca

Este proyecto consiste en la creación de una app capaz de llevar seguimiento a los libros físicos o digitales que el usuario esté leyendo, esto para tener más control sobre sus libros favoritos

# Preview de los módulos de la app

![MiBiblioteca App Preview](images/Mockup.png)

# Endpoints para puebas en Postman

# GET

https://us-central1-proyectoweb-b44de.cloudfunctions.net/api/books

# GET by ID

https://us-central1-proyectoweb-b44de.cloudfunctions.net/api/books/IDLIBRO

# POST

https://us-central1-proyectoweb-b44de.cloudfunctions.net/api/books

```bash
{
  "status": true,
  "name": "The Lord of the Rings",
  "author": "J. R. R. Tolkien",
  "genre": "Fantasy",
  "country": "United Kingdom",
  "publishYear": "29/07/1954",
  "imageUrl": "https://example.com/lotr.jpg"
}
```
# PUT

https://us-central1-proyectoweb-b44de.cloudfunctions.net/api/books/IDLIBRO

```bash
{
  "status": true,
  "name": "The Hobbit",
  "author": "J. R. R. Tolkien",
  "genre": "Fantasy",
  "country": "United Kingdom",
  "publishYear": "21/09/1937",
  "imageUrl": "https://example.com/hobbit-new-cover.jpg"
}
```

# DELETE

https://us-central1-proyectoweb-b44de.cloudfunctions.net/api/books/IDLIBRO

