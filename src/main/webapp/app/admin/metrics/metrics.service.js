(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .factory('JhiMetricsService', JhiMetricsService);

    JhiMetricsService.$inject = ['$rootScope', '$http', 'ServerURL'];

    function JhiMetricsService ($rootScope, $http, ServerURL) {
        var service = {
            getMetrics: getMetrics,
            threadDump: threadDump
        };

        return service;

        function getMetrics () {
            return $http.get(ServerURL + 'management/metrics').then(function (response) {
                return response.data;
            });
        }

        function threadDump () {
            return $http.get(ServerURL + 'management/dump').then(function (response) {
                return response.data;
            });
        }
    }
})();
