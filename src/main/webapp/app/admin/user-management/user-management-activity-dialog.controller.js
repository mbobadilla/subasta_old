(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .controller('UserManagementActivityDialogController',UserManagementActivityDialogController);

    UserManagementActivityDialogController.$inject = ['$stateParams', '$uibModalInstance', 'entity', 'User', 'JhiLanguageService', 'pagingParams', '$state', 'paginationConstants', 'UserActivity', 'ParseLinks'];

    function UserManagementActivityDialogController ($stateParams, $uibModalInstance, entity, User, JhiLanguageService, pagingParams, $state, paginationConstants, UserActivity, ParseLinks) {
        var vm = this;
        
        vm.clear = clear;
        vm.user = entity;
        vm.countryName = null;
        
        vm.activities = [];
        vm.loadAll = loadAll;
        vm.page = 1;
        vm.totalItems = null;
        vm.loadPage = loadPage;
        vm.itemsPerPage = 20;
        vm.transition = transition;
        vm.search = search;

        vm.loadAll();
        
        function loadAll () {
        	UserActivity.query({
        		login: vm.user.login,
                page: vm.page - 1,
                types: vm.selected
            }, onSuccess, onError);
        }
        
        function onSuccess(data, headers) {
            vm.links = ParseLinks.parse(headers('link'));
            vm.totalItems = headers('X-Total-Count');
            vm.queryCount = vm.totalItems;
            vm.activities = data;
        }

        function onError(error) {
            AlertService.error(error.data.message);
        }
        
        function loadPage (page) {
            vm.page = page;
            vm.transition();
        }

        function transition () {
        	vm.loadAll();
        }
        
        function search () {
        	vm.loadPage(1);
        }
        
        vm.items = [{
        	type: 'BID',
        	detail: 'Oferta'
        }, {
        	type: 'PURCHASE',
        	detail: 'Compra'
        }, {
        	type: 'EVENT',
        	detail: 'Participacion'
        }, {
        	type: 'PRODUCT_VIEW',
        	detail: 'Producto visto'
        }, {
        	type: 'MESSAGE',
        	detail: 'Mensaje'
        }, {
        	type: 'DELETE_BID',
        	detail: 'Oferta eliminada'
        }];
        
        vm.all = ['BID','PURCHASE','EVENT','PRODUCT_VIEW','MESSAGE', 'DELETE_BID'];
        vm.selected = ['BID','PURCHASE','EVENT','PRODUCT_VIEW','MESSAGE', 'DELETE_BID'];

        vm.toggle = function (item, list) {
          var idx = list.indexOf(item);
          if (idx > -1) {
            list.splice(idx, 1);
          }
          else {
            list.push(item);
          }
          
          if(list.length == 0) { // Si no hay seleccionados, vuelvo a seleccionar a todos
        	  list.push.apply(list, vm.all);
          }
        };

        vm.exists = function (item, list) {
          return list.indexOf(item) > -1;
        };
        
        // -------------------------------------------------

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        vm.countryName = getCountryName(vm.user.userCountry);
        
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
