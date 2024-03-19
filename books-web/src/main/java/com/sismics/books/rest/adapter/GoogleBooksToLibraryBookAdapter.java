package com.sismics.books.rest.adapter;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.sismics.books.core.model.jpa.Book;
import com.sismics.books.core.model.jpa.LibraryBook;
import com.sismics.books.rest.util.StringUtil;

public class GoogleBooksToLibraryBookAdapter implements APIToLibraryBookInterface {
    private class urlDto {
        int totalItems = 0;
        List<Item> items;
    }

    private class Item {
        VolumeInfo volumeInfo;
    }

    private class VolumeInfo {
        // String title;
        List<String> authors;
        List<String> categories;
        ImageLinks imageLinks;
    }

    private class ImageLinks {
        // String smallThumbnail;
        String thumbnail;
    }

    private static String baseURL = "https://www.googleapis.com/books/v1/volumes?q=isbn:";

    public GoogleBooksToLibraryBookAdapter() {
    }

    private VolumeInfo readJSONFromURL(String url) throws Exception {
        urlDto dto;
        do {
            URL url_object = new URL(url);
            InputStreamReader reader = new InputStreamReader(url_object.openStream());
            dto = new Gson().fromJson(reader, urlDto.class);
        } while (dto.totalItems == 0);

        VolumeInfo volumeInfo = new VolumeInfo();
        volumeInfo = dto.items.get(0).volumeInfo;
        return volumeInfo;
    }

    public List<VolumeInfo> readJSONFromURLList(String url) throws Exception {
        urlDto dto;
        do {
            URL url_object = new URL(url);
            InputStreamReader reader = new InputStreamReader(url_object.openStream());
            dto = new Gson().fromJson(reader, urlDto.class);
        } while (dto.totalItems == 0);

        List<VolumeInfo> volumeInfoList = new ArrayList<>();
        for (Item item : dto.items) {
            volumeInfoList.add(item.volumeInfo);
        }
        return volumeInfoList;
    }

    @Override
    public LibraryBook createLibraryBook(Book book) {
        LibraryBook libraryBook;
        libraryBook = new LibraryBook();
        libraryBook.setId(book.getId());
        libraryBook.setTitle(book.getTitle());
        libraryBook.setRating(0);
        libraryBook.setNumRatings(0);

        String url = baseURL + book.getIsbn13();
        VolumeInfo volumeInfo;
        try {
            volumeInfo = readJSONFromURL(url);
        } catch (Exception e) {
            volumeInfo = new VolumeInfo();
        }

        if (volumeInfo.authors == null) {
            volumeInfo.authors = new ArrayList<>();
            volumeInfo.authors.add(book.getAuthor());
        }

        if (volumeInfo.categories == null) {
            volumeInfo.categories = new ArrayList<>();
            volumeInfo.categories.add("N/A");
        }

        libraryBook.setAuthors(StringUtil.ListToString(volumeInfo.authors, ","));
        libraryBook.setGenres(StringUtil.ListToString(volumeInfo.categories, ","));
        libraryBook.setThumbURL(volumeInfo.imageLinks.thumbnail);

        return libraryBook;
    }
}
