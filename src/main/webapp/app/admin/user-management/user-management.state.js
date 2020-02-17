(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('user-management', {
            parent: 'admin',
            url: '/user-management', //?page&sort',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'userManagement.home.title'
            },
            params: {
            	filter: null
            },
            views: {
                'content@': {
                    templateUrl: 'app/admin/user-management/user-management.html?release=4.1',
                    controller: 'UserManagementController',
                    controllerAs: 'vm'
                }
            },
//            params: {
//                page: {
//                    value: '1',
//                    squash: true
//                },
//                sort: {
//                    value: 'id,asc',
//                    squash: true
//                }
//            },
            resolve: {
//                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
//                    return {
//                        page: PaginationUtil.parsePage($stateParams.page),
//                        sort: $stateParams.sort,
//                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
//                        ascending: PaginationUtil.parseAscending($stateParams.sort)
//                    };
//                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('user-management');
                    return $translate.refresh();
                }]

            }        })
        .state('user-management.new', {
            url: '/new',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            params: {
            	filter: null
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/admin/user-management/user-management-dialog.html?release=4.1',
                    controller: 'UserManagementDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                id: null, login: null, firstName: null, lastName: null, email: null,
                                activated: true, langKey: null, createdBy: null, createdDate: null,
                                lastModifiedBy: null, lastModifiedDate: null, resetDate: null,
                                resetKey: null, authorities: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('user-management', { filter: $state.params.filter}, { reload: true });
                }, function() {
                    $state.go('user-management');
                });
            }]
        })
        .state('user-management.edit', {
            url: '/{login}/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            params: {
            	filter: null
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/admin/user-management/user-management-dialog.html?release=4.1',
                    controller: 'UserManagementDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['User', function(User) {
                            return User.get({login : $stateParams.login}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-management', { filter: $state.params.filter}, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        }).state('user-management.activity', {
            url: '/activityEvent/{login}?page',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                }
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/admin/user-management/user-management-activity-dialog.html?release=4.1',
                    controller: 'UserManagementActivityDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                    	pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                            return {
                                page: PaginationUtil.parsePage($stateParams.page)
                            };
                        }],
                        entity: ['User', function(User) {
                            return User.get({login : $stateParams.login}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-management', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('user-management.new-notification', {
        	url: '/newMessage',
        	data: {
        		authorities: ['ROLE_ADMIN']
        	},
        	params: {
        		selectedUsers: null
            },
        	onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
        		$uibModal.open({
        			templateUrl: 'app/admin/user-management/user-management-new-notification.html?release=4.1',
        			controller: 'UserManagementNewNotificationController',
        			controllerAs: 'vm',
        			backdrop: 'static',
        			size: 'lg',
        			resolve: {
        				mailTemplates: ['MessageTemplate', function(MessageTemplate) {
        					return MessageTemplate.findTop10({type: 'MAIL'}).$promise;
        				}],
        				phoneTemplates: ['MessageTemplate', function(MessageTemplate) {
        					return MessageTemplate.findTop10({type: 'PHONE'}).$promise;
        				}]
        			}
        		}).result.then(function() {
        			$state.go('user-management', null, { reload: true });
        		}, function() {
        			$state.go('^');
        		});
        	}]
        })
        .state('user-management-detail', {
            parent: 'user-management',
            url: '/{login}',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'user-management.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/admin/user-management/user-management-detail.html?release=4.1',
                    controller: 'UserManagementDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('user-management');
                    return $translate.refresh();
                }]
            }
        })
        .state('user-management.delete', {
            url: '/{login}/delete',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            params: {
            	filter: null
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/admin/user-management/user-management-delete-dialog.html?release=4.1',
                    controller: 'UserManagementDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['User', function(User) {
                            return User.get({login : $stateParams.login}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-management', { filter: $state.params.filter}, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }
})();
