(function () {
    'use strict';

    angular
        .module('subastadosApp')
        .factory('Message', Message);

    Message.$inject = ['$resource', 'ServerURL'];

    function Message ($resource, ServerURL) {
        var service = $resource(ServerURL + 'api/messages/:id', {}, {
            'query': {method: 'GET', isArray: true},
            'get': {method: 'GET', isArray: false},
            'save': { method:'POST', withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=utf-8',
                    'Access-Control-Allow-Origin': true,
                    'X-Requested-With': 'XMLHttpRequest'
                }
            }
        });

        return service;
    }
})();
