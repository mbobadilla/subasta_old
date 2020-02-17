(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .controller('ParticipateController', ParticipateController);

    ParticipateController.$inject = ['$scope', '$state', '$mdDialog', '$http', 'ServerURL', '$stateParams'];

    function ParticipateController ($scope, $state, $mdDialog, $http, ServerURL, $stateParams) {
        var vm = this;

        $http({
			method: 'GET',
			url: ServerURL + 'api/participate/' + $stateParams.login
	  	}).then(function() {
	  		var textContent = 'Con dicha participación, usted también acepta las condiciones del evento.';
	  		vm.showResponse("Ya se encuentra participando.", textContent);
	  	}, function(response) {
	  		vm.showResponse("Ha ocurrido un error", response.data.title);
  		});
        
    	vm.showResponse = function(response, textContent) {
    		var confirm = $mdDialog.confirm()
				.title(response)
				.textContent(textContent)
				.ariaLabel('Lucky day')
				.ok('Aceptar');

    		$mdDialog.show(confirm).then(function(result) {
    			reload();
    	    });
    	};
    	
    	var reload = function() {
    		location.replace("/#/home");
    		location.reload();
    	};
    }
})();
