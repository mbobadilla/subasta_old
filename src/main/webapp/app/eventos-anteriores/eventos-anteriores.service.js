(function () {
    'use strict';

    angular
        .module('subastadosApp')
        .factory('EventosAnteriores', EventosAnteriores);

    EventosAnteriores.$inject = ['$resource', 'ServerURL'];

    function EventosAnteriores ($resource, ServerURL) {
        var service = $resource(ServerURL + 'api/event/history', {}, {
            'query': {method: 'GET', isArray: true}
        });

        return service;
    }
})();
