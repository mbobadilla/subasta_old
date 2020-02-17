(function () {
    'use strict';

    angular
        .module('subastadosApp')
        .factory('RegisterSecondStep', RegisterSecondStep);

    RegisterSecondStep.$inject = ['$resource', 'ServerURL'];

    function RegisterSecondStep ($resource, ServerURL) {
        return $resource(ServerURL + 'api/register/secondStep', {}, {
            'save': { method:'POST', withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=utf-8',
                    'Access-Control-Allow-Origin': true,
                    'X-Requested-With': 'XMLHttpRequest'
                } }
        });
    }
})();
