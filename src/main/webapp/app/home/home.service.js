(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .factory('HomeService', HomeService);

    HomeService.$inject = ['$resource', 'ServerURL'];

    function HomeService ($resource, ServerURL) {
    	var service = {
    			saveSubscriber: saveSubscriber
    	};

        var Subscriber = $resource(ServerURL + 'api/subscribers/:id', {}, {
            'query': {method: 'GET', isArray: true},
            'save': { method:'POST', withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=utf-8',
                    'Access-Control-Allow-Origin': true,
                    'X-Requested-With': 'XMLHttpRequest'
                } },
            'delete':{ method:'DELETE'}
        });
        
        function saveSubscriber (subscriberPerson, callback) {
            var cb = callback || angular.noop;

            return Subscriber.save(subscriberPerson,
                function () {
                    return cb(subscriberPerson);
                },
                function (err) {
                    return cb(err);
                }.bind(this)).$promise;
        }
        
        return service;
    }
})();
