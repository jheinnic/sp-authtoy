angular.module('login', []).factory('userContextSvc', ['$q'], function($q) {
    return function() {
        var nextUpdate = $q.defer(); 
        var userContext = {
            user: null,
            updatePromise: nextUpdate.promise()
        };
        
        function updateUserContext(userInfo) {
        	var lastPromise = userContext.updatePromise;
        	nextUpdate = $q.defer();
        	userContext = {
        			user: userInfo,
        			updatePromise: $q.defer()
        	};
        	lastPromise.resolve(userContext);
        }

        return {
            getUserContext: function getUserContext() {
                return angular.clone(userContext);
            },

            // For use by the controller that performs login/logout
            onUserLogin: function onUserLogin(userInfo) {
                updateUserContext(userInfo);
            },
            onUserLogout: function onUserLogout() {
                updateUserContext(null);
            }
        };
    };
}).factory('acctCtxtSvc', [ '$q', 'userCtxtSvc' ], function($q, userContext) {
    return function() {
    	var nextUpdate = $q.defer(); 
    	var accountContext = {
    		accounts: [],
    		activeAccount: null,
    		updatePromise: nextUpdate.promise()
    	};
    	
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
    	
    	function updateAccountContext(accountList, activeAccount) {
    		var lastPromise = accountContext.updatePromise;
    		nextUpdate = $q.defer();
    		accountContext = {
    			accounts: accountList,
    			activeAccount: activeAccount,
    			updatePromise: nextUpdate.promise()
    		};
    		lastPromise.resolve(userContext);
    	}
    	
    	var userContext = userContext.getUserContext();
    	userContext.updatePromise.then(onUserChange());
    	
        return {
            // Account Context dependency.
            getAccountContext: function getAccountContext() {
                return angular.clone(AccountContext);
            },

            onAccountChange: function onUserLogin(accountList, activeAccount) {
                updateAccountContext(userInfo);
            }
        };
    };
});