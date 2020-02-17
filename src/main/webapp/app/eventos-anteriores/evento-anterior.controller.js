(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .controller('EventoAnteriorController', EventoAnteriorController);
    
    EventoAnteriorController.$inject = ['$scope', '$rootScope','$stateParams', 'Principal', 'LoginService', '$state', '$sce', 'EventoAnterior', '$window',
                                '$http', 'ServerURL', '$translate', 'GeneralConfigurationService'];

    function EventoAnteriorController ($scope, $rootScope, $stateParams, Principal, LoginService, $state, $sce, EventoAnterior, $window, 
    		$http, ServerURL, $translate, GeneralConfigurationService) {
        
    	var vm = this;

        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        vm.register = register;
        var message = "";
        var userNameCountry = "";
        vm.loteMessage = [];
        vm.appType = document.URL.indexOf( 'http://' ) === -1 && document.URL.indexOf( 'https://' ) === -1;
        vm.idEvento = $stateParams.idEvento;
        vm.viewAmountInOffer = true;
        
        GeneralConfigurationService.get({
        	key: 'URL_FOTO_CABALLO'
        }, function(data) {
        	vm.urlFotoCaballo = data.value;
        });
        
        GeneralConfigurationService.get({
        	key: 'URL_CABALLO'
        }, function(data) {
        	vm.urlDetalleCaballo = data.value;
        });
        
        GeneralConfigurationService.get({
        	key: 'VIEW_AMOUNT_IN_OFFER'
        }, function(data) {
        	vm.viewAmountInOffer = (data.value == 'true');
        });
        
        vm.downloadMobileFile = function (eventName, type, fileName) {
            var url = encodeURI(ServerURL + "/eventos/" + eventName + "/" + fileName);

            if(device.platform === 'Android'){
                cordova.InAppBrowser.open(url, '_system');
            }else{
                cordova.InAppBrowser.open(url, '_blank');
            }    		
        };
        
        
        if(vm.idEvento != null){
        	vm.evento = EventoAnterior.query({
        		id: vm.idEvento
        	}, function () {
            	
        		for(var i=0; i < vm.evento.lotes.length; i++) {
            		
            		
            		if(vm.evento.lotes[i].lastBid!=null){
    					userNameCountry = getCountryName(vm.evento.lotes[i].lastBid.userCountry);
    					message = $translate.instant("global.event.messageBid4", { 
    						country: userNameCountry
    					});
    				}else{
    					userNameCountry = getCountryName('AR');
    					message = $translate.instant("global.event.messageBid4", { 
    						country: userNameCountry
    					});
    				}
            		
            		
            		vm.loteMessage[vm.evento.lotes[i].id] = message;
            	}
			});
        	

        }
        
        $scope.$on('authenticationSuccess', function() {
            getAccount();
        });
        
        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }
        
        function register () {
            $state.go('register');
        }
        
    	$scope.showDetail = function(caballoId, loteId) {

    		var app = document.URL.indexOf( 'http://' ) === -1 && document.URL.indexOf( 'https://' ) === -1;
    		if ( app ) {
    			// PhoneGap application Android
          		if(device.platform === 'Android'){
                	$state.go('.productDetail', {idCaballo: caballoId, url: vm.urlDetalleCaballo});
          		}else {
          			//PhoneGap iOS
	    			let position = Math.floor(window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0);
	    			$state.go('.product-detail', {idCaballo: caballoId, position: position, url: vm.urlDetalleCaballo});
          		}
    			
    		}else {
    			//Browser iOS
        		if(navigator.userAgent.match(/(iPod|iPhone|iPad)/)){
	    			let position = Math.floor(window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0);
	    			$state.go('.product-detail', {idCaballo: caballoId, position: position, url: vm.urlDetalleCaballo});
	            }else{
	    			// browser android o pc
	            	$state.go('.productDetail', {idCaballo: caballoId, url: vm.urlDetalleCaballo});
	            }
    			
    		}

    	};
    	
        function getCountryName (countryCode){
    		
    		switch (countryCode) {
	    	    case "AR":
	    	    	name = "Argentina";
	    	        break;
	    	    case "CL":
	    	    	name = "Chile";
	    	        break;
	    	    case "UY":
	    	    	name = "Uruguay";
	    	        break;
	    	    case "PY":
	    	    	name = "Paraguay";
	    	        break;
	    	    case "BO":
	    	    	name = "Bolivia";
	    	        break;
	    	    case "PE":
	    	    	name = "Perú";
	    	        break;
	    	    case "CO":
	    	    	name = "Colombia";
	    	    	break;
	    	    case "EC":
	    	    	name = "Ecuador";
	    	    	break;
	    	    case "MX":
	    	    	name = "México";
	    	    	break;
	    	    case "BR":
	    	    	name = "Brasil";
	    	    	break;
	    	    case "US":
	    	    	name = "Estados Unidos";
	    	    	break;
	    	    case "ES":
	    	    	name = "España";
	    	    	break;
	    	    default: 
	    	    	name = "Argentina";
	    	}
    		
    		return name;
    	}
    	
    }
})();
