'use strict';

// Define a shared service to store data
App.factory('DataService', function() {
    var mediaType = "";

    return {
        getData: function() {
            return mediaType;
        },
        setData: function(newData) {
            mediaType = newData;
        }
    };
});

/**
 * Favorites controller.
 */
App.controller('Favorites', function($scope, $timeout, Restangular, $stateParams, $state, DataService) {
    $scope.mediaType = "";
    $scope.audioItems = [];
    $scope.total = -1;

    $scope.loadData = function() {
        console.log($scope.mediaType);
        $scope.audioItems = [];

        if($scope.mediaType == ""){
            $scope.mediaType = DataService.getData();
        }

        Restangular.one('favorites/show').get({
            audioType: $scope.mediaType
        }).then(function (data) {
            console.log(data);

            const convertedData = []
            data.audioFav.forEach((dataItem) => {convertedData.push(JSON.parse(dataItem))});

            $scope.audioItems = $scope.audioItems.concat(convertedData);
            $scope.total = data.total;
        })
    }

    $scope.remFavorites = function(id){
        console.log(id);
        Restangular.one('favorites/remove').get({
            audioType: $scope.mediaType,
            audioId: id
        }).then(function (data){
            DataService.setData($scope.mediaType);
            $state.reload();
        })
    }





});