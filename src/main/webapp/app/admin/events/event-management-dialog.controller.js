(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .controller('EventManagementDialogController',EventManagementDialogController);

    EventManagementDialogController.$inject = ['$stateParams', '$uibModalInstance', 'entity', 'EventManagement'];

    function EventManagementDialogController ($stateParams, $uibModalInstance, entity, EventManagement) {
        var vm = this;

        vm.clear = clear;
        vm.save = save;
        vm.event = entity;
        vm.lotes = vm.event.lotes || [];
        vm.isSaving = false;
        
        vm.dateTimeFormat = 'yyyy/MM/dd HH:mm';
        vm.event.initDate = moment(vm.event.initDate);
        vm.event.endDate = moment(vm.event.endDate);
        
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

        function cleanDate(date) { //moment
        	return date.seconds(0).milliseconds(0);
        }
        
        function save () {
        	vm.isSaving = true;
        	cleanDate(vm.event.initDate);
        	cleanDate(vm.event.endDate);
        	
        	vm.event.lotes = vm.lotes;
            if (vm.event.id !== null) {
            	EventManagement.update(vm.event, onSaveSuccess, onSaveError);
            } else {
            	EventManagement.save(vm.event, onSaveSuccess, onSaveError);
            }
        }
        
        ////////////////	LOTES	///////////////
        
        vm.newLote = function(type) {
        	var lote = {
            	id: null,
            	orden: vm.lotes.length + 1,
            	product: {
            		$idcaballo: '',
            		$nombre: ''
            	},
            	initialPrice: 5000,
            	minutesToIncrement: 3,
            	incrementPeriod: 3
            };
        	
        	vm.lotes.push(lote);
        	vm.totalItems = vm.lotes.length;
        	
        	vm.lote = lote;
        };

        vm.page = 1;
        vm.totalItems = vm.lotes.length;
    	vm.itemsPerPage = 4;

    	vm.lote = {};
        
        vm.deleteLote = function (index, lote) {
        	var r = confirm("Desea borrar el lote: " + lote.orden);
        	if (r == true) {
        		var realIndex = vm.itemsPerPage * (vm.page - 1) + index;
        		vm.lotes.splice(realIndex, 1);
        		
        		vm.totalItems = vm.lotes.length;
        		
        		var selectedIndex = realIndex - 1 < 0 ? 0 : realIndex - 1;
        		vm.lote = vm.lotes[selectedIndex];
        	}
        };
        
        vm.copy = function (soruce, destination) {
        	angular.copy(soruce, destination);
        };
        
    }
})();
