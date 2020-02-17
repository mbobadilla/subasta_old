(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .controller('DetalleController', DetalleController);

    DetalleController.$inject = ['$stateParams', '$uibModalInstance'];

    function DetalleController ($stateParams, $uibModalInstance) {
        var vm = this;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();

