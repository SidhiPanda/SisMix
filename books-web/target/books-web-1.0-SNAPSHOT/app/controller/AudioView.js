'use strict';


/**
 * Audio search controller.
 */
App.controller('AudioView', function($scope, $timeout, Restangular, $stateParams, AudioDataService) {
    /**
     * View scope variables.
     */
    $scope.audio = AudioDataService.getData();
    console.log($scope.audio.explicit);

    // A timeout promise is used to slow down search requests to the server
    // We keep track of it for cancellation purpose
    var timeoutPromise;

    /**
     * Reload audio items.
     */
    $scope.loadAudio = function() {
        $scope.audio = AudioDataService.getData();
    };


});