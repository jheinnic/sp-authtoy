window.App = angular.module('portfolio', ['ngResource', 'crosswords'])

App.controller 'NavBarCtrl', ['$scope', '$location', ($scope, $location) ->
  $scope.section = 'home';

  paths =
    home: '/',
    crosswords: '/crosswords',
    poker: '/poker',
    video: '/videos'

  $scope.changeSection = (newSection) ->
    $scope.section = newSection
    $location.path paths[newSection] 
]

App.controller 'HomeCtrl', ['$scope', ($scope) ->
  $scope.message = 'Hello world!'
]

App.config ['$routeProvider', ($routeProvider) ->
  $routeProvider.when '/', 
    templateUrl: 'partials/portfolio/view.html',
    controller: 'HomeCtrl'
  $routeProvider.when '/crosswords', 
    templateUrl: 'partials/crosswords/view.html'
    controller: 'CrosswordsCtrl'
]
