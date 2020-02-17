(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .controller('UserManagementNewNotificationController',UserManagementNewNotificationController);

    UserManagementNewNotificationController.$inject = ['$stateParams', '$uibModalInstance', 'mailTemplates', 'phoneTemplates', 'Message'];

    function UserManagementNewNotificationController ($stateParams, $uibModalInstance, mailTemplates, phoneTemplates, Message) {
        var vm = this;
        vm.selectedUsers = $stateParams.selectedUsers || [];
        vm.clear = clear;
        vm.send = send;
        vm.mailTemplateList = mailTemplates;
        vm.phoneTemplateList = phoneTemplates;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function send (type, selectedId) {
        	var message = {
    			id: null,
    			type: type,
    			messageTemplateDTO: {
    				id: selectedId
    			},
    			destinataries: []
        	};
        	
        	message.destinataries = $stateParams.selectedUsers.map(function(item) { 
    			return {
    				contactId: item.id
    			};
    		});
        	
            vm.isSaving = true;
            Message.save(message, onSaveSuccess, onSaveError);
        }
        
        function onSaveSuccess (result) {
            vm.isSaving = false;
            $uibModalInstance.close(result);
        }

        function onSaveError () {
        	vm.isSaving = false;
        	alert('Error al enviar mensaje');
        }
    }
})();
