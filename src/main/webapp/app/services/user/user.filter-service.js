(function () {
    'use strict';

    angular
        .module('subastadosApp')
        .factory('UserFilter', UserFilter);

    UserFilter.$inject = ['$resource', 'ServerURL'];

    function UserFilter ($resource, ServerURL) {
        var service = $resource(ServerURL + 'api/users/search', {}, {
            'query': { method:'POST', isArray: true, withCredentials: true,
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
