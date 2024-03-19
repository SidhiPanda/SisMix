package com.sismics.books.rest.command;

public class LibraryInvoker {
    
    private LibraryCommand libraryCommand;

    public LibraryInvoker(LibraryCommand libraryCommand) {
        this.libraryCommand = libraryCommand;
    }

    public LibraryInvoker() {
    }

    public void setLibraryCommand(LibraryCommand libraryCommand) {
        this.libraryCommand = libraryCommand;
    }

    public LibraryCommand getLibraryCommand() {
        return libraryCommand;
    }

    public void executeLibraryCommand(String type, String arg, String userId) throws Exception {
        libraryCommand.getLibraryBooks(type, arg, userId);
    }
}
