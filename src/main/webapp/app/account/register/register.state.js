(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('register', {
            parent: 'account',
            url: '/register',
            data: {
                authorities: [],
                pageTitle: 'register.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/account/register/register-step-first.html?release=4.1',
                    controller: 'RegisterController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('register');
                    return $translate.refresh();
                }]
            }
        })
        .state('register-second-step', {
            parent: 'register',
            url: '/2',
            params: {
            	firstStepAccount: null
            },
            data: {
                authorities: [],
                pageTitle: 'register.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/account/register/register-step-second.html?release=4.1',
                    controller: 'RegisterController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('register');
                    return $translate.refresh();
                }]
            }
        });
//        .state('register.phoneValidation', {
//            url: '/{login}/phoneValidation',
//            params: {
//            	phoneNumber: null
//            },
//            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
//                $uibModal.open({
//                    templateUrl: 'app/account/register/phone-validation-dialog.html?release=4.1',
//                    controller: 'PhoneValidationDialogController',
//                    controllerAs: 'vm',
//                    backdrop: 'static',
//                    size: 'lg'
//                }).result.then(function() {
//                    $state.go('register', null, { reload: false });
//                }, function() {
//                    $state.go('^');
//                });
//            }]
//        });
    }
})();
