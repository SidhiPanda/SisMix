package com.sismics.books.rest.resource;

import com.sismics.books.core.dao.jpa.*;
import com.sismics.books.core.model.jpa.Book;
import com.sismics.books.core.model.jpa.LibraryBook;
import com.sismics.rest.exception.ClientException;
import com.sismics.rest.exception.ForbiddenClientException;
import com.sismics.books.rest.adapter.GoogleBooksToLibraryBookAdapter;
import com.sismics.books.rest.command.FilteringLibraryCommand;
import com.sismics.books.rest.command.LibraryInvoker;
import com.sismics.books.rest.command.RankingLibraryCommand;

import javax.ws.rs.Path;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Library book resource.
 * 
 * Serves as the client for the Command design pattern
 */

@Path("/library")
public class LibraryBookResource extends BaseResource {
    
    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLibrary(
        @QueryParam("input") String input,
        @QueryParam("type") String type
    ) throws Exception {
        if (!authenticate()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        JSONObject response = new JSONObject();
        String commandType = ((Arrays.asList("default", "rating", "genre", "author").indexOf(type) != -1) ? "filter" : "rank");
        
        List<JSONObject> books = new ArrayList<>();
        LibraryInvoker libraryInvoker = new LibraryInvoker();
        switch (commandType) {
            case "filter":
                libraryInvoker.setLibraryCommand(new FilteringLibraryCommand());
                break;
            case "rank":
                libraryInvoker.setLibraryCommand(new RankingLibraryCommand());
                break;
            default:
                break;
        }

        books = libraryInvoker.getLibraryCommand().getLibraryBooks(type, input, principal.getId());
        response.put("books", books);

        return Response.ok().entity(response).build();
    }

    private LibraryBook createLibraryBook(Book book, LibraryBookDao libraryBookDao) {
        GoogleBooksToLibraryBookAdapter adapter = new GoogleBooksToLibraryBookAdapter();
        LibraryBook libraryBook = adapter.createLibraryBook(book);

        libraryBookDao.create(libraryBook);
        return libraryBook;
    }

    /**
     * Creates a new book.
     *
     * @param isbn ISBN Number
     * @return Response
     * @throws JSONException
     */
    @PUT
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Response add(
            @FormParam("title") String title,
            @FormParam("isbn") String isbn) throws JSONException {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        // Get the book
        BookDao bookDao = new BookDao();
        Book book = bookDao.getByIsbn(isbn);

        // Create the library book if needed
        String id = book.getId();
        LibraryBookDao libraryBookDao = new LibraryBookDao();
        LibraryBook libraryBook = libraryBookDao.getById(id);
        if (libraryBook == null) {
            libraryBook = createLibraryBook(book, libraryBookDao);
        } else {
            throw new ClientException("BookAlreadyAdded", "Book already added");
        }

        JSONObject response = new JSONObject();
        response.put("id", libraryBook.getId());
        return Response.ok().entity(response).build();
    }

    @POST
    @Path("/rate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response rate(
        @FormParam("bookId") String bookId,
        @FormParam("rating") float rating) throws JSONException {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        if (rating < 0 || rating > 10) {
            throw new ClientException("InvalidRating", "Invalid rating");
        }

        LibraryBookDao libraryBookDao = new LibraryBookDao();

        // check if user has already rated the book
        float oldUserRating = libraryBookDao.findUserRating(bookId, principal.getId());
        if (oldUserRating == -1) { // is a new rating
            // compute new rating
            LibraryBook libraryBook = libraryBookDao.getById(bookId);
            float newRating = (libraryBook.getRating() * libraryBook.getNumRatings() + rating) / (libraryBook.getNumRatings() + 1);
            // round to 2 decimal places
            newRating = (float) ((float) Math.round(newRating * 100.0) / 100.0);
            libraryBookDao.updateBookRating(bookId, newRating, true);
            libraryBookDao.setUserRating(bookId, principal.getId(), rating);
        } else {
            // update rating
            LibraryBook libraryBook = libraryBookDao.getById(bookId);
            float newRating = (libraryBook.getRating() * libraryBook.getNumRatings() - oldUserRating + rating) / libraryBook.getNumRatings();    
            newRating = (float) ((float) Math.round(newRating * 100.0) / 100.0);
            libraryBookDao.updateBookRating(bookId, newRating, false);
            libraryBookDao.setUserRating(bookId, principal.getId(), rating);
        }

        // send oldRating in response
        JSONObject response = new JSONObject();
        response.put("oldRating", rating);
        return Response.ok().entity(response).build();
    }
}
