(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('sessions', {
            parent: 'account',
            url: '/sessions',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'global.menu.account.sessions'
            },
            views: {
                'content@': {
                    templateUrl: 'app/account/sessions/sessions.html?release=4.1',
                    controller: 'SessionsController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('sessions');
                    return $translate.refresh();
                }]
            }
        });
    }
})();
