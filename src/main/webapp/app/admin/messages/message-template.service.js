(function () {
    'use strict';

    angular
        .module('subastadosApp')
        .factory('MessageTemplate', MessageTemplate);

    MessageTemplate.$inject = ['$resource', 'ServerURL'];

    function MessageTemplate ($resource, ServerURL) {
        var service = $resource(ServerURL + 'api/messageTemplate/:id', {}, {
            'findAll': { method: 'GET', isArray: true,
            	params: {
            		top10: false
            	}
            },
            'findTop10': { method: 'GET', isArray: true,
            	params: {
            		top10: true
            	}
            },
            'save': { method:'POST', withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=utf-8',
                    'Access-Control-Allow-Origin': true,
                    'X-Requested-With': 'XMLHttpRequest'
                }
            },
            'update': { method:'PUT', withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=utf-8',
                    'Access-Control-Allow-Origin': true,
                    'X-Requested-With': 'XMLHttpRequest'
                }
            },
            'delete': { method:'DELETE'}
        });

        return service;
    }
})();
