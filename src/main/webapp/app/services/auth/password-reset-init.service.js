(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .factory('PasswordResetInit', PasswordResetInit);

    PasswordResetInit.$inject = ['$resource','ServerURL'];

    function PasswordResetInit($resource, ServerURL) {
        var service = $resource(ServerURL + 'api/account/reset-password/init', {}, {});

        return service;
    }
})();
