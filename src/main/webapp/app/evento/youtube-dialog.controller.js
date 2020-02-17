(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .controller('YoutubeDialogController', YoutubeDialogController);

    YoutubeDialogController.$inject = ['$stateParams', '$uibModalInstance'];

    function YoutubeDialogController ($stateParams, $uibModalInstance) {
        var vm = this;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
