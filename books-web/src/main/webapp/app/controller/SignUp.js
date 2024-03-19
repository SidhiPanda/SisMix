'use strict';

/**
 * Settings user edition page controller.
 */
App.controller('SignUp', function($scope, $state, $stateParams, Restangular) {
    /**
     * Register a new user.
     */
    $scope.signup = function() {
        var promise = null;

        promise = Restangular
            .one('user')
            .put($scope.user);

        promise.then(function() {
            window.alert("created user");
            $state.transitionTo('main');
        }).catch(function (error) {
            console.error("Error encountered during signup:", error);
            window.alert("An error occurred while creating the user: " + error.data.message);
        });
    };
    /**
     * Clear form by reloading page
     */
    $scope.clearForm = function() {
        console.log("clearing form");
        location.reload();
    };

    /**
     * Redirect to home page or login based on user authentication status.
     */
    $scope.redirectToHomePageOrLogin = function() {
        $state.transitionTo('main');
    }

});