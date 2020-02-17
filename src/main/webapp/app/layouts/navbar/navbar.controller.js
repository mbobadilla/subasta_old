(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .controller('NavbarController', NavbarController);

    NavbarController.$inject = ['$scope','$state', 'Auth', 'Principal', 'ProfileService', 'LoginService', '$http', 'ServerURL','$mdDialog'];

    function NavbarController ($scope, $state, Auth, Principal, ProfileService, LoginService, $http, ServerURL, $mdDialog) {
        var vm = this;

        vm.isNavbarCollapsed = true;
        vm.isAuthenticated = Principal.isAuthenticated;

        vm.showRegister = true;
        vm.appType = document.URL.indexOf( 'http://' ) === -1 && document.URL.indexOf( 'https://' ) === -1;
        
        if(vm.appType){
        	vm.showRegister = !(device.platform === 'iOS');
        }
        
        ProfileService.getProfileInfo().then(function(response) {
            vm.inProduction = response.inProduction;
            vm.swaggerEnabled = response.swaggerEnabled;
        });
        
        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
            });
        }
        
        $scope.$on('authenticationSuccess', function() {
            getAccount();
        });

        vm.login = login;
        vm.logout = logout;
        vm.toggleNavbar = toggleNavbar;
        vm.collapseNavbar = collapseNavbar;
        vm.$state = $state;

        function login() {
            collapseNavbar();
            LoginService.open();
        }

        function logout() {
            collapseNavbar();
            Auth.logout();
            $state.go('home');
        }

        function toggleNavbar() {
            vm.isNavbarCollapsed = !vm.isNavbarCollapsed;
        }

        function collapseNavbar() {
            vm.isNavbarCollapsed = true;
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
	  				console.log("Participacion exitosa");
	  				vm.account.lastEventParticipant = true;
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
