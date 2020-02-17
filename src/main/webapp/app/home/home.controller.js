(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'LoginService', '$state', 'HomeService', '$sce', '$mdDialog', '$http', 'ServerURL', '$stateParams', 'carouselImages'];

    function HomeController ($scope, Principal, LoginService, $state, HomeService, $sce, $mdDialog, $http, ServerURL, $stateParams, carouselImages) {
        var vm = this;

        vm.account = null;
        vm.isAuthenticated = null;
        vm.register = register;
        vm.subscriberPerson = {
    		name: "",
    		email: "",
    		cellPhone: ""
        };
        vm.subscribe = subscribe;
        vm.slides = [];
        
        vm.urlSegundaImagen = $stateParams.urlSegundaImagen;
        vm.stateSegundaImagen = $stateParams.stateSegundaImagen;
        vm.exportImage = $stateParams.exportImage;
        vm.exportPdf = $stateParams.exportPdf;
        
        vm.appType = document.URL.indexOf( 'http://' ) === -1 && document.URL.indexOf( 'https://' ) === -1;
        
    	// Seteo imagenes carousel relativas a la url ppal	  
		var elements = [];
		var i = 0;
		carouselImages.data.forEach(function(element) {
			console.log(element);
			elements.push({
				image: encodeURI(ServerURL + $stateParams.imagesFolder + element),
				id: i
			});
			i++;
		});
		
		Array.prototype.push.apply(vm.slides, elements);
		// FIN
        
        
        $scope.$on('authenticationSuccess', function() {
            getAccount();
        });
        
        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }
        
        getAccount();
        
        $scope.downloadMobleFile = function () {
            var url = ServerURL + "/eventos/activo/" + $stateParams.mobileExportName + ".pdf";
            
      		if(device.platform === 'Android'){
                cordova.InAppBrowser.open(url, '_system');
      		}else{
                cordova.InAppBrowser.open(url, '_blank');
      		}
        };
        
        function register () {
            $state.go('register');
        }
        
        function subscribe () {
	        vm.error = null;
	
	        HomeService.saveSubscriber(vm.subscriberPerson).then(function () {
	            vm.success = 'OK';
	        }).catch(function (response) {
	            vm.success = null;
	            if (response.status === 400) {
	                vm.error = 'ERROR';
	            }
	        });
        }
        
    	$scope.participar = function() {
    		
    		var confirm = $mdDialog.confirm()
				.title('Desea participar del evento '+ vm.account.lastEvent + '?')
				.textContent('Con dicha participación, usted también acepta las condiciones del evento')
				.ariaLabel('Lucky day')
				.ok('Aceptar')
				.cancel('Cancelar');

    		$mdDialog.show(confirm).then(function(result) {
    	    	console.log('El usuario ' + vm.account.login + ' confirma participacion');
    	    	
    	      	$http({
	  				method: 'GET',
	  				url: ServerURL + 'api/participate/' + vm.account.login
  			  	}).then(function() {
  			  		vm.account.lastEventParticipant = true;
	  				console.log("Participacion exitosa");
	  		  	}, function(data) {
	  				console.log("Error al registrar participacion");
	  				console.log(data);
  		  		});
    	    }, function() {
    	    	console.log('Cancelada la solicitud de participacion: ' + m.account.login);
    	    });
    	};
    	
    }
})();
