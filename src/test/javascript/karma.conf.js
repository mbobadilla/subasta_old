// Karma configuration
// http://karma-runner.github.io/0.13/config/configuration-file.html

var sourcePreprocessors = ['coverage'];

function isDebug() {
    return process.argv.indexOf('--debug') >= 0;
}

if (isDebug()) {
    // Disable JS minification if Karma is run with debug option.
    sourcePreprocessors = [];
}

module.exports = function (config) {
    config.set({
        // base path, that will be used to resolve files and exclude
        basePath: 'src/test/javascript/'.replace(/[^/]+/g, '..'),

        // testing framework to use (jasmine/mocha/qunit/...)
        frameworks: ['jasmine'],

        // list of files / patterns to load in the browser
        files: [
            // bower:js
            'src/main/webapp/bower_components/jquery/dist/jquery.min.js',
            'src/main/webapp/bower_components/messageformat/messageformat.js',
            'src/main/webapp/bower_components/humanize-duration/humanize-duration.js',
            'src/main/webapp/bower_components/moment/min/moment.min.js',
            'src/main/webapp/bower_components/json3/lib/json3.min.js',
            'src/main/webapp/bower_components/sockjs-client/dist/sockjs.min.js',
            'src/main/webapp/bower_components/stomp-websocket/lib/stomp.min.js',
            'src/main/webapp/bower_components/angular/angular.min.js',
            'src/main/webapp/bower_components/angular-aria/angular-aria.min.js',
            'src/main/webapp/bower_components/angular-bootstrap/ui-bootstrap-tpls.min.js',
            'src/main/webapp/bower_components/angular-cache-buster/angular-cache-buster.js',
            'src/main/webapp/bower_components/angular-cookies/angular-cookies.min.js',
            'src/main/webapp/bower_components/angular-dynamic-locale/dist/tmhDynamicLocale.min.js',
            'src/main/webapp/bower_components/ngstorage/ngStorage.min.js',
            'src/main/webapp/bower_components/angular-loading-bar/build/loading-bar.min.js',
            'src/main/webapp/bower_components/angular-resource/angular-resource.min.js',
            'src/main/webapp/bower_components/angular-sanitize/angular-sanitize.min.js',
            'src/main/webapp/bower_components/angular-translate/angular-translate.min.js',
            'src/main/webapp/bower_components/angular-translate-interpolation-messageformat/angular-translate-interpolation-messageformat.min.js',
            'src/main/webapp/bower_components/angular-translate-loader-partial/angular-translate-loader-partial.min.js',
            'src/main/webapp/bower_components/angular-translate-storage-cookie/angular-translate-storage-cookie.min.js',
            'src/main/webapp/bower_components/angular-ui-router/release/angular-ui-router.min.js',
            'src/main/webapp/bower_components/angular-animate/angular-animate.min.js',
            'src/main/webapp/bower_components/angular-messages/angular-messages.min.js',
            'src/main/webapp/bower_components/angular-timer/dist/angular-timer.min.js',
            'src/main/webapp/bower_components/bootstrap-ui-datetime-picker/dist/datetime-picker.min.js',
            'src/main/webapp/bower_components/ng-file-upload/ng-file-upload.min.js',
            'src/main/webapp/bower_components/ngInfiniteScroll/build/ng-infinite-scroll.min.js',
            'src/main/webapp/bower_components/angular-ui-carousel/dist/ui-carousel.js',
            'src/main/webapp/bower_components/teljs/dist/scripts/tel.js',
            'src/main/webapp/bower_components/teljs/data/metadatalite.js',
            'src/main/webapp/bower_components/ng-country-select/dist/ng-country-select.min.js',
            'src/main/webapp/bower_components/checklist-model/checklist-model.js',
            'src/main/webapp/bower_components/angular-mocks/angular-mocks.js',
            'src/main/webapp/bower_components/angular-material/angular-material.min.js',
            // endbower
            'src/main/webapp/app/app.module.js',
            'src/main/webapp/app/app.state.js',
            'src/main/webapp/app/app.constants.js',
            'src/main/webapp/app/**/*.+(js|html)',
            'src/test/javascript/spec/helpers/module.js',
            'src/test/javascript/spec/helpers/httpBackend.js',
            'src/test/javascript/**/!(karma.conf).js'
        ],


        // list of files / patterns to exclude
        exclude: [],

        preprocessors: {
            './**/*.js': sourcePreprocessors
        },

        reporters: ['dots', 'junit', 'coverage', 'progress'],

        junitReporter: {
            outputFile: '../target/test-results/karma/TESTS-results.xml'
        },

        coverageReporter: {
            dir: 'target/test-results/coverage',
            reporters: [
                {type: 'lcov', subdir: 'report-lcov'}
            ]
        },

        // web server port
        port: 9876,

        // level of logging
        // possible values: LOG_DISABLE || LOG_ERROR || LOG_WARN || LOG_INFO || LOG_DEBUG
        logLevel: config.LOG_INFO,

        // enable / disable watching file and executing tests whenever any file changes
        autoWatch: false,

        // Start these browsers, currently available:
        // - Chrome
        // - ChromeCanary
        // - Firefox
        // - Opera
        // - Safari (only Mac)
        // - IE (only Windows)
        browsers: ['PhantomJS'],

        // Continuous Integration mode
        // if true, it capture browsers, run tests and exit
        singleRun: false,

        // to avoid DISCONNECTED messages when connecting to slow virtual machines
        browserDisconnectTimeout: 10000, // default 2000
        browserDisconnectTolerance: 1, // default 0
        browserNoActivityTimeout: 4 * 60 * 1000 //default 10000
    });
};
