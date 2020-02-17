(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .controller('MessageManagementDialogController', MessageManagementDialogController);

    MessageManagementDialogController.$inject = ['$scope', '$stateParams', '$uibModalInstance', 'entity'];

    function MessageManagementDialogController ($scope, $stateParams, $uibModalInstance, entity) {
        var vm = this;

        vm.clear = clear;
        vm.message = entity;
        
        vm.page = 1;
        vm.totalItems = vm.message.destinataries.length;
    	vm.itemsPerPage = 5;
        
    	vm.completeness = 10; // FIXME
        ////////////////////////////////
//        vm.message = {
//    		frecuency: 15,
//    		state: 'En curso',
//    		completeness: 30
//        };
        
        
        


        function clear () {
            $uibModalInstance.dismiss('cancel');
        }


    }
})();
