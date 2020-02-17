(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .factory('LoginService', LoginService);

    LoginService.$inject = ['$uibModal', 'MobileControlVersionService'];

    function LoginService ($uibModal, MobileControlVersionService) {
        var service = {
            open: open
        };

        var modalInstance = null;
        var resetModal = function () {
            modalInstance = null;
        };

        return service;

        function open () {
        	
        	console.log(MobileControlVersionService);
    		var app = document.URL.indexOf( 'http://' ) === -1 && document.URL.indexOf( 'https://' ) === -1;
    		if ( app ) {

    			MobileControlVersionService.isValidVersion(function(){
        			showLoginPopup();
    			});
    			
    		}else {
    			showLoginPopup();
    		}

        }
        
        function showLoginPopup(){
        	
            if (modalInstance !== null) return;
            modalInstance = $uibModal.open({
                animation: true,
                templateUrl: 'app/components/login/login.html?release=4.1',
                controller: 'LoginController',
                controllerAs: 'vm',
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('login');
                        return $translate.refresh();
                    }]
                }
            });
            modalInstance.result.then(
                resetModal,
                resetModal
            );
        }
    }
})();
