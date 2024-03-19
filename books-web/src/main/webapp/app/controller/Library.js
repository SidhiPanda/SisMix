"use strict";

App.controller("Library", function ($scope, $state, Restangular) {
  // Initialize filter text
  
  $scope.filter = {
    text: null,
  };
  /**
   * Load the library.
  */
 $scope.loadLibrary = function () {
  if ($scope.bookRanking === null || $scope.bookRanking === undefined) {
    $scope.submitBookRanking("average");
  }
  else {
    $scope.submitBookRanking($scope.bookRanking);
  }
  // $scope.submitBookRanking("average");
   if ($scope.filter.text === "" || $scope.selectedFilter === null || $scope.selectedFilter === "" || $scope.filter.text === null || $scope.filter.text === undefined) {
    $scope.applyFilter("", "default");
   } else {
   $scope.applyFilter($scope.filter.text, $scope.selectedFilter);
   }
   return;
  };

  $scope.openRatingDialog = function (book) {
    // Open the rating dialog
    $scope.curBook = book;
    document.getElementById("ratingDialog").showModal();
  };

  $scope.closeRatingDialog = function () {
    // Close the rating dialog
    $scope.curBook = null;
    document.getElementById("ratingDialog").close();
  };

  $scope.rateBook = function (rating) {
    // Rate the book
    $scope.loading = true;
    if (rating < 1 || rating > 10) {
      alert("Invalid rating");
      return;
    }
        if (rating % 1 !== 0) {
            alert("Rating should be an integer");
            return;
        }
    try {
      Restangular.one("library/rate")
        .post("", { bookId: $scope.curBook.id, rating: rating })
        .then(function (data) {
        $scope.applyFilter($scope.filter.text, $scope.selectedFilter);
          // clear the dialog
          $scope.ratingInput = null;
        })
        .catch(function (response) {
          console.log("Response error: " + response.status);
          if (response.status == 403 || response.status == 401)
            $scope.libauth = false;
          alert("Server error");
        });
    } catch (e) {
      console.log("Got error:" + e);
      alert("Error rating book: " + e);
    } finally {
      $scope.loading = false;
      $scope.loadLibrary();
      $scope.closeRatingDialog();
    }
  };

  // Submit the book ranking type
  $scope.submitBookRanking = function (bookRanking) {
    console.log($scope);
    console.log("Ranking books with type: " + bookRanking);
    try { 
      Restangular.one("library/list")
        .get({ input: "", type: bookRanking })
        .then(function (data) {
          $scope.popularBooks = data.books.map(function (book) {
            book.rating = Math.round(book.rating * 100) / 100;
            // console.log(book.rating);
            return book;
          });
          console.log("Popular books: ");
          console.log($scope.popularBooks);
        })
        .catch(function (error) {
          console.error("Error retrieving top books:", error);
          if (error.status == 403 || error.status == 401) {
            $scope.libauth = false;
          }
          if (error.status == 400) {
            alert("Books already ranked.");
          }
        });
    } catch (e) {
      console.log("Got error:" + e);
    } finally {
      $scope.loading = false;
    }
  };

  // Submit the filter type with input
  $scope.applyFilter = function (filterText, selectedFilter) {
    if (filterText === null || filterText === undefined) {
      filterText = "";
    }

    if ($scope.selectedFilter === null || $scope.selectedFilter === undefined) {
      $scope.selectedFilter = "default";
    }

    if ($scope.filter.text === null || $scope.filter.text === undefined) {
      $scope.filter.text = "";
    }

    if (selectedFilter === null || selectedFilter === undefined) {
      selectedFilter = "default";
    }
    console.log(
      "Filtering books with type: " +
        selectedFilter +
        " and input: " +
        filterText
    );

    console.log("scope.filter.text: " + $scope.filter.text + ",,, scope.selectedFilter: " + $scope.selectedFilter);

    if ($scope.loading) {
      // Avoid spamming the server
      return;
    }
    if (selectedFilter === "default") {
        $scope.filter.text = "";
    }
    if (selectedFilter === "rating") {
        // Check if filterText is a valid integer between 1 and 10
        if (filterText.match(/\D/g)) {
            alert("Please enter a valid integer between 1 and 10 for the rating filter.");
            return; // Exit the function early if the input is invalid
        }
        const rating = parseInt(filterText, 10);
        if (isNaN(rating) || rating < 1 || rating > 10) {
            alert("Please enter a valid integer between 1 and 10 for the rating filter.");
            return; // Exit the function early if the input is invalid
        }
    }
    $scope.libauth = true;
    $scope.library = [];
    $scope.loading = true;
    $scope.filtering = (filterText !== "" && selectedFilter !== "default") ? true : false;

    try {
      Restangular.one("library/list")
        .get({ input: $scope.filter.text, type: $scope.selectedFilter })
        .then(function (data) {
          $scope.library = data.books.map(function (book) {
            book.rating = Math.round(book.rating * 100) / 100;
            return book;
          });
          console.log("Library: ");
          console.log($scope.library);
        })
        .catch(function (error) {
          console.error("Error filtering the books of the library:", error);
          if (error.status == 403 || error.status == 401) {
            $scope.libauth = false;
          }
          if (error.status == 400) {
            alert("Books already filtered with the selected filter.");
          }
        });
    } catch (e) {
      console.log("Got error:" + e);
    } finally {
      $scope.loading = false;
    }
    };

    $scope.getStars = function(rating) {
        var numStars = Math.round(rating / 2);
        var stars = [];
        for (var i = 0; i < numStars; i++) {
            stars.push(i);
        }
        return stars;
  };        

  // Load the library
  $scope.loadLibrary();
});
