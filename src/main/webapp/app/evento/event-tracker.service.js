(function() {
    'use strict';
    /* globals SockJS, Stomp */

    angular
        .module('subastadosApp')
        .factory('EventTrackerService', EventTrackerService);

    EventTrackerService.$inject = ['$rootScope', '$window', '$cookies', '$http', '$q', '$timeout', 'ServerURL'];

    function EventTrackerService ($rootScope, $window, $cookies, $http, $q, $timeout, ServerURL) {
        var stompClient = null;
        var subscriber = null;
        var listener = $q.defer();
        var connected = $q.defer();
        var subscribed = false;
        
        var service = {
            connect: connect,
            disconnect: disconnect,
            receive: receive,
            subscribe: subscribe,
            unsubscribe: unsubscribe
        };

        return service;

        function connect (successCallback) {
            //building absolute path so that websocket doesn't fail when deploying with a context path
            var loc = $window.location;
            var socket = new SockJS(ServerURL + 'websocket/event-tracker');
            stompClient = Stomp.over(socket);
            var stateChangeStart;
            var headers = {};
            headers[$http.defaults.xsrfHeaderName] = $cookies.get($http.defaults.xsrfCookieName);
            stompClient.connect(headers, function() {
                connected.resolve('success');
                if (angular.isDefined(successCallback)) {
                	successCallback();
                }
            });
            
            socket.onclose = function(e) {
            	$timeout(function() {
            		connect(function() {
            			if(subscribed) subscribe(); // si estaba subscripto que se vuelva a subscribir al reconectar
            		});
            	}, 5000);
            };
        }

        function disconnect () {
            if (stompClient !== null) {
                stompClient.disconnect();
                stompClient = null;
            }
        }

        // Usado en EventController para recibir datos de ofertas
        function receive () {
            return listener.promise;
        }

        function subscribe () {
        	subscribed = true;
            connected.promise.then(function() {
                subscriber = stompClient.subscribe('/topic/event-tracker', function(data) {
                    listener.notify(angular.fromJson(data.body));
                });
            }, null, null);
        }

        function unsubscribe () {
        	subscribed = false;
            if (subscriber !== null) {
                subscriber.unsubscribe();
            }
            listener = $q.defer();
        }
    }
})();
