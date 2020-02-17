(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .factory('AuditsService', AuditsService);

    AuditsService.$inject = ['$resource', 'ServerURL'];

    function AuditsService ($resource, ServerURL) {
        var service = $resource(ServerURL + 'management/audits/:id', {}, {
            'get': {
                method: 'GET',
                isArray: true
            },
            'query': {
                method: 'GET',
                isArray: true,
                params: {fromDate: null, toDate: null}
            }
        });

        return service;
    }
})();
