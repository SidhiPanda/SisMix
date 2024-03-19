'use strict';

// Define a shared service to store and manage data
App.factory('AudioDataService', function() {
    var sharedData = {};

    return {
        getData: function() {
            return sharedData;
        },
        setData: function(data) {
            sharedData = data;
        }
    };
});


/**
 * Audio search controller.
 */
App.controller('AudioSearch', function($scope, $timeout, Restangular, $stateParams, AudioDataService) {
    /**
     * View scope variables.
     */
    $scope.sortColumn = 3;
    $scope.asc = true;
    $scope.offset = 0;
    $scope.limit = 20;
    $scope.search = {
        text: '',
        read: false
    };
    $scope.loading = false;
    $scope.audioItems = [];
    $scope.total = -1;
    $scope.searchField="title"
    $scope.mediaType = ""

    // A timeout promise is used to slow down search requests to the server
    // We keep track of it for cancellation purpose
    var timeoutPromise;

    /**
     * Reload audio items.
     */
    $scope.loadAudioItems = function() {
        $scope.offset = 0;
        $scope.total = -1;
        $scope.audioItems = [];
        $scope.pageAudioItems();
    };

    /**
     * Load books.
     */
    $scope.pageAudioItems = function(next) {
        if ($scope.loading || $scope.total == $scope.audioItems.length) {
            // Avoid spamming the server
            return;
        }

        if (next) {
            $scope.offset += $scope.limit;
        }

        $scope.loading = true;
        // media can be audiobook and podcast
        // provider can be spotify or itunes
        // checks
        if($stateParams.media == "podcast" || $stateParams.media == "audiobook") {
            if ($stateParams.provider == 'spotify' || $stateParams.provider == 'itunes') {
                Restangular.one('mediaSearch/' + $stateParams.media).get({
                    searchField: $scope.searchField,
                    query: $scope.search.text.split(" ").join("+"),
                    importType: $stateParams.provider
                }).then(function (data) {
                    console.log(data.audio.length)
                    console.log(data)
                    console.log($scope.search.text.split(" ").join("+"))
                    const convertedData = []
                    data.audio.forEach((dataItem) => {convertedData.push(JSON.parse(dataItem))});

                    $scope.mediaType = data.type;

                    $scope.audioItems = $scope.audioItems.concat(convertedData);
                    $scope.total = data.total;
                    $scope.loading = false;
                })
                    .catch(function(data) {
                        console.log($scope.search.text.split(" ").join("+"));
                    })
                    .finally(() => {$scope.loading= false});
            }
        }
    };

    /**
     * Watch for search scope change.
     */
    $scope.$watch(function(){return $scope.search.text + $scope.searchField;}, function() {
        console.log('reloading');
        if (timeoutPromise) {
            // Cancel previous timeout
            $timeout.cancel(timeoutPromise);
        }

        // Call API later
        timeoutPromise = $timeout(function () {
            $scope.loadAudioItems();
        }, 1000);
    }, true);

    $scope.addToFavorites = function(audio, media) {
            Restangular.one('favorites/add').get({
                audioType: media,
                audioObj: audio
            }).then(function (data) {
                console.log(data);
            }, function(response) {
                alert("Already in your favorites")
            });
    }

    $scope.sendData = function(data){
        data.type = $scope.mediaType;
        AudioDataService.setData(data);

    }


});