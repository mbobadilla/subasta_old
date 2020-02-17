(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .factory('OffersService', OffersService);

    OffersService.$inject = ['$resource', 'ServerURL'];

    function OffersService ($resource, ServerURL) {
        var service = $resource(ServerURL + 'api/bid/:loteId', {}, {
            'query': {
                method: 'GET', isArray: true
            }
        });
        return service;
    }

})();
