(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .controller('UserManagementController', UserManagementController);

    UserManagementController.$inject = ['Principal', 'User', 'UserFilter', 'ParseLinks', 'AlertService', '$state', 'JhiLanguageService', '$http', 'ServerURL', '$mdDialog'];

    function UserManagementController(Principal, User, UserFilter, ParseLinks, AlertService, $state, JhiLanguageService, $http, ServerURL, $mdDialog) {
        var vm = this;

        vm.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
        vm.currentAccount = null;
        vm.languages = null;
        vm.search = search;
        vm.loadEvents = loadEvents;
        vm.clearSearch = clearSearch;
        vm.setActive = setActive;
        vm.users = [];
        vm.filter = $state.params.filter || getDefaultFilter();
        vm.events = [];
        
        vm.selected = [];
        vm.allSelected = false;
        vm.checkAll = checkAll;
        vm.uncheckAll = uncheckAll;
        vm.changeAll = changeAll;
        
        vm.clear = clear;
        vm.links = null;
        
        vm.predicate = "";
        vm.reverse = "";
        vm.transition = transition;
        vm.sendNotification = sendNotification;

        vm.loadEvents();
        vm.search();
        vm.setBlocked = setBlocked;
        
        function getDefaultFilter() {
        	return {
        		participant: true,
        		validatedEmail: true,
        		notValidatedEmail: true,
        		validatedPhone: true,
        		notValidatedPhone: true,
        		activated: true,
        		blocked: false
        	}
        }
        
        
        function loadEvents () {
        	$http({
    			method: 'GET',
    			url: ServerURL + 'api/event/combo',
    			isArray: true
    		}).then(function(response) {
    			vm.events = response.data;
    		});
        }
        
        JhiLanguageService.getAll().then(function (languages) {
            vm.languages = languages;
        });
        Principal.identity().then(function(account) {
            vm.currentAccount = account;
        });

        function setBlocked (user, isBlocked) {
        	
        	var confirm = $mdDialog.confirm()
			.title("Desea " + (isBlocked ? "bloquear" : "desbloquear") + " al usuario: " + user.login)
			.ok('Aceptar')
			.cancel('Cancelar');

			$mdDialog.show(confirm).then(function(result) {
				if(isBlocked) {
                	$http({
            			method: 'PATCH',
            			url: ServerURL + 'api/users/' + user.login + '/bloquear',
            		}).then(function(response) {
            			console.log('Usuario ' + user.login + ' bloqueado correctamente');
            			user.bloqueado = isBlocked;
            		});
                } else {
                	$http({
            			method: 'PATCH',
            			url: ServerURL + 'api/users/' + user.login + '/desbloquear',
            		}).then(function(response) {
            			console.log('Usuario ' + user.login + ' desbloqueado correctamente');
            			user.bloqueado = isBlocked;
            		});
                }
		      	
		    }, function() {
		    	console.log('Cancelada la solicitud de borrar la ultima oferta del lote: ' + loteId);
		    });
        }
        
        function setActive (user, isActivated) {
            user.activated = isActivated;
            User.update(user, function () {
                vm.loadAll();
                vm.clear();
            });
        }

        function search () {
        	vm.uncheckAll();
        	UserFilter.query(vm.filter, onSuccess, onError);
        }

        function onSuccess(data, headers) {
            vm.users = data;
        }

        function onError(error) {
            AlertService.error(error.data.message);
        }

        function clearSearch () {
        	vm.filter = getDefaultFilter();
        	search();
        }
        
        function transition () {
        	vm.filter.sortBy = vm.predicate;
        	vm.filter.sortAsc = vm.reverse;
        	search();
	    }

        function clear () {
            vm.user = {
                id: null, login: null, firstName: null, lastName: null, email: null,
                activated: null, langKey: null, createdBy: null, createdDate: null,
                lastModifiedBy: null, lastModifiedDate: null, resetDate: null,
                resetKey: null, authorities: null
            };
        }

        function sort () {
            var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
            if (vm.predicate !== 'id') {
                result.push('id');
            }
            return result;
        }
        
        function checkAll() {
        	vm.selected = angular.copy(vm.users);
        }
        
		function uncheckAll() {
			vm.allSelected = false;
			vm.selected = [];
		}

		function changeAll() {
			if(vm.allSelected)
				vm.checkAll();
			else 
				vm.uncheckAll();
		}
		
        function sendNotification () {
        	if(vm.selected.length == 0) {
        		alert('Debe seleccionar al menos un usuario.');
        		return;
        	}
        	
        	$state.go('.new-notification', {selectedUsers: vm.selected});
        }
    }
})();
