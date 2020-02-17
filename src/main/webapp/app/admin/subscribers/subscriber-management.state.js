(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('subscriber-management', {
            parent: 'admin',
            url: '/subscriber?page&sort',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'subscriberManagement.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/admin/subscribers/subscriber-management.html?release=4.1',
                    controller: 'SubscriberManagementController',
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
                    $translatePartialLoader.addPart('subscriber-management');
                    return $translate.refresh();
                }]

            }        })
    }
})();
