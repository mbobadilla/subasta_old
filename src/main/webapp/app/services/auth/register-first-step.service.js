(function () {
    'use strict';

    angular
        .module('subastadosApp')
        .factory('RegisterFirstStep', RegisterFirstStep);

    RegisterFirstStep.$inject = ['$resource', 'ServerURL'];

    function RegisterFirstStep ($resource, ServerURL) {
        return $resource(ServerURL + 'api/register/firstStep', {}, {
            'save': { method:'POST', withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=utf-8',
                    'Access-Control-Allow-Origin': true,
                    'X-Requested-With': 'XMLHttpRequest'
                } }
        });
    }
})();
