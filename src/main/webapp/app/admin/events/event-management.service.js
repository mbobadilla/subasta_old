(function () {
    'use strict';

    angular
        .module('subastadosApp')
        .factory('EventManagement', EventManagement);

    EventManagement.$inject = ['$resource', 'ServerURL'];

    function EventManagement ($resource, ServerURL) {
        var service = $resource(ServerURL + 'api/event-management/:id', {}, {
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
