(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .controller('ProductBidDialogController',ProductBidDialogController);

    ProductBidDialogController.$inject = ['$stateParams', '$uibModalInstance', '$http', 'ServerURL', '$timeout'];

    function ProductBidDialogController ($stateParams, $uibModalInstance, $http, ServerURL, $timeout) {
        var vm = this;

        vm.clear = clear;
        vm.save = save;
        vm.price = $stateParams.price;
        vm.nombre = $stateParams.nombre;
        vm.loteNro = $stateParams.loteNro;
        vm.isError = false;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function onSaveSuccess (result) {
            vm.isSaving = false;
            $uibModalInstance.close(result);
        }

        function onSaveError (error) {
        	console.log(error.data.title);
        	vm.isError = true;
        	vm.error = error.data.title;
            $timeout(function() {
            	$uibModalInstance.dismiss('cancel');
            }, 5000);
        }

        var i = 0;
        function save () {
            if ($stateParams.loteId !== null && $stateParams.userLogin !== '' && $stateParams.price !== null) {
            	vm.isSaving = true;
            	$http.post(ServerURL + 'api/bid/', {
					loteId: $stateParams.loteId,
					userLogin: $stateParams.userLogin,
					price: $stateParams.price
            	}, {
            		withCredentials: true,
                    headers: {
                        'Content-Type': 'application/json; charset=utf-8',
                        'Access-Control-Allow-Origin': true,
                        'X-Requested-With': 'XMLHttpRequest'
                    }
            	}).then(onSaveSuccess, onSaveError);
            }
        }
    }
})();
