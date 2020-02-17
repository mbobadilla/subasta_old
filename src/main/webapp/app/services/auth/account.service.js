(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .factory('Account', Account);

    Account.$inject = ['$resource', 'ServerURL'];

    function Account ($resource, ServerURL) {
        var service = $resource(ServerURL + 'api/account', {
        }, {
            'get': {
            	method: 'GET',
            	params:{},
                isArray: false,
                interceptor: {
                    response: function(response) {
                        // expose response
                        return response;
                    }
                }
            },
            'save': { method:'POST', withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=utf-8',
                    'Access-Control-Allow-Origin': true,
                    'X-Requested-With': 'XMLHttpRequest'
             } }
        });

        return service;
    }
})();
