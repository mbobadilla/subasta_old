(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .factory('Activate', Activate);

    Activate.$inject = ['$resource', 'ServerURL'];

    function Activate ($resource, ServerURL) {
        var service = $resource(ServerURL + 'api/validateMail', {}, {
            'get': { method: 'GET', params: {}, isArray: false}
        });

        return service;
    }
})();
