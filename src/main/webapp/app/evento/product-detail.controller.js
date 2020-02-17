(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .controller('ProductDetailController', ProductDetailController);

    ProductDetailController.$inject = ['$stateParams', '$sce'];

    function ProductDetailController($stateParams, $sce) {
        var vm = this;
        vm.idCaballo = $stateParams.idCaballo;
        vm.position = $stateParams.position;
        var url = $stateParams.url;
        
        console.log("Id de caballo: " + vm.idCaballo);
        
    	var src = url.replace("##ID_CABALLO##", vm.idCaballo);
    	vm.frameUrl = $sce.trustAsResourceUrl(src);
    }
})();
