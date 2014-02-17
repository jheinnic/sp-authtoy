angular.module('authtoy', ['ngRoute', 'login', 'navbar']).value(
    'key', 'value'
).config(function($routeProvider) {
  $routeProvider
    .when('/', {
	  controller: 'LoginCtrl',
	  template: 'assets/login/templates/login_page.html'
    }).when('/lobby', {
        controller: 'LobbyCtrl',
        template: 'assets/authtoy/templates/lobby.html'
    }).when('/publisher/:publisherId/workbench', {
      controller: 'WorkbenchCtrl',
      template: 'assets/authtoy/templates/workbench.html'
    })
    .otherwise({
      redirectTo:'/'
    });
});
