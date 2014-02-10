angular.module('authtoy', ['ngRoute']).value(
    'key', 'value'
).factory('userContextSvc', [ '$q', function($q) {
    return function() {
        return {
            var nextUpdate = $q.defer(); 
            var userContext = {
                user: null;
                updatePromise: nextUpdate.promise()
            };

            getUserContext: function getUserContext() {
                return angular.clone(userContext);
            }

            function updateUserContext(userInfo) {
                var lastPromise = userContext.updatePromise;
                nextUpdate = $q.defer();
                userContext = {
                    user: userInfo;
                    updatePromise: $q.defer();
                };
                lastPromise.resolve(userContext);
           }

            // For use by the controller that performs login/logout
            onUserLogin: function onUserLogin(userInfo) {
                updateUserContext(userInfo);
            }
            onUserLogout: function onUserLogout() {
                updateUserContext(null);
        };
    })
}])
).factory('acctCtxtSvc', [ '$q', 'userCtxtSvc', function($q, userContext) {
    return function() {
        return {
            var nextUpdate = $q.defer(); 
            var accountContext = {
                accounts: [];
                activeAccount: null;
                updatePromise: nextUpdate.promise()
            };

            // User Context dependency.
            function onUserChange(newContext) {
                if( userContext.user !== newContext.user ) {
                    if( newContext.user == null ) {
                        updateAccountContext( [], null )
                    } else {
                        // Service call TBD;
                        result = {accountList: [], defaultAccount: null};
                        updateAccountContext( result.accountList, result.defaultAccount );
                    }
                }

                return newContext;
            }

            var userContext = userContext.getUserContext();
            userContext.updatePromise.then(onUserChange());

            getUserContext: function getUserContext() {
                return angular.clone(userContext);
            }

            function updateUserContext(accountList, activeAccount) {
                var lastPromise = accountContext.updatePromise;
                nextUpdate = $q.defer();
                accountContext = {
                    accounts: accountList;
                    activeAccount: activeAccount;
                    updatePromise: nextUpdate.promise();
                };
                lastPromise.resolve(userContext);
           }

            onAccountChange: function onUserLogin(accountList, activeAccount) {
                updateUserContext(userInfo);
            }
        };
    })
}])
            
            getAccountList: function getAccountList() {
                return angular.clone(accountList);
            }

            hasCurrentAccount: function hasCurrentAccount() { return isAuthenticated && this.activeAccount != null; }

            getCurrentAccount: function getCurrentAccount() { return angular.clone(activeAccount) );

            onAuthenticate: function onAuthenticate( user, accountList ) { }
              
            onAccountChange: function onAccountChange( accountHref ) { }
        };
    })
}])
.controller('ToolbarCtrl', ['$scope', , function($scope) {
    $scope.greeting = 'Hola!';
}]);
(
 
.config(function($routeProvider) {
  $routeProvider
    .when('/', {
      controller: 'LobbyCtrl',
      template: 'lobby.html'
    })
    .when('/publisher/:publisherId/workbench', {
      controller: 'WorkbenchCtrl',
      template: 'workbench.html'
    })
    .otherwise({
      redirectTo:'/'
    });
})