(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .factory('GeneralConfigurationService', GeneralConfigurationService);

    GeneralConfigurationService.$inject = ['$resource', 'ServerURL'];

    function GeneralConfigurationService ($resource, ServerURL) {
        var service = $resource(ServerURL + 'api/generalConfiguration', {}, {
            'get': {
                method: 'GET', params: {}, isArray: false
            }
        });
        return service;
    }

})();
