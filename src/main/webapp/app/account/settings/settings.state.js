(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('settings', {
            parent: 'account',
            url: '/settings',
            cache: false,
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'global.menu.account.settings'
            },
            views: {
                'content@': {
                    templateUrl: 'app/account/settings/settings.html?release=4.1',
                    controller: 'SettingsController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('settings');
                    return $translate.refresh();
                }]
            }
        })
        .state('settings.phoneValidation', {
            url: '/{login}/phoneValidation',
            params: {
            	phoneNumber: null
            },
            onEnter: ['$stateParams', '$state', '$uibModal', '$http', 'ServerURL', function($stateParams, $state, $uibModal, $http, ServerURL) {
            	$http({
          		  method: 'GET',
          		  url: ServerURL + 'api/reSendValidationPhone'
          		}).then(function() {
          			console.log("Codigo de validacion de celular enviado");
          			
          			$uibModal.open({
                        templateUrl: 'app/account/register/phone-validation-dialog.html?release=4.1',
                        controller: 'PhoneValidationDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg'
                    }).result.then(function() {
                    	location.replace("/#/settings");
                		location.reload();
                    }, function() {
                        $state.go('^');
                    });
          		});
            }]
        });
    }
})();
