(function () {
    'use strict';

    angular
        .module('subastadosApp')
        .factory('UserActivity', UserActivity);

    UserActivity.$inject = ['$resource', 'ServerURL'];

    function UserActivity ($resource, ServerURL) {
        var service = $resource(ServerURL + 'api/activityEvent/:login', {}, {
            'query': {method: 'GET',  params: {}, isArray: true}
        });

        return service;
    }
})();
