(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .factory('Sessions', Sessions);

    Sessions.$inject = ['$resource', 'ServerURL'];

    function Sessions ($resource, ServerURL) {
        return $resource(ServerURL + 'api/account/sessions/:series', {}, {
            'getAll': { method: 'GET', isArray: true}
        });
    }
})();
