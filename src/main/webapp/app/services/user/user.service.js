(function () {
    'use strict';

    angular
        .module('subastadosApp')
        .factory('User', User);

    User.$inject = ['$resource', 'ServerURL'];

    function User ($resource, ServerURL) {
        var service = $resource(ServerURL + 'api/users/:login', {}, {
            'query': {method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'save': { method:'POST', withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=utf-8',
                    'Access-Control-Allow-Origin': true,
                    'X-Requested-With': 'XMLHttpRequest'
                } },
            'update': { method:'PUT', withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=utf-8',
                    'Access-Control-Allow-Origin': true,
                    'X-Requested-With': 'XMLHttpRequest'
                } },
            'delete':{ method:'DELETE'}
        });

        return service;
    }
})();
