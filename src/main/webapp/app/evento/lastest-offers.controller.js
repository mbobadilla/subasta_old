(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .controller('LastestOffersController', LastestOffersController);

    LastestOffersController.$inject = ['OffersService', '$stateParams', '$uibModalInstance'];

    function LastestOffersController(OffersService, $stateParams, $uibModalInstance) {
        var vm = this;

        vm.qty = 10;
        vm.authorities = ['ROLE_ADMIN'];
        vm.nombreCaballo = $stateParams.nombreCaballo;
        vm.loteId = $stateParams.loteId;
        vm.clear = clear;
        
        vm.bids = OffersService.query({
    		loteId: $stateParams.loteId,
            page: 0,
            size: vm.qty
        });
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

    }
})();
