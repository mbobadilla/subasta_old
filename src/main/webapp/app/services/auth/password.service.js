(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .factory('Password', Password);

    Password.$inject = ['$resource', 'ServerURL'];

    function Password($resource, ServerURL) {
        var service = $resource(ServerURL + 'api/account/change-password', {}, {});

        return service;
    }
})();
