(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .factory('AuthServerProvider', AuthServerProvider);

    AuthServerProvider.$inject = ['$http', '$localStorage' , 'JhiTrackerService', 'ServerURL', 'EventTrackerService', 'ReloadEventService', 'FinishLoteService'];

    function AuthServerProvider ($http, $localStorage , JhiTrackerService, ServerURL, EventTrackerService, ReloadEventService, FinishLoteService) {
        var service = {
            getToken: getToken,
            hasValidToken: hasValidToken,
            login: login,
            logout: logout
        };

        return service;

        function getToken () {
            var token = $localStorage.authenticationToken;
            return token;
        }

        function hasValidToken () {
            var token = this.getToken();
            return !!token;
        }

        function login (credentials) {
        	
            var data = 'j_username=' + encodeURIComponent(credentials.username) +
                '&j_password=' + encodeURIComponent(credentials.password) +
                '&remember-me=' + credentials.rememberMe + '&submit=Login';

            return $http.post(ServerURL + 'api/authentication', data, {
         	   withCredentials: true,
               headers: {
                   'Content-Type': 'application/x-www-form-urlencoded',
                   'Access-Control-Allow-Origin': true,
                   "X-Requested-With": "XMLHttpRequest"
               }
            }).success(function (response) {

        		var app = document.URL.indexOf( 'http://' ) === -1 && document.URL.indexOf( 'https://' ) === -1;
        		if ( app ) {
        			// Envio la registracion del dispostivo. Solo si es la aplicación mobile
        			$http.post(ServerURL + 'api/deviceRegistration/', credentials.deviceParameters, {
                	   withCredentials: true,
                  	   headers : {
              		      	   'Content-Type': 'application/json; charset=utf-8',
              		            'Access-Control-Allow-Origin': true,
              		            "X-Requested-With": "XMLHttpRequest"
              		        }
                  	}).success(function (response) {
                       	 return response;
                       });
        		}     		
            	
                return response;
            });
        }

        function logout () {
            JhiTrackerService.disconnect();
            EventTrackerService.disconnect();
            ReloadEventService.disconnect();
            FinishLoteService.disconnect();

            
            // logout from the server
            $http.post(ServerURL + 'api/logout').success(function (response) {
                delete $localStorage.authenticationToken;
                // to get a new csrf token call the api
                $http.get(ServerURL + 'api/account');
            
                return response;
            });
            
        }
    }
})();
