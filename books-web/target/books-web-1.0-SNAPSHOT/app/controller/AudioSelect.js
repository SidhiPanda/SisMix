'use strict';

/**
 * Audio search controller.
 */
App.controller('AudioSelect', function($scope, $timeout, Restangular, $stateParams, $state, User) {
    $scope.selectedProvider="";
    $scope.selectedAudio="";
    $scope.isLoggedIn=false;



    User.userInfo().then(function(data) {
        if (data.anonymous) {
          $scope.isLoggedIn = false;
        } else {
          $scope.isLoggedIn = true;
        }
        console.log($scope.isLoggedIn);
      });

    $scope.printfn = function(){
        console.log($scope.selectedProvider);
        console.log($scope.selectedAudio);
    }

    $scope.showAlert = function() {
            alert("Selected audio or provider is invalid. Please select Audiobook or Podcast for media type, and Spotify or iTunes for the provider.");
    };

});