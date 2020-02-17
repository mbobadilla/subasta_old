(function () {
    'use strict';

    angular
        .module('subastadosApp')
        .factory('Subscriber', Subscriber);

    Subscriber.$inject = ['$resource', 'ServerURL'];

    function Subscriber ($resource, ServerURL) {
        var service = $resource(ServerURL + 'api/subscribers/:login', {}, {
            'query': {method: 'GET', isArray: true}
        });

        return service;
    }
})();
