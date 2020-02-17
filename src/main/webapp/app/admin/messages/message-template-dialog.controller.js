(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .controller('MessageTemplateDialogController', MessageTemplateDialogController);

    MessageTemplateDialogController.$inject = ['$scope', '$stateParams', '$uibModalInstance', 'MessageTemplate', 'JhiLanguageService'];

    function MessageTemplateDialogController ($scope, $stateParams, $uibModalInstance, MessageTemplate, JhiLanguageService) {
        var vm = this;
        
        vm.isSaving = false;
        
        vm.newTemplate = function(type) {
        	return {
            	id: null,
            	type: type,
            	name: '',
            	subject: '',
            	body: ''
            };
        };

        ////////////////	MAIL TEMPLATES	///////////////

        vm.mailPage = 1;
        vm.mailTotalItems = null;
    	vm.mailItemsPerPage = 10;

    	vm.mailTemplates = [];
    	vm.mailTemplate = vm.newTemplate('MAIL');
    	loadAllMailTemplates(true);
        
        function loadAllMailTemplates(selectFirst) {
        	MessageTemplate.findAll({type: 'MAIL'}, function (response){
        		vm.mailTemplates = response;
        		vm.mailTotalItems = response.length;
        		if(response.length > 0 && selectFirst)
        			vm.mailTemplate = angular.copy(response[0]);
        	});
        }
        
        vm.deleteMailTemplate = function (id) {
        	Sessions.delete({id: id},
    			function () {
	        		vm.error = null;
	        		vm.success = 'OK';
	        		loadAllMailTemplates(false);
	        	},
	        	function () {
	        		vm.success = null;
	        		vm.error = 'ERROR';
	        	}
        	);
        };

        ////////////////	PHONE TEMPLATES	///////////////

        vm.phonePage = 1;
        vm.phoneTotalItems = null;
    	vm.phoneItemsPerPage = 10;

    	vm.phoneTemplates = [];
    	vm.phoneTemplate = vm.newTemplate('PHONE');
    	loadAllPhoneTemplates(true);
        
        function loadAllPhoneTemplates(selectFirst) {
        	MessageTemplate.findAll({type: 'PHONE'}, function(response) {
        		vm.phoneTemplates = response;
        		vm.phoneTotalItems = response.length;
        		if(response.length > 0 && selectFirst)
        			vm.phoneTemplate = angular.copy(response[0]);
    		});
        }
        
        vm.deletePhoneTemplate = function (id) {
            Sessions.delete({id: id},
                function () {
                    vm.error = null;
                    vm.success = 'OK';
                    loadAllPhoneTemplates(false);
                },
                function () {
                    vm.success = null;
                    vm.error = 'ERROR';
                }
            );
        };
        
        ////////////////	BOTH	///////////////

        
        vm.saveOrUpdate = function (template) {
            vm.isSaving = true;
            if (template.id !== null) {
            	MessageTemplate.update(template, function() {
            		vm.isSaving = false;
            		if(template.type == 'MAIL') 
            			loadAllMailTemplates(false);
            		else 
            			loadAllPhoneTemplates(false);
            	}, onSaveError);
            } else {
            	MessageTemplate.save(template, function() {
            		vm.isSaving = false;
            		if(template.type == 'MAIL') 
            			loadAllMailTemplates(true);
            		else 
            			loadAllPhoneTemplates(true);
            	}, onSaveError);
            }
        };

        function onSaveError () {
            vm.isSaving = false;
            alert('Error al guardar');
        }
        
        vm.clear = function () {
            $uibModalInstance.dismiss('cancel');
        };
        
        vm.copy = function (soruce, destination) {
        	angular.copy(soruce, destination);
        };
    }
})();
