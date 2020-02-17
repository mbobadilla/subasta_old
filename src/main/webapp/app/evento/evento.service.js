(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .factory('EventoService', EventoService);

    EventoService.$inject = ['$resource', 'ServerURL'];

    function EventoService ($resource, ServerURL) {
        var service = $resource(ServerURL + 'api/event/last', {}, {
            'get': {
                method: 'GET'
            }
        });
        return service;
    }

})();
