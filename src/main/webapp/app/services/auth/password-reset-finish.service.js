(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .factory('PasswordResetFinish', PasswordResetFinish);

    PasswordResetFinish.$inject = ['$resource', 'ServerURL'];

    function PasswordResetFinish($resource, ServerURL) {
        var service = $resource(ServerURL + 'api/account/reset-password/finish', {}, {
        	'save': {
        		method:'POST', withCredentials: true,
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
