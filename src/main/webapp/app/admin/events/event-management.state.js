(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('event-management', {
            parent: 'admin',
            url: '/event-management?page&sort',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'eventManagement.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/admin/events/event-management.html?release=4.1',
                    controller: 'EventManagementController',
                    controllerAs: 'vm'
                }
            }, params: {
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
                    $translatePartialLoader.addPart('event-management');
                    return $translate.refresh();
                }]

           }})
       .state('event-management.new', {
            url: '/new',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/admin/events/event-management-dialog.html?release=4.1',
                    controller: 'EventManagementDialogController',
                    controllerAs: 'vm',
                    windowClass: 'event-management-modal-window',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                id: null, name: null, initDate: null, endDate: null, type: 'INDEPENDIENTE'
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('event-management', null, { reload: true });
                }, function() {
                    $state.go('event-management');
                });
            }]
        })
        .state('event-management.edit', {
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/admin/events/event-management-dialog.html?release=4.1',
                    controller: 'EventManagementDialogController',
                    controllerAs: 'vm',
                    windowClass: 'event-management-modal-window',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['EventManagement', function(EventManagement) {
                            return EventManagement.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('event-management', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        }).state('event-management.delete', {
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/admin/events/event-management-delete-dialog.html?release=4.1',
                    controller: 'EventManagementDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                    	entity: ['EventManagement', function(EventManagement) {
                            return EventManagement.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('event-management', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }
})();
