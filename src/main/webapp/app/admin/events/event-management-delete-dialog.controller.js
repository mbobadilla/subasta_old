(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .controller('EventManagementDeleteController', EventManagementDeleteController);

    EventManagementDeleteController.$inject = ['$uibModalInstance', 'entity', 'EventManagement'];

    function EventManagementDeleteController ($uibModalInstance, entity, EventManagement) {
        var vm = this;

        vm.event = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
        	EventManagement.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
