package com.sismics.books.rest.adapter;

import com.sismics.books.core.model.jpa.Book;
import com.sismics.books.core.model.jpa.LibraryBook;

public interface APIToLibraryBookInterface {
    
    public LibraryBook createLibraryBook(Book book);
}
