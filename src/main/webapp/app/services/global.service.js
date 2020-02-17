(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .factory('GlobalService', GlobalService);

    GlobalService.$inject = [];

    function GlobalService () {
        var me = this;
        
        me.getYoutubeUrl = function(videoId) {
        	if(videoId.startsWith("PL") || videoId.startsWith("pl")) {
        		return "https://www.youtube.com/embed/?list=" + videoId;
        	}
        	return "https://www.youtube.com/embed/" + videoId;
        };
        
        return me;
    }
})();
