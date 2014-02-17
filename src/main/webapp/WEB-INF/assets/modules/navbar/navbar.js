angular.module('navbar', ['ngRoute']).value(
    'key', 'value'
).factory('navBarSvc', [ 'navBarContextSvc', function($q, userContext) {
    return function() {
        return {
            var nextUpdate = $q.defer();
            
            this.namePathMap = angular.clone(namePathMap),
            this.updatePromise = nextUpdate.promise();
            
            this.onNavChange = function onNavChange(newContext) {
                var lastPromise = this.updatePromise;
                var lastUpdate = this.nextUpdate;

                nextUpdate = $q.defer();
                this.namePathMap = angular.clone(namePathMap),
                this.updatePromise = updatePromise = nextUpdate.promise();
                
                lastPromise.resolve(this);
           };
        };
    };
}]);