(function () {
    'use strict';

    angular
        .module('subastadosApp')
        .factory('EventoAnterior', EventoAnterior);

    EventoAnterior.$inject = ['$resource', 'ServerURL'];

    function EventoAnterior ($resource, ServerURL) {
        var service = $resource(ServerURL + 'api/event/:id', {}, {
            'query': {method: 'GET', isArray: false}
        });

        return service;
    }
})();
