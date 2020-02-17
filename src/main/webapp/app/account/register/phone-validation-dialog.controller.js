(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .controller('PhoneValidationDialogController',PhoneValidationDialogController);

    PhoneValidationDialogController.$inject = ['$stateParams', '$uibModalInstance', '$http', 'ServerURL'];

    function PhoneValidationDialogController ($stateParams, $uibModalInstance, $http, ServerURL) {
        var vm = this;

        vm.clear = clear;
        vm.save = save;
        vm.login = $stateParams.login;
        vm.phoneNumber = $stateParams.phoneNumber;
        vm.validationKey = '';

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function onSaveSuccess (result) {
            vm.isSaving = false;
            $uibModalInstance.close(result);
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        function save () {
            vm.isSaving = true;
            if (vm.login !== null && vm.validationKey !== '') {
            	$http({
            		  method: 'GET',
            		  url: ServerURL + 'api/validatePhone',
            		  params: {
            			  login: vm.login,
            			  key: vm.validationKey
            		  }
            		}).then(onSaveSuccess, onSaveError);
            }
        }
    }
})();
