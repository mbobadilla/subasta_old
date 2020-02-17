(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('message-management', {
            parent: 'admin',
            url: '/message?page&sort',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'messageManagement.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/admin/messages/message-management.html?release=4.1',
                    controller: 'MessageManagementController',
                    controllerAs: 'vm'
                }
            },            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                }
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort)
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('message-management');
                    return $translate.refresh();
                }]

            }})
            .state('message-management.view', {
                url: '/view/{id}',
                data: {
                    authorities: ['ROLE_ADMIN']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/admin/messages/message-management-dialog.html?release=4.1',
                        controller: 'MessageManagementDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: ['Message', function(Message) {
                                return Message.get({id : $stateParams.id}).$promise;
                            }]
                        }
                    }).result.then(function() {
                        $state.go('message-management', null, { reload: true });
                    }, function() {
                        $state.go('message-management');
                    });
                }]
            })
            .state('message-management.new-template', {
            	url: '/new-template',
            	data: {
            		authorities: ['ROLE_ADMIN']
            	},
            	onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
            		$uibModal.open({
            			templateUrl: 'app/admin/messages/message-template-dialog.html?release=4.1',
            			controller: 'MessageTemplateDialogController',
            			controllerAs: 'vm',
            			backdrop: 'static',
            			size: 'lg'
            		}).result.then(function() {
            			$state.go('message-management', null, { reload: true });
            		}, function() {
            			$state.go('message-management');
            		});
            	}]
            })
    }
})();
