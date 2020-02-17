(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .factory('MobileControlVersionService', MobileControlVersionService);

    MobileControlVersionService.$inject = ['ServerURL', '$http', '$state'];

    function MobileControlVersionService(ServerURL, $http, $state) {
    	
        var service = {
        		isValidVersion: isValidVersion
            };
        
        return service;

            function isValidVersion(successCallback, errorCallback) {
            		
        		cordova.getAppVersion.getVersionNumber(function (version) {
            		
                    $http({method: 'HEAD', url: ServerURL + "api/mobileVersion/isValid" + "?version=" + version + "&platform=" + device.platform.toUpperCase()}).
        	            success(function(currentVersion, status, headers) {
        	            	if(successCallback){
        	            		successCallback();
        	            	}
        	            }).error(function(data, status, headers, config) {
                    	
        		        navigator.notification.confirm(
        		        		"Es Necesario una actualización de la aplicación. ¿ Desea ir a la Tienda?", 
        		                function(button){
        		        			if(button == "1"){
        		    			    	cordova.plugins.market.open('ar.com.pmpbsolutions.subastados');
        		        			   }else{
        		        		           $state.go('home');
        		        		           if(errorCallback){
        		        		        	   errorCallback();
        		        		           }
        		        		           navigator.app.exitApp();
        		        		           
        		        			   }
        		        		}, 
        		                'Actualización Requerida', 
        		                'Si,No'  
        		         );
                    });
            
            	});
            }

    }
})();
