(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .factory('LogsService', LogsService);

    LogsService.$inject = ['$resource', 'ServerURL'];

    function LogsService ($resource, ServerURL) {
        var service = $resource(ServerURL + 'management/logs', {}, {
            'findAll': { method: 'GET', isArray: true},
            'changeLevel': { method: 'PUT', withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=utf-8',
                    'Access-Control-Allow-Origin': true,
                    'X-Requested-With': 'XMLHttpRequest'
                }}
        });

        return service;
    }
})();
