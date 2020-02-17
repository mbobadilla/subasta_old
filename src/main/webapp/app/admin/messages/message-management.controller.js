(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .controller('MessageManagementController', MessageManagementController);

    MessageManagementController.$inject = ['Principal', 'Message', 'ParseLinks', 'AlertService', '$state', 'pagingParams', 'paginationConstants', 'JhiLanguageService', 'ServerURL', '$http'];

    function MessageManagementController(Principal, Message, ParseLinks, AlertService, $state, pagingParams, paginationConstants, JhiLanguageService, ServerURL, $http) {
        var vm = this;

        vm.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
        vm.currentAccount = null;
        vm.loadAll = loadAll;
        vm.messages = [];
        vm.page = 1;
        vm.totalItems = null;
        vm.links = null;
        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.transition = transition;

        vm.loadAll();
        
        Principal.identity().then(function(account) {
            vm.currentAccount = account;
        });

        function loadAll () {
        	Message.query({
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
        }

        function onSuccess(data, headers) {
            vm.links = ParseLinks.parse(headers('link'));
            vm.totalItems = headers('X-Total-Count');
            vm.queryCount = vm.totalItems;
            vm.page = pagingParams.page;
            vm.messages = data;
        }

        function onError(error) {
            AlertService.error(error.data.message);
        }

        function sort () {
            var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
            if (vm.predicate !== 'id') {
                result.push('id');
            }
            return result;
        }

        function loadPage (page) {
            vm.page = page;
            vm.transition();
        }

        function transition () {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                search: vm.currentSearch
            });
        }
        
        vm.retry = function(id) {
        	$http({
    			method: 'GET',
    			url: ServerURL + "api/messages/" + id + "/retry"
    	  	}).then(function() {
    	  		location.reload();
    	  	}, function(response) {
    	  		console.log("Error al reintentar mensaje "+ id);
      		});
        }
    }
})();
