var app = angular.module('productApp', [ 'ngRoute', 'chieffancypants.loadingBar',  'ui.grid', 'ui.grid.pagination', 'ui.grid.selection', 'ui.grid.exporter', 'ui.grid.resizeColumns', 'ui.grid.moveColumns']);
//var app = angular.module('productApp', ['ngRoute', 'chieffancypants.loadingBar','ui.grid']);

app.config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
    //cfpLoadingBarProvider.includeSpinner = true;
    //$locationProvider.hashPrefix('');
    $routeProvider
        .when('/product', {
            templateUrl: 'views/productView.html',
            controller: 'productController'
        })
        .when('/stats', {
            templateUrl: 'views/statsView.html',
            controller: 'statsController'
        })
        .when('/demographics', {
            templateUrl: 'views/demographicsView.html'
        })
        .when('/footnotes', {
            templateUrl: 'views/footnotesView.html'
        })
        .when('/map', {
            templateUrl: 'views/mapView.html',
            controller: 'mapController'
        })
    .otherwise({
        redirectTo: '/product'
    });

}]);

app.directive('adjustHeight', ['$timeout', '$window', function ($timeout, $window) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var setGridHeight = function () {
                $('#changes-height-stats').height($(element).height() / 4);
            };
            //setGridHeight();          // does not resize the grid
            $timeout(setGridHeight);  // resizes the grid but not the render-container
            angular.element($window).bind('resize', setGridHeight);
        }
    };
}]);

app.config(function (cfpLoadingBarProvider) {
    cfpLoadingBarProvider.includeSpinner = true;
});

app.controller('statsController', function ($scope, $http,$location,$route, uiGridExporterService, uiGridExporterConstants,$timeout, cfpLoadingBar, uiGridConstants, $window)
{
    var productname;
    var apiUrl = 'http://152.144.227.176:8080/ProductWS/jaxrs/WebService/';
    //var apiUrl = 'http://localhost:8080/ProductWS/jaxrs/WebService/';
    $scope.mapNotes = new Map();

    $scope.gridStatsOptions = {
        enableFiltering: true,
        enableRowHeaderSelection: false,

        enableColumnResizing: true,
        enableGridMenu: true,
        exporterMenuPdf: false,//for removing pdf export option
        enableSelectAll: true,
        exporterCsvFilename: 'PitneyBowes@AllRightsReserved.csv',
        exporterPdfDefaultStyle: { fontSize: 9 },
        exporterPdfTableStyle: { margin: [30, 30, 30, 30] },
        exporterPdfTableHeaderStyle: { fontSize: 10, bold: true, italics: true, color: 'blue' },
        exporterPdfHeader: { text: "Pitney Bowes", style: 'headerStyle' },
        exporterPdfFooter: { text: "PitneyBowes@AllRightsReserved", style: 'footerStyle' },
        exporterPdfCustomFormatter: function (docDefinition) {
            docDefinition.styles.headerStyle = { fontSize: 7, bold: true };
            docDefinition.styles.footerStyle = { fontSize: 7, bold: true };
            return docDefinition;
        },
        exporterPdfOrientation: 'landscape',
        exporterPdfPageSize: 'LETTER',
        exporterPdfMaxGridWidth: 175,
        exporterCsvLinkElement: angular.element(document.querySelectorAll(".custom-csv-link-location")),
        onRegisterApi: function (gridApi) {
            $scope.gridApi = gridApi;
        }

    };
    $scope.filteringText = '';
    $scope.filterOptions = {
        filterText:'',
        useExternalFilter: true
    };



    //var _content;

    $scope.loadNotes = function (_content) {
        toastr.info(_content, {
            closeButton: true,
            position: 'toast-bottom-right',
            tapToDismiss: false,
            timeOut: 0
        });
    };



    $scope.readNotesCSV = function () {
        // http get request to read CSV file content
        $http.get('Notes.csv').success($scope.processData);
    };

    //Loading Notes.csv to be consumed later
    $scope.readNotesCSV();

    $scope.processData = function (allText) {
        // split content based on new line
        var allTextLines = allText.split(/\r\n|\n/);
        var headers = allTextLines[0].split(',');
        var lines = [];

        for (var i = 0; i < allTextLines.length; i++) {
            // split content based on comma
            var data = allTextLines[i].split(',');
            //if (data.length == headers.length) {
            var tarr = [];
            var _data,_table;
            for (var j = 0; j < data.length; j++) {
                if (j < 1) {
                    _table = data[j];
                    lines.push(_table);
                    _data = data[j+1]
                }
                else
                    if(j <2)
                        _data = data[j];
                    else
                        _data = _data + data[j];
            }
                  
            lines.push(_data);
            $scope.mapNotes.set(_table, _data);
            //mapNotes[_table] = _data;
            //}
        }
        $scope.data = lines;
        console.log($scope.mapNotes);
    };




    $scope.socialplacestd = true;
    $scope.countrytd = true;
    $scope.enableFiltering= true;

    $scope.productData = [];
    $scope.productStats = [];
    $scope.poistatsdata = [];
    $scope.poicountrystatsdata = [];
    $scope.countries = [];
    $scope.socialplaces = [];
    $scope.productlines = [];

    $scope.selectedproduct = null;
    $scope.statselected = null;
    $scope.countryselected = null;
    $scope.socialplacesselected = null;

    $scope.productMap = {};
        

    //*************************************************************************************************************************
    ////For Loading up ProductLine Data to Dropdown
    //##########################################################################################################################       
    $scope.loadProductline = function () {
        var config = {
            headers: {
                'Content-Type': 'application/json',
            }
        }
        $scope.productlines = [];
        $http.get('' + apiUrl + 'GetProductline', config)
        .success(function (data, status, headers, config) {
            for (var i = 0; i < data.length; i++) {
                $scope.productlines.push(data[i].product_line);
            }
            //console.log($scope.countries);
        })
        .error(function (data, status, header, config) {
            alert('Unable to get Productlines !!!');
        });
    };

    //**************************************************************************************************************************
    $scope.loadProductline();
    //**************************************************************************************************************************


    //*************************************************************************************************************************
    ////For Loading up Producs Data to Dropdown
    //##########################################################################################################################       
    $scope.loadProducts = function (_product) {
        var config = {
            headers: {
                'Content-Type': 'application/json',
            }
        }
        $scope.products = [];
        $http.get('' + apiUrl + 'GetProducts/' + _product, config)
        .success(function (data, status, headers, config) {
            for (var i = 0; i < data.length; i++) {
                $scope.products.push(data[i].products);
            }
            //console.log($scope.countries);
        })
        .error(function (data, status, header, config) {
            alert('Unable to get Products !!!');
        });
    };


    //*************************************************************************************************************************
    ////For Loading up First Filter to Dropdown
    //##########################################################################################################################  

    $scope.loadFilters = function(selectedproduct)
    {
        productname = selectedproduct;
        if (selectedproduct == 'Social Places') {
            $scope.loadSecondFilterData(selectedproduct);
        }
            //kunaledit
        else if (selectedproduct == 'PropertyAttributefabric') {
            $scope.loadSecondFilterData(selectedproduct);
        }
        else if(selectedproduct == 'PropertyAttributeParcel') {
            $scope.loadSecondFilterData(selectedproduct);
        }
        else if(selectedproduct == 'PropertyAttributeGEM') {
            $scope.loadSecondFilterData(selectedproduct);
        }
        else {
            $scope.socialplacestd = true;
            $scope.loadFirstFilter(selectedproduct);
        }
    };


    //Loading First Filter data in the drop down
    $scope.loadFirstFilter = function (_product) {
        var config = {
            headers: {
                'Content-Type': 'application/json',
            }
        }
        $scope.productStats = [];
        $http.get('' + apiUrl + 'GetFirstFilter/' + _product, config)
        .success(function (data, status, headers, config) {
            for (var i = 0; i < data.length; i++) {
                $scope.productStats.push(data[i].filter2);
            }
            console.log($scope.productStats);
        })
        .error(function (data, status, header, config) {
            alert('Unable to get First Filter Data !!!');
        });
    };


    //Loading Social Places data in the drop down
    $scope.loadSecondFilterData = function (_product) {

        $scope.socialplacestd = false;
        var config = {
            headers: {
                'Content-Type': 'application/json',
            }
        }
        $scope.socialplaces = [];
        $http.get('' + apiUrl + 'GetSecondFilterData/' + _product, config)
        .success(function (data, status, headers, config) {
            if(_product == "PropertyAttributefabric"){
                $scope.socialplaces.push("USA");
                for (var i = 1; i < data.length; i++) {
                    if(data[i].filter1 != "USA"){
                        $scope.socialplaces.push(data[i].filter1);
                    }
                }
            }
            else if(_product == "PropertyAttributeGEM"){
                $scope.socialplaces.push("USA");
                for (var i = 0; i < data.length; i++) {
                    if(data[i].filter1 != "USA"){
                        $scope.socialplaces.push(data[i].filter1);
                    }
                }
			
            }
            else if(_product == "PropertyAttributeParcel"){
                $scope.socialplaces.push("USA");
                for (var i = 0; i < data.length; i++) {
                    if(data[i].filter1 != "USA"){
                        $scope.socialplaces.push(data[i].filter1);
                    }
                }
            }
            else{
                for (var i = 0; i < data.length; i++) {
                    if(data[i].filter1 != "USA"){
                        $scope.socialplaces.push(data[i].filter1);
                    }
                }
            }
			

            //console.log($scope.countries);
        })
        .error(function (data, status, header, config) {
            alert('Unable to load Social Places !!!');
        });
    };


    //Loading Social Places data in the drop down
    $scope.loadTables = function (_subproduct) {
        // _product = 'Social Places';
        var config = {
            headers: {
                'Content-Type': 'application/json',
            }
        }
        $scope.productStats = [];
        /*
        $http.get('' + apiUrl + 'GetSecondFilter/' + _product + '/' + _subproduct, config)
        .success(function (data, status, headers, config) {
            for (var i = 0; i < data.length; i++) {
                $scope.productStats.push(data[i].filter2);
            }
            //console.log($scope.countries);
        })
        .error(function (data, status, header, config) {
            alert('Unable to load Social Places Stats Tables !!!');
        });*/
        _product = productname;
        $scope.productStats = [];
        $http.get('' + apiUrl + 'GetSecondFilter/' + _product + '/' + _subproduct, config)
        .success(function (data, status, headers, config) {
            for (var i = 0; i < data.length; i++) {
                $scope.productStats.push(data[i].filter2);
            }
            //console.log($scope.countries);
        })
        .error(function (data, status, header, config) {
            alert('Unable to load Social Places Stats Tables !!!');
        });
    };

    //**************************************************************************************************************************
    //getting product stats data 
    //##########################################################################################################################
    $scope.loadStats = function (_tblname) {
        if ((_tblname == "ppoi_counts_by_category_country") || (_tblname == "ppoic_counts_by_category_country")) {
            //if (_tblname == "ppoi_counts_by_category_country") {
            $scope.countrytd = false;
            $scope.loadCountries();
        }
        else {
            $scope.countrytd = true;
            var config = {
                headers: {
                    'Content-Type': 'application/json',
                }
            }
            $scope.poistatsdata = [];
            $http.get('' + apiUrl + 'GetPoiStats/' + _tblname, config)
            .success(function (data, status, headers, config) {
                $scope.poistatsdata = data;

                console.log(data);
                $scope.gridStatsOptions.data = $scope.poistatsdata;
                $scope.gridStatsOptions.height = ($scope.poistatsdata.length * 2) + "px";
                $scope.loadStatsData(_tblname, $scope);
            })
            .error(function (data, status, header, config) {
                alert('Unable to fetch poi stats data !!!');
            });
        }

    };
    //***********************************************************************************************************************

    //$scope.loadStats('micode');

    //$scope.gridStatsOptions.columnDefs = [
    //{ displayName: 'TradeDivision', field: 'tradedivision', width: 400 },
    //{ displayName: 'TradeGroup', field: 'tradegroup', width: 400 },
    //{ displayName: 'Class', field: 'class', width: 400 },
    //{ displayName: 'SubClass', field: 'subclass', width: 400 },
    //{ displayName: 'Sic', field: 'sic', width: 150 },
    //{ displayName: 'Sic8', field: 'sic8', width: 150 },
    //{ displayName: 'MicodeSic8', field: 'micodesic8', width: 150 },
    //{ displayName: 'Sic8_Description', field: 'sic8description', width: 400 },
    //{ displayName: 'Search_Description_Engine', field: 'search_description_engine', width: 400 },
    //{ displayName: 'Grouping', field: 'grouping', width: 120 },
    //{ displayName: 'Source', field: 'source', width: 120 },
    //{ displayName: 'AnzCode', field: 'anzcode', width: 120 },
    //{ displayName: 'AnzDescription', field: 'anzdescription', width: 400 },
    //{ displayName: 'ConsumerPoi', field: 'consumerpoi', width: 120 },
    //{ displayName: 'NonBussiness', field: 'nonbussiness', width: 120 },
    //{ displayName: 'OwneriqMicodes', field: 'owneriqmicodes', width: 120 }
    //];

    //*************************************************************************************************************************
    ////For Loading up countrieswise stats data
    //##########################################################################################################################       
    $scope.loadCountries = function () {
        var config = {
            headers: {
                'Content-Type': 'application/json',
            }
        }
        $scope.countries = [];
        $http.get('' + apiUrl + 'GetCountries', config)
        .success(function (data, status, headers, config) {

            for (var i = 0; i < data.length; i++)
            {
                $scope.countries.push(data[i].Country);
            }
               

            console.log($scope.countries);

        })
        .error(function (data, status, header, config) {
            alert('Unable to fetch country data !!!');
        });
    };

    //**************************************************************************************************************************
    //getting poi stats for country for POI
    //##########################################################################################################################

    $scope.loadCountryStats = function (_country) {           

        var config = {
            headers: {
                'Content-Type': 'application/json',
            }
        }
        $scope.poicountrystatsdata = [];
        $http.get('' + apiUrl + 'GetCountryPoiStats/' + $scope.statselected + '/' + _country, config)
        .success(function (data, status, headers, config) {
            $scope.poistatsdata = data;

            //console.log(data);
            $scope.gridStatsOptions.data = $scope.poistatsdata;
            $scope.gridStatsOptions.height = ($scope.poistatsdata.length * 2) + "px";

            $scope.loadStatsData($scope.statselected, $scope);


            //$scope.gridStatsOptions.columnDefs = [
            //    { displayName: 'Country_Bundles', field: 'countrybundles', width: 300 },
            //    { displayName: 'Countries', field: 'countries', width: 150 },
            //    { displayName: 'ISO3', field: 'iso3', width: 150 },
            //    { displayName: 'Trade_Division', field: 'tradedivision', width: 500 },
            //    { displayName: 'Group', field: 'group', width: 500 },
            //    { displayName: 'Class', field: 'class', width: 500 },
            //    { displayName: 'SubClass', field: 'subclass', width: 500 },
            //    { displayName: 'SIC8', field: 'sic8', width: 150 },
            //    { displayName: 'Micode', field: 'micode', width: 150 },
            //    { displayName: 'Description', field: 'description', width: 500 },
            //    { displayName: 'PoiCounts', field: 'poicounts', width: 160 },
            //    { displayName: 'Version', field: 'version', width: 120 },
            //    { displayName: 'Delta', field: 'delta', width: 120 }
            //];
        })
        .error(function (data, status, header, config) {
            alert('Unable to fetch country poi stats data !!!');
        });

    };

    //**************************************************************************************************************************
    $scope.toTitleCase = function (string) {
        // \u00C0-\u00ff for a happy Latin-1
        return string.toLowerCase().replace(/_/g, ' ').replace(/\b([a-z\u00C0-\u00ff])/g, function (_, initial) {
            return initial.toUpperCase();
        }).replace(/(\s(?:de|a|o|e|da|do|em|ou|[\u00C0-\u00ff]))\b/ig, function (_, match) {
            return match.toLowerCase();
        });
    }
    //*************************************************************************************************************************
    //Retun back to Product View
    $scope.return = function () {
        $location.path('/product');
    };
    
    $scope.getdemographics = function () {
        $location.path('/demographics');
    };

    $scope.getfootnotes = function () {
        $location.path('/footnotes');
    };
    //**************************************************************************************************************************
    //loading the data to the datagrid for stats page
    //##########################################################################################################################
    $scope.loadStatsData = function (item, $scope) {
        var arrData = [];
        var _content = '',propercasestr = '';

        var arrData = Object.getOwnPropertyNames($scope.poistatsdata[0]);
        var griddata = '';

        $scope.gridStatsOptions.columnDefs = new Array();
		
        for (var i = 0; i < arrData.length; i++) {
            //console.log($scope.toTitleCase(arrData[i]));
            propercasestr = $scope.toTitleCase(arrData[i]);
            propercasestr = propercasestr.replace("'", '');
            propercasestr = propercasestr.replace("'", '');
            $scope.gridStatsOptions.columnDefs.push({
                field: arrData[i],
				
                displayName: propercasestr,
                width: 300,
                /*filter: 'text',
				filter: {
				 //kunal edit
					condition: uiGridConstants.filter.CONTAINS,
				}*/
            });
        }
        
        if (item == "base_demographics_description") {
            $window.open('BaseDemographics.html');
        }
        else if (item == "base_demographics_footnotes") {
            $window.open('Footnotes.html');
        }

    };


});

app.controller('productController', function ($scope, $http,$route, $timeout, $timeout, cfpLoadingBar, uiGridConstants,$window,$location)
{
    var apiUrl = 'http://152.144.227.176:8080/ProductWS/jaxrs/WebService/';
    //var apiUrl = 'http://localhost:8080/ProductWS/jaxrs/WebService/';

    $scope.productData = [];
    $scope.productStats = [];
    $scope.poistatsdata = [];

    $scope.productlines = ["GEM", "PAD", "World Streets", "World Points Of Interest", "World Boundaries", "World Boundaries Telco", "World Boundaries Risk", "World Demographics", "Geocoding", "Spectrum", "All"]
    $scope.products = ["Routing J Server", "ERM", "Route Finder", "StreetPro", "StreetPro Nav", "World StreetPro", "WPPOI", "WBO", "Anow", "EGM", "Address Doctor", "Loqate"]
        
    $scope.selectedproduct = null;
    $scope.statselected = null;

    $scope.productMap = {};

    $scope.gridOptions = {
        enableSorting: true,
        enableFiltering: true,
        enableGridMenu: true,
        enableSelectAll: true,
        exporterMenuPdf: false,
        //exporterCsvFilename: 'myFile.csv',
        //exporterPdfDefaultStyle: { fontSize: 9 },
        //exporterPdfTableStyle: { margin: [30, 30, 30, 30] },
        //exporterPdfTableHeaderStyle: { fontSize: 10, bold: true, italics: true, color: 'red' },
        //exporterPdfHeader: { text: "My Header", style: 'headerStyle' },
        //exporterPdfFooter: function (currentPage, pageCount) {
        //    return { text: currentPage.toString() + ' of ' + pageCount.toString(), style: 'footerStyle' };
        //},
        //exporterPdfCustomFormatter: function (docDefinition) {
        //    docDefinition.styles.headerStyle = { fontSize: 22, bold: true };
        //    docDefinition.styles.footerStyle = { fontSize: 10, bold: true };
        //    return docDefinition;
        //},
        //exporterPdfOrientation: 'portrait',
        //exporterPdfPageSize: 'LETTER',
        //exporterPdfMaxGridWidth: 500,
        exporterCsvLinkElement: angular.element(document.querySelectorAll(".custom-csv-link-location")),
        onRegisterApi: function (gridApi) {
            $scope.gridApi = gridApi;
        }
    };



    $scope.gridOptions.columnDefs = [
        { displayName: 'Region', field: 'Region', width: 150 },
        { displayName: 'Country', field: 'Country', width: 300 },
        { displayName: 'WPPOI_Cycle', field: 'WPPOI_Cycle', width: 150 },
        { displayName: 'WPPOI_Data_Source', field: 'WPPOI_Data_Source', width: 250 },
        { displayName: 'WPPOI_Data_Available', field: 'WPPOI_Data_Available', width: 200 },
        { displayName: 'WPPOI_Version', field: 'WPPOI_Version', width: 150 },
        { displayName: 'WPPOI_Released_Month', field: 'WPPOI_Released_Month', width: 200 },
        { displayName: 'WPOI_Cycle', field: 'WPOI_Cycle', width: 250 },
        { displayName: 'WPOI_Data_Source', field: 'WPOI_Data_Source', width: 200 },
        { displayName: 'WPOI_Data_Available', field: 'WPOI_Data_Available', width: 200 },
        { displayName: 'WPOI_Vintage', field: 'WPOI_Vintage', width: 200 },
        { displayName: 'WPOI_Released_Month', field: 'WPOI_Released_Month', width: 200 },
        { displayName: 'WBO_Cycle', field: 'WBO_Cycle', width: 250 },
        { displayName: 'WBO_Data_Source', field: 'WBO_Data_Source', width: 200 },
        { displayName: 'WBO_Data_Available', field: 'WBO_Data_Available', width: 200 },
        { displayName: 'WBO_Vintage', field: 'WBO_Vintage', width: 200 },
        { displayName: 'WBO_Released_Month', field: 'WBO_Released_Month', width: 200 },
        { displayName: 'Other_Boundaries_Cycle', field: 'Admin_Cycle', width: 300 },
        { displayName: 'Other_Boundaries_Source', field: 'Admin_Source', width: 200 },
        { displayName: 'Other_Boundaries_Available', field: 'Admin_Available', width: 300 },
        { displayName: 'Other_Boundaries_Vintage', field: 'Admin_Vintage', width: 200 },
        { displayName: 'Other_Boundaries_Data_Released', field: 'Admin_Data_Released', width: 200 },
        { displayName: 'StreetPro_Cycle', field: 'StreetPro_Cycle', width: 200 },
        { displayName: 'StreetPro_Data_Source', field: 'StreetPro_Data_Source', width: 200 },
        { displayName: 'StreetPro_Available', field: 'StreetPro_Available', width: 200 },
        { displayName: 'StreetPro_Vintage', field: 'StreetPro_Vintage', width: 200 },
        { displayName: 'StreetPro_Released', field: 'StreetPro_Released', width: 200 },
        { displayName: 'StreetProNav_Cycle', field: 'StreetProNav_Cycle', width: 150 },
        { displayName: 'StreetProNav_Data_Source', field: 'StreetProNav_Data_Source', width: 200 },
        { displayName: 'StreetProNav_Available', field: 'StreetProNav_Available', width: 200 },
        { displayName: 'StreetProNav_Vintage', field: 'StreetProNav_Vintage', width: 200 },
        { displayName: 'StreetProNav_Released', field: 'StreetProNav_Released', width: 200 },
        { displayName: 'StreetProWrld_Cycle', field: 'StreetProWrld_Cycle', width: 150 },
        { displayName: 'StreetProWrld_Data_Source', field: 'StreetProWrld_Data_Source', width: 200 },
        { displayName: 'StreetProWrld_Available', field: 'StreetProWrld_Available', width: 200 },
        { displayName: 'StreetProWrld_Vintage', field: 'StreetProWrld_Vintage', width: 200 },
        { displayName: 'StreetProWrld_Released', field: 'StreetProWrld_Released', width: 200 },
        { displayName: 'RJS_Cycle', field: 'RJS_Cycle', width: 200 },
        { displayName: 'RJS_Source', field: 'RJS_Source',width: 200 },
        { displayName: 'RJS_Available', field: 'RJS_Available', width: 200 },
        { displayName: 'RJS_Vintage', field: 'RJS_Vintage', width: 200 },
        { displayName: 'RJS_Released', field: 'RJS_Released', width: 200 },
        { displayName: 'ERM_Cycle', field: 'ERM_Cycle', width: 200 },
        { displayName: 'ERM_Source', field: 'ERM_Source',width: 200 },
        { displayName: 'ERM_Available', field: 'ERM_Available', width: 200 },
        { displayName: 'ERM_Vintage', field: 'ERM_Vintage', width: 200 },
        { displayName: 'ERM_Released', field: 'ERM_Released', width: 200 },
        { displayName: 'RF_Cycle', field: 'RF_Cycle', width: 150 },
        { displayName: 'RF_Source', field: 'RF_Source',width: 200 },
        { displayName: 'RF_Available', field: 'RF_Available', width: 200 },
        { displayName: 'RF_Version', field: 'RF_Version', width: 150 },
        { displayName: 'RF_Vintage', field: 'RF_Vintage', width: 200 },
        { displayName: 'RF_Released', field: 'RF_Released', width: 200 },
        { displayName: 'EGM_Cycle', field: 'EGM_Cycle', width: 200 },
        { displayName: 'EGM_Source', field: 'EGM_Source', width: 200 },
        { displayName: 'EGM_Available', field: 'EGM_Available', width: 200 },
        { displayName: 'EGM_Data_Vintage', field: 'EGM_Vintage', width: 200 },
        { displayName: 'EGM_Released', field: 'EGM_Released', width: 200 },
        { displayName: 'EGM_Postal', field: 'EGM_Postal', width: 200 },
        { displayName: 'EGM_City', field: 'EGM_City',width: 200 },
        { displayName: 'EGM_Locality', field: 'EGM_Locality', width: 200 },
        { displayName: 'EGM_Street_Centroid', field: 'EGM_Street_Centroid',width: 200 },
        { displayName: 'EGM_Street_Interpolation', field: 'EGM_Street_Interpolation',width: 200 },
        { displayName: 'EGM_Address_Point', field: 'EGM_Address_Point',width: 200 },
        { displayName: 'EGM_Maximum_Precision', field: 'EGM_Maximum_Precision', width: 300 },
        { displayName: 'AD_Cycle', field: 'AD_Cycle', width: 200 },
        { displayName: 'AD_Source', field: 'AD_Source',width: 200 },
        { displayName: 'AD_Available', field: 'AD_Available', width: 200 },
        { displayName: 'AD_Data_Vintage', field: 'AD_Vintage', width: 200 },
        { displayName: 'AD_Released', field: 'AD_Released', width: 200 },
        { displayName: 'AD_Data_Updated', field: 'AD_Updated',width: 200 },
        { displayName: 'Loqate_Cycle', field: 'Loqate_Cycle', width: 200 },
        { displayName: 'Loqate_Source', field: 'Loqate_Source',width: 200 },
        { displayName: 'Loqate_Available', field: 'Loqate_Available', width: 200 },
        { displayName: 'Loqate_Released_Month', field: 'Loqate_Released_Month', width: 200 },
        { displayName: 'Loqate_Verification_Level', field: 'Loqate_Verification_Level', width: 200 },
        { displayName: 'Loqate_Geocoding_Level', field: 'Loqate_Geocoding_Level', width: 250 },
        { displayName: 'Loqate_Power_Search', field: 'Loqate_Power_Search',width: 200 },
        { displayName: 'Anow_Cycle', field: 'ADN_Cycle', width: 200 },
        { displayName: 'Anow_Source', field: 'ADN_Source', width: 200 },
        { displayName: 'Anow_Available', field: 'ADN_Available', width: 200 },
        { displayName: 'Anow_Version', field: 'ADN_Version', width: 150 },
        { displayName: 'Anow_Released_Month', field: 'ADN_Released_Month', width: 200 },
        { displayName: 'Anow_Knowledgebase_Updated', field: 'ADN_Knowledgebase_Updated', width: 200 },
        { displayName: 'Anow_Validation_Level', field: 'ADN_Validation_Level', width: 200 },
        { displayName: 'Anow_RefData_Updated', field: 'ADN_RefData_Updated', width: 200 },
        { displayName: 'Anow_Geocode_Updated', field: 'ADN_Geocode_Updated', width: 200 },
        { displayName: 'ICP_Cycle', field: 'ICP_Cycle', width: 200 },
        { displayName: 'ICP_Source', field: 'ICP_Source',width: 200 },
        { displayName: 'ICP_Available', field: 'ICP_Available', width: 200 },
        { displayName: 'ICP_Released', field: 'ICP_Released', width: 200 },
        { displayName: 'ICP_Updated', field: 'ICP_Updated',width: 200 },
        { displayName: 'ICP_Level', field: 'ICP_Level', width: 200 },
        { displayName: 'Cameo(CallCredit)_Data', field: 'Cameo_Data', width: 200 },
        { displayName: 'Cameo(CallCredit)_Level', field: 'Cameo_Level', width: 400 },
        { displayName: 'DetailedDemo_Data', field: 'DetDem_Data', width: 200 },
        { displayName: 'DetailedDemo_Level', field: 'DetDem_Level', width: 400 },
        { displayName: 'BaseDem_Data', field: 'BaseDem_Data', width: 200 },
        { displayName: 'BaseDem_Level', field: 'BaseDem_Level', width: 400 }


    ];


    $scope.getProductData = function () {
        var config = {
            headers: {
                'Content-Type': 'application/json',
            }
        }
        $scope.productData = [];
        $http.get('' + apiUrl + 'GetProductMaster', config)
        .success(function (data, status, headers, config) {
            $scope.productData = data;
            console.log(data);
            $scope.gridOptions.data = $scope.productData;
            $scope.gridOptions.height = ($scope.productData.length * 50 + 30) + "px";
        })
        .error(function (data, status, header, config) {
            alert('Unable to fetch product data !!!');
        });

    };

    $scope.getProductData();

    //$scope.getTableHeight = function () {
    //    var rowHeight = 30; // your row height
    //    var headerHeight = 30; // your header height
    //    return {
    //        height: ($scope.productData.length * rowHeight + headerHeight) + "px"
    //    };
    //};

    //$scope.getTableHeight();

    $scope.getProducts = function (item) {
        console.log(item);
        if (item == "World Streets") {
            $scope.products = [];
            $scope.products.push("Routing J Server");
            $scope.products.push("Route Finder");
            $scope.products.push("StreetPro");
            $scope.products.push("StreetPro Nav");
            $scope.products.push("World StreetPro");

            $scope.productStats = [];
        }
        else if (item == "GEM") {
            $scope.products = [];
            $scope.products.push("Communication Suite");
            $scope.products.push("Demographic Bundles");
            $scope.products.push("Risk Data Suite");

            $scope.productStats = [];
        }
        else if (item == "PAD") {
            $scope.products = [];
            $scope.products.push("Property Attribute - Elevation");

            $scope.productStats = [];
        }
        else if (item == "World Points Of Interest") {
            $scope.products = [];
            //$scope.products.push("WPOI");
            $scope.products.push("WPPOI");

            $scope.productStats = [];
            $scope._tblname = "";
            $scope.productStats.push("geocoding_confidence_score");
            $scope.productStats.push("poi_count_by_category_country");
            $scope.productStats.push("poi_counts_by_country");
            $scope.productStats.push("poi_counts_by_geocoding_country");
            $scope.productStats.push("populated_column_counts");
        }
        else if (item == "World Boundaries") {
            $scope.products = [];
            $scope.products.push("WBO");
            $scope.products.push("Suburbs & Localities");

            $scope.productStats = [];
        }
        else if (item == "World Boundaries Telco") {
            $scope.products = [];
            $scope.products.push("AHJ Pro");
            $scope.products.push("LATAInfo");
            $scope.products.push("AreaCodeInfo");
            $scope.productStats = [];
        }
        else if (item == "World Boundaries Risk") {
            $scope.products = [];
            $scope.products.push("Fire Station Bundle");
            $scope.products.push("FloodRiskPro");
            $scope.products.push("FireRiskPro");
            $scope.productStats = [];
        }
        else if (item == "World Demographics") {
            $scope.products = [];
            $scope.products.push("Cameo(CallCredit)");
            $scope.products.push("Detailed Demographics");
            $scope.products.push("Base Demographics");

            $scope.productStats = [];
        }
        else if (item == "Geocoding") {
            $scope.products = [];
            $scope.products.push("Spectrum EGM");

            $scope.productStats = [];
        }
        else if (item == "Spectrum") {
            $scope.products = [];
            $scope.products.push("Address Doctor");
            $scope.products.push("Loqate");
            $scope.products.push("ICP");
            $scope.products.push("Anow");
            $scope.products.push("ERM");

            $scope.productStats = [];
        }
        else if (item == "All") {
            $scope.products = [];
            $scope.products.push("Routing J Server");
            $scope.products.push("ERM");
            $scope.products.push("Route Finder");
            $scope.products.push("StreetPro");
            $scope.products.push("StreetPro Nav");
            $scope.products.push("StreetPro World");    
            $scope.products.push("WPOI");
            $scope.products.push("WPPOI");
            $scope.products.push("WBO");
            $scope.products.push("Suburbs & Localities");
            $scope.products.push("Cameo(CallCredit)");
            $scope.products.push("Detailed Demographics");
            $scope.products.push("Base Demographics");
            $scope.products.push("MBI");
            $scope.products.push("Anow");
            $scope.products.push("Spectrum EGM");
            $scope.products.push("Address Doctor");
            $scope.products.push("Loqate");
            $scope.products.push("ICP");
            $scope.products.push("All");

            //$('#cmbProducts').selected = "All";
            //$("#cmbProducts option:selected").attr("All").
            //document.getElementById("cmbProducts").selected = "true";

            $scope.gridOptions.columnDefs = [
                { displayName: 'Region', field: 'Region', width: 150 },
                { displayName: 'Country', field: 'Country', width: 300 },
                { displayName: 'WPPOI_Cycle', field: 'WPPOI_Cycle', width: 150 },
                { displayName: 'WPPOI_Data_Source', field: 'WPPOI_Data_Source', width: 250 },
                { displayName: 'WPPOI_Data_Available', field: 'WPPOI_Data_Available', width: 200 },
                { displayName: 'WPPOI_Version', field: 'WPPOI_Version', width: 150 },
                { displayName: 'WPPOI_Released_Month', field: 'WPPOI_Released_Month', width: 200 },
                { displayName: 'WPOI_Cycle', field: 'WPOI_Cycle', width: 250 },
                { displayName: 'WPOI_Data_Source', field: 'WPOI_Data_Source', width: 200 },
                { displayName: 'WPOI_Data_Available', field: 'WPOI_Data_Available', width: 200 },
                { displayName: 'WPOI_Vintage', field: 'WPOI_Vintage', width: 200 },
                { displayName: 'WPOI_Released_Month', field: 'WPOI_Released_Month', width: 200 },
                { displayName: 'WBO_Cycle', field: 'WBO_Cycle', width: 250 },
                { displayName: 'WBO_Data_Source', field: 'WBO_Data_Source', width: 200 },
                { displayName: 'WBO_Data_Available', field: 'WBO_Data_Available', width: 200 },
                { displayName: 'WBO_Vintage', field: 'WBO_Vintage', width: 200 },
                { displayName: 'WBO_Released_Month', field: 'WBO_Released_Month', width: 200 },
                { displayName: 'Other_Boundaries_Cycle', field: 'Admin_Cycle', width: 300 },
                { displayName: 'Other_Boundaries_Source', field: 'Admin_Source', width: 200 },
                { displayName: 'Other_Boundaries_Available', field: 'Admin_Available', width: 300 },
                { displayName: 'Other_Boundaries_Vintage', field: 'Admin_Vintage', width: 200 },
                { displayName: 'Other_Boundaries_Data_Released', field: 'Admin_Data_Released', width: 200 },
                { displayName: 'StreetPro_Cycle', field: 'StreetPro_Cycle', width: 200 },
                { displayName: 'StreetPro_Data_Source', field: 'StreetPro_Data_Source', width: 200 },
                { displayName: 'StreetPro_Available', field: 'StreetPro_Available', width: 200 },
                { displayName: 'StreetPro_Vintage', field: 'StreetPro_Vintage', width: 200 },
                { displayName: 'StreetPro_Released', field: 'StreetPro_Released', width: 200 },
                { displayName: 'StreetProNav_Cycle', field: 'StreetProNav_Cycle', width: 150 },
                { displayName: 'StreetProNav_Data_Source', field: 'StreetProNav_Data_Source', width: 200 },
                { displayName: 'StreetProNav_Available', field: 'StreetProNav_Available', width: 200 },
                { displayName: 'StreetProNav_Vintage', field: 'StreetProNav_Vintage', width: 200 },
                { displayName: 'StreetProNav_Released', field: 'StreetProNav_Released', width: 200 },
                { displayName: 'StreetProWrld_Cycle', field: 'StreetProWrld_Cycle', width: 150 },
                { displayName: 'StreetProWrld_Data_Source', field: 'StreetProWrld_Data_Source', width: 200 },
                { displayName: 'StreetProWrld_Available', field: 'StreetProWrld_Available', width: 200 },
                { displayName: 'StreetProWrld_Vintage', field: 'StreetProWrld_Vintage', width: 200 },
                { displayName: 'StreetProWrld_Released', field: 'StreetProWrld_Released', width: 200 },
                { displayName: 'RJS_Cycle', field: 'RJS_Cycle', width: 200 },
                { displayName: 'RJS_Source', field: 'RJS_Source',width: 200 },
                { displayName: 'RJS_Available', field: 'RJS_Available', width: 200 },
                { displayName: 'RJS_Vintage', field: 'RJS_Vintage', width: 200 },
                { displayName: 'RJS_Released', field: 'RJS_Released', width: 200 },
                { displayName: 'ERM_Cycle', field: 'ERM_Cycle', width: 200 },
                { displayName: 'ERM_Source', field: 'ERM_Source',width: 200 },
                { displayName: 'ERM_Available', field: 'ERM_Available', width: 200 },
                { displayName: 'ERM_Vintage', field: 'ERM_Vintage', width: 200 },
                { displayName: 'ERM_Released', field: 'ERM_Released', width: 200 },
                { displayName: 'RF_Cycle', field: 'RF_Cycle', width: 150 },
                { displayName: 'RF_Source', field: 'RF_Source',width: 200 },
                { displayName: 'RF_Available', field: 'RF_Available', width: 200 },
                { displayName: 'RF_Version', field: 'RF_Version', width: 150 },
                { displayName: 'RF_Vintage', field: 'RF_Vintage', width: 200 },
                { displayName: 'RF_Released', field: 'RF_Released', width: 200 },
                { displayName: 'EGM_Cycle', field: 'EGM_Cycle', width: 200 },
                { displayName: 'EGM_Source', field: 'EGM_Source', width: 200 },
                { displayName: 'EGM_Available', field: 'EGM_Available', width: 200 },
                { displayName: 'EGM_Data_Vintage', field: 'EGM_Vintage', width: 200 },
                { displayName: 'EGM_Released', field: 'EGM_Released', width: 200 },
                { displayName: 'EGM_Postal', field: 'EGM_Postal', width: 200 },
                { displayName: 'EGM_City', field: 'EGM_City',width: 200 },
                { displayName: 'EGM_Locality', field: 'EGM_Locality', width: 200 },
                { displayName: 'EGM_Street_Centroid', field: 'EGM_Street_Centroid', width: 200 },
                { displayName: 'EGM_Street_Interpolation', field: 'EGM_Street_Interpolation', width: 200 },
                { displayName: 'EGM_Address_Point', field: 'EGM_Address_Point', width: 200 },
                { displayName: 'EGM_Maximum_Precision', field: 'EGM_Maximum_Precision', width: 300 },
                { displayName: 'AD_Cycle', field: 'AD_Cycle', width: 200 },
                { displayName: 'AD_Source', field: 'AD_Source',width: 200 },
                { displayName: 'AD_Available', field: 'AD_Available', width: 200 },
                { displayName: 'AD_Data_Vintage', field: 'AD_Vintage', width: 200 },
                { displayName: 'AD_Released', field: 'AD_Released', width: 200 },
                { displayName: 'AD_Data_Updated', field: 'AD_Updated',width: 200 },
                { displayName: 'Loqate_Cycle', field: 'Loqate_Cycle', width: 200 },
                { displayName: 'Loqate_Source', field: 'Loqate_Source',width: 200 },
                { displayName: 'Loqate_Available', field: 'Loqate_Available', width: 200 },
                { displayName: 'Loqate_Released_Month', field: 'Loqate_Released_Month', width: 200 },
                { displayName: 'Loqate_Verification_Level', field: 'Loqate_Verification_Level', width: 200 },
                { displayName: 'Loqate_Geocoding_Level', field: 'Loqate_Geocoding_Level', width: 250 },
                { displayName: 'Loqate_Power_Search', field: 'Loqate_Power_Search',width: 200 },
                { displayName: 'Anow_Cycle', field: 'ADN_Cycle', width: 200 },
                { displayName: 'Anow_Source', field: 'ADN_Source', width: 200 },
                { displayName: 'Anow_Available', field: 'ADN_Available', width: 200 },
                { displayName: 'Anow_Version', field: 'ADN_Version', width: 150 },
                { displayName: 'Anow_Released_Month', field: 'ADN_Released_Month', width: 200 },
                { displayName: 'Anow_Knowledgebase_Updated', field: 'ADN_Knowledgebase_Updated', width: 200 },
                { displayName: 'Anow_Validation_Level', field: 'ADN_Validation_Level', width: 200 },
                { displayName: 'Anow_RefData_Updated', field: 'ADN_RefData_Updated', width: 200 },
                { displayName: 'Anow_Geocode_Updated', field: 'ADN_Geocode_Updated', width: 200 },
                { displayName: 'ICP_Cycle', field: 'ICP_Cycle', width: 200 },
                { displayName: 'ICP_Source', field: 'ICP_Source',width: 200 },
                { displayName: 'ICP_Available', field: 'ICP_Available', width: 200 },
                { displayName: 'ICP_Released', field: 'ICP_Released', width: 200 },
                { displayName: 'ICP_Updated', field: 'ICP_Updated',width: 200 },
                { displayName: 'ICP_Level', field: 'ICP_Level', width: 200 },
                { displayName: 'Cameo(CallCredit)_Data', field: 'Cameo_Data', width: 200 },
                { displayName: 'Cameo(CallCredit)_Level', field: 'Cameo_Level', width: 400 },
                { displayName: 'DetailedDemo_Data', field: 'DetDem_Data', width: 200 },
                { displayName: 'DetailedDemo_Level', field: 'DetDem_Level', width: 400 },
                { displayName: 'BaseDem_Data', field: 'BaseDem_Data', width: 200 },
                { displayName: 'BaseDem_Level', field: 'BaseDem_Level', width: 400 }
            ];
        }
    };

        

    $scope.loadData = function (item) {

        console.log(item);
        console.log($scope.productlineselected);

        if(item == "Routing J Server")
        {
            $scope.gridOptions.columnDefs = [
            { displayName: 'Region', field: 'Region', width: 150 },
            { displayName: 'Country', field: 'Country', width: 300 },
            { displayName: 'RJS_Cycle', field: 'RJS_Cycle', width: 200 },
            { displayName: 'RJS_Source', field: 'RJS_Source',width: 200 },
            { displayName: 'RJS_Available', field: 'RJS_Available', width: 200 },
            { displayName: 'RJS_Vintage', field: 'RJS_Vintage', width: 200 },
            { displayName: 'RJS_Released', field: 'RJS_Released', width: 200 }

            ];
        }
        else if (item == "ERM") {
            $scope.gridOptions.columnDefs = [
            { displayName: 'Region', field: 'Region', width: 150 },
            { displayName: 'ERM_Cycle', field: 'ERM_Cycle', width: 200 },
            { displayName: 'ERM_Source', field: 'ERM_Source',width: 200 },
            { displayName: 'ERM_Available', field: 'ERM_Available', width: 200 },
            { displayName: 'ERM_Vintage', field: 'ERM_Vintage', width: 200 },
            { displayName: 'ERM_Released', field: 'ERM_Released', width: 200 }

            ];
        }
        else if (item == "Route Finder") {
            $scope.gridOptions.columnDefs = [
            { displayName: 'Region', field: 'Region', width: 150 },
            { displayName: 'Country', field: 'Country', width: 300 },
            { displayName: 'RF_Cycle', field: 'RF_Cycle', width: 150 },
            { displayName: 'RF_Source', field: 'RF_Source',width: 200 },
            { displayName: 'RF_Available', field: 'RF_Available', width: 200 },
            { displayName: 'RF_Version', field: 'RF_Version', width: 150 },
            { displayName: 'RF_Vintage', field: 'RF_Vintage', width: 200 },
            { displayName: 'RF_Released', field: 'RF_Released', width: 200 }

            ];
        }
        else if (item == "StreetPro") {
            $scope.gridOptions.columnDefs = [
            { displayName: 'Region', field: 'Region', width: 150 },
            { displayName: 'Country', field: 'Country', width: 300 },
            { displayName: 'StreetPro_Cycle', field: 'StreetPro_Cycle', width: 200 },
            { displayName: 'StreetPro_Data_Source', field: 'StreetPro_Data_Source', width: 200 },
            { displayName: 'StreetPro_Available', field: 'StreetPro_Available', width: 200 },
            { displayName: 'StreetPro_Vintage', field: 'StreetPro_Vintage', width: 200 },
            { displayName: 'StreetPro_Released', field: 'StreetPro_Released', width: 200 }

            ];
        }
        else if (item == "StreetPro Nav") {
            $scope.gridOptions.columnDefs = [
            { displayName: 'Region', field: 'Region', width: 300 },
            { displayName: 'Country', field: 'Country', width: 150 },
            { displayName: 'StreetProNav_Cycle', field: 'StreetProNav_Cycle', width: 150 },
            { displayName: 'StreetProNav_Data_Source', field: 'StreetProNav_Data_Source', width: 200 },
            { displayName: 'StreetProNav_Available', field: 'StreetProNav_Available', width: 200 },
            { displayName: 'StreetProNav_Vintage', field: 'StreetProNav_Vintage', width: 200 },
            { displayName: 'StreetProNav_Released', field: 'StreetProNav_Released', width: 200 }

            ];
        }
        else if (item == "StreetPro World") {
            $scope.gridOptions.columnDefs = [
            { displayName: 'Region', field: 'Region', width: 150 },
            { displayName: 'Country', field: 'Country', width: 300 },
            { displayName: 'StreetProWrld_Cycle', field: 'StreetProWrld_Cycle', width: 150 },
            { displayName: 'StreetProWrld_Data_Source', field: 'StreetProWrld_Data_Source', width: 200 },
            { displayName: 'StreetProWrld_Available', field: 'StreetProWrld_Available', width: 200 },
            { displayName: 'StreetProWrld_Vintage', field: 'StreetProWrld_Vintage', width: 200 },
            { displayName: 'StreetProWrld_Released', field: 'StreetProWrld_Released', width: 200 }

            ];
        }
        else if (item == "WPOI") {
            $scope.gridOptions.columnDefs = [
            { displayName: 'Region', field: 'Region', width: 150 },
            { displayName: 'Country', field: 'Country', width: 300 },
            { displayName: 'WPOI_Cycle', field: 'WPOI_Cycle', width: 250 },
            { displayName: 'WPOI_Data_Source', field: 'WPOI_Data_Source', width: 200 },
            { displayName: 'WPOI_Data_Available', field: 'WPOI_Data_Available', width: 200 },
            { displayName: 'WPOI_Vintage', field: 'WPOI_Vintage', width: 200 },
            { displayName: 'WPOI_Released_Month', field: 'WPOI_Released_Month', width: 200 }

            ];
        }
        else if (item == "WPPOI") {
            $scope.gridOptions.columnDefs = [
            { displayName: 'Region', field: 'Region', width: 150 },
            { displayName: 'Country', field: 'Country', width: 300 },
            { displayName: 'WPPOI_Cycle', field: 'WPPOI_Cycle', width: 150 },
            { displayName: 'WPPOI_Data_Source', field: 'WPPOI_Data_Source', width: 250 },
            { displayName: 'WPPOI_Data_Available', field: 'WPPOI_Data_Available', width: 200 },
            { displayName: 'WPPOI_Version', field: 'WPPOI_Version', width: 150 },
            { displayName: 'WPPOI_Released_Month', field: 'WPPOI_Released_Month', width: 200 }

            ];
        }
        else if (item == "WBO") {
            $scope.gridOptions.columnDefs = [
            { displayName: 'Region', field: 'Region', width: 150 },
            { displayName: 'Country', field: 'Country', width: 300 },
            { displayName: 'WBO_Cycle', field: 'WBO_Cycle', width: 250 },
            { displayName: 'WBO_Data_Source', field: 'WBO_Data_Source', width: 200 },
            { displayName: 'WBO_Data_Available', field: 'WBO_Data_Available', width: 200 },
            { displayName: 'WBO_Vintage', field: 'WBO_Vintage', width: 200 },
            { displayName: 'WBO_Released_Month', field: 'WBO_Released_Month', width: 200 }

            ];
        }
        else if (item == "Suburbs & Localities") {
            $scope.gridOptions.columnDefs = [
            { displayName: 'Region', field: 'Region', width: 150 },
            { displayName: 'Country', field: 'Country', width: 300 },
            { displayName: 'Other_Boundaries_Cycle', field: 'Admin_Cycle', width: 300 },
            { displayName: 'Other_Boundaries_Source', field: 'Admin_Source', width: 200 },
            { displayName: 'Other_Boundaries_Available', field: 'Admin_Available', width: 300 },
            { displayName: 'Other_Boundaries_Vintage', field: 'Admin_Vintage', width: 200 },
            { displayName: 'Other_Boundaries_Data_Released', field: 'Admin_Data_Released', width: 200 }

            ];
        }
        else if (item == "Cameo(CallCredit)") {
            $scope.gridOptions.columnDefs = [
            { displayName: 'Region', field: 'Region', width: 150 },
            { displayName: 'Country', field: 'Country', width: 300 },
            { displayName: 'Cameo(CallCredit)_Data', field: 'Cameo_Data', width: 200 },
            { displayName: 'Cameo(CallCredit)_Level', field: 'Cameo_Level', width: 400 }

            ];
        }
        else if (item == "Detailed Demographics") {
            $scope.gridOptions.columnDefs = [
            { displayName: 'Region', field: 'Region', width: 150 },
            { displayName: 'Country', field: 'Country', width: 300 },
            { displayName: 'DetailedDemo_Data', field: 'DetDem_Data', width: 200 },
            { displayName: 'DetailedDemo_Level', field: 'DetDem_Level', width: 400 }

            ];
        }
        else if (item == "Base Demographics") {
            $scope.gridOptions.columnDefs = [
            { displayName: 'Region', field: 'Region', width: 150 },
            { displayName: 'Country', field: 'Country', width: 300 },
            { displayName: 'BaseDem_Data', field: 'BaseDem_Data', width: 200 },
            { displayName: 'BaseDem_Level', field: 'BaseDem_Level', width: 400 }

            ];
        }
        else if (item == "Spectrum EGM") {
            $scope.gridOptions.columnDefs = [
            { displayName: 'Region', field: 'Region', width: 150 },
            { displayName: 'Country', field: 'Country', width: 300 },
            { displayName: 'EGM_Cycle', field: 'EGM_Cycle', width: 200 },
            { displayName: 'EGM_Source', field: 'EGM_Source', width: 200 },
            { displayName: 'EGM_Available', field: 'EGM_Available', width: 200 },
            { displayName: 'EGM_Data_Vintage', field: 'EGM_Vintage', width: 200 },
            { displayName: 'EGM_Released', field: 'EGM_Released', width: 200 },
            { displayName: 'EGM_Postal', field: 'EGM_Postal', width: 200 },
            { displayName: 'EGM_City', field: 'EGM_City',width: 200 },
            { displayName: 'EGM_Locality', field: 'EGM_Locality', width: 200 },
            { displayName: 'EGM_Street_Centroid', field: 'EGM_Street_Centroid',width: 200 },
            { displayName: 'EGM_Street_Interpolation', field: 'EGM_Street_Interpolation',width: 200 },
            { displayName: 'EGM_Address_Point', field: 'EGM_Address_Point',width: 200 },
            { displayName: 'EGM_Maximum_Precision', field: 'EGM_Maximum_Precision', width: 300 }

            ];
        }
        else if (item == "Address Doctor") {
            $scope.gridOptions.columnDefs = [
            { displayName: 'Region', field: 'Region', width: 150 },
            { displayName: 'Country', field: 'Country', width: 300 },
            { displayName: 'AD_Cycle', field: 'AD_Cycle', width: 200 },
            { displayName: 'AD_Source', field: 'AD_Source',width: 200 },
            { displayName: 'AD_Available', field: 'AD_Available', width: 200 },
            { displayName: 'AD_Data_Vintage', field: 'AD_Vintage', width: 200 },
            { displayName: 'AD_Released', field: 'AD_Released', width: 200 },
            { displayName: 'AD_Data_Updated', field: 'AD_Updated',width: 200 }

            ];
        }
        else if (item == "Loqate") {
            $scope.gridOptions.columnDefs = [
            { displayName: 'Region', field: 'Region', width: 150 },
            { displayName: 'Country', field: 'Country', width: 300 },
            { displayName: 'Loqate_Cycle', field: 'Loqate_Cycle', width: 200 },
            { displayName: 'Loqate_Source', field: 'Loqate_Source',width: 200 },
            { displayName: 'Loqate_Available', field: 'Loqate_Available', width: 200 },
            { displayName: 'Loqate_Released_Month', field: 'Loqate_Released_Month', width: 200 },
            { displayName: 'Loqate_Verification_Level', field: 'Loqate_Verification_Level', width: 200 },
            { displayName: 'Loqate_Geocoding_Level', field: 'Loqate_Geocoding_Level', width: 250 },
            { displayName: 'Loqate_Power_Search', field: 'Loqate_Power_Search',width: 200 }

            ];
        }
        else if (item == "ICP") {
            $scope.gridOptions.columnDefs = [
            { displayName: 'Region', field: 'Region', width: 150 },
            { displayName: 'Country', field: 'Country', width: 300 },
            { displayName: 'ICP_Cycle', field: 'ICP_Cycle', width: 200 },
            { displayName: 'ICP_Source', field: 'ICP_Source',width: 200 },
            { displayName: 'ICP_Available', field: 'ICP_Available', width: 200 },
            { displayName: 'ICP_Released', field: 'ICP_Released', width: 200 },
            { displayName: 'ICP_Updated', field: 'ICP_Updated',width: 200 },
            { displayName: 'ICP_Level', field: 'ICP_Level', width: 200 }

            ];
        }
        else if (item == "Anow") {
            $scope.gridOptions.columnDefs = [
            { displayName: 'Anow_Cycle', field: 'ADN_Cycle', width: 200 },
            { displayName: 'Anow_Source', field: 'ADN_Source', width: 200 },
            { displayName: 'Anow_Available', field: 'ADN_Available', width: 200 },
            { displayName: 'Anow_Version', field: 'ADN_Version', width: 150 },
            { displayName: 'Anow_Released_Month', field: 'ADN_Released_Month', width: 200 },
            { displayName: 'Anow_Knowledgebase_Updated', field: 'ADN_Knowledgebase_Updated', width: 200 },
            { displayName: 'Anow_Validation_Level', field: 'ADN_Validation_Level', width: 200 },
            { displayName: 'Anow_RefData_Updated', field: 'ADN_RefData_Updated', width: 200 },
            { displayName: 'Anow_Geocode_Updated', field: 'ADN_Geocode_Updated', width: 200 }

            ];
        }

    };


    $scope.loadStatsData = function (_tblname) {
        var config = {
            headers: {
                'Content-Type': 'application/json',
            }
        }
        $scope.poistatsdata = [];
        $http.get('' + apiUrl + 'GetPoiStats/' + _tblname, config)
        .success(function (data, status, headers, config) {
            $scope.poistatsdata = data;

        })
        .error(function (data, status, header, config) {
            alert('Unable to fetch poi stats data !!!');
        });
    };


    $scope.loadStats = function () {
        $location.path('/stats');
        //$window.open('DynamicStatistics.html');
    };


});

app.controller('mapController', function ($scope, $http, $location, $route, uiGridExporterService, uiGridExporterConstants, $timeout, cfpLoadingBar, uiGridConstants, $window) {

    var productname;
    var apiUrl = 'http://152.144.227.176:8080/ProductWS/jaxrs/WebService/';

    var map, mapZoom; 

    var latCenter = 53.42135;
    var longCenter = -1.28498;
    var color = "#000000";
    var mapdata = [];
    var countyLayer = new google.maps.Data();
    var countryLayer = new google.maps.Data();
    var addedLayers = [];
    var countryflag = false, countyflag = false;
    //var infoWindow = new google.maps.InfoWindow({
    //    content: ""
    //});

    var my_boundaries = {};
    var data_layer;
    var info_window;
  
    $scope.map = new google.maps.Map(document.getElementById('map'), {
        zoom: 3,
        center: new google.maps.LatLng(latCenter, longCenter),
        mapTypeId: google.maps.MapTypeId.ROADMAP
    });

    google.maps.event.addListener($scope.map, 'click', function () {
        if ($scope.info_window) {
            $scope.info_window.setMap(null);
            $scope.info_window = null;
        }
    });


    $scope.loadCountyMapData = function () {
        $scope.clearMap();
        $http.get("usa_counties.geo.json")
           .then(function (res) {

               countyLayer = new google.maps.Data();

               countyLayer.addGeoJson(res.data);
               countyLayer.setStyle({});
               countyLayer.setStyle(function (feature) {
                   var countyname = feature.getProperty('NAME');                   
                  
                   $scope.color = $scope.countrychk(countyname);
                   if ($scope.color == null)
                       $scope.color = 'red';
                   //console.log($scope.color);
                   return {
                       fillColor: $scope.color,
                                  strokeWeight: .5
                   }
               });
               countyLayer.setMap($scope.map);
               $scope.zoom($scope.map, countyLayer);
               addedLayers.push(countyLayer);

               countyLayer.addListener('mouseover', function (event) {
                   //show an infowindow on click   
                   infoWindow.setContent('<div style="line-height:1.35;overflow:hidden;white-space:nowrap;">'+
                       'County  = ' + event.feature.getProperty("NAME") +"</div>");
                   var anchor = new google.maps.MVCObject();
                   anchor.set("position", event.latLng);
                   infoWindow.open($scope.map, anchor);
               });
           });
    }



    $scope.loadCountryMapData = function () {
        $scope.initializeDataLayer();
        $http.get("world.countries.geo.json")
           .then(function (response) {
             
               if(response.data.type == "FeatureCollection"){ //we have a collection of boundaries in geojson format
                   if (response.data.features) {
                       for (var i = 0; i < response.data.features.length; i++) {
                           var boundary_id = i + 1;
                           var new_boundary = {};
                           if (!response.data.features[i].properties) {
                               response.data.features[i].properties = {};
                           }
                           response.data.features[i].properties.boundary_id = boundary_id; //we will use this id to identify boundary later when clicking on it
                           $scope.data_layer.addGeoJson(response.data.features[i], { idPropertyName: 'boundary_id' });
                           new_boundary.feature = $scope.data_layer.getFeatureById(boundary_id);
                           if (response.data.features[i].properties.name) {
                               new_boundary.name = response.data.features[i].properties.name;
                           }
                           if (response.data.features[i].properties.NAME) {
                               new_boundary.name = response.data.features[i].properties.NAME;
                           }
                           my_boundaries[boundary_id] = new_boundary;
                       }
                   }
                   if(my_boundaries[24]){ //just an example, that you can change styles of individual boundary
                       $scope.data_layer.overrideStyle(my_boundaries[24].feature, {
                           fillColor: '#0000FF',
                           fillOpacity: 0.9
                       });
                   }
               }
           });

    }


    $scope.countrychk = function (_chkfor) {
        
        var arrmapData = $scope.mapdata;
        var arrfromjson = [], arrfromdb = [];
        var jsoncntry = '', dbcntry = '';

        for (var i = 0; i < arrmapData.length; i++) {
            //console.log(mapdata[i].toLowerCase());
            //if (arrmapData[i].toLowerCase() == _chkfor.toLowerCase())
            //{
            //    $scope.color = 'green';
            //    return $scope.color;
            //    break;
            //}

            arrfromjson = _chkfor.split(' ');
            if (arrfromjson.length > 0)
                jsoncntry = arrfromjson[0].toLowerCase();

            arrfromdb = arrmapData[i].split(' ');
            if (arrfromdb.length > 0)
                dbcntry = arrfromdb[0].toLowerCase();

            if (jsoncntry == dbcntry) {
                console.log(jsoncntry +"::"+ dbcntry)
                color = 'green';
                return color;
                break;
            }
            
        }
    };

    $scope.initializeDataLayer = function(){
        if ($scope.data_layer) {
            $scope.data_layer.forEach(function (feature) {
                $scope.data_layer.remove(feature);
            });
            $scope.data_layer = null;
        }
        $scope.data_layer = new google.maps.Data({ map: $scope.map }); //initialize data layer which contains the boundaries. It's possible to have multiple data layers on one map
        $scope.data_layer.setStyle({ //using set style we can set styles for all boundaries at once
            fillColor: 'white',
            strokeWeight: 1,
            fillOpacity: 0.1
        });

        $scope.data_layer.addListener('click', function (e) { //we can listen for a boundary click and identify boundary based on e.feature.getProperty('boundary_id'); we set when adding boundary to data layer
            var boundary_id = e.feature.getProperty('boundary_id');
            var boundary_name = "NOT SET";

            if (boundary_id && $scope.my_boundaries[boundary_id] && $scope.my_boundaries[boundary_id].name) {
                boundary_name = $scope.my_boundaries[boundary_id].name;
            }
            if ($scope.info_window) {
                $scope.info_window.setMap(null);
                $scope.info_window = null;
            }
            $scope.info_window = new google.maps.InfoWindow({
                content: '<div>You have clicked a boundary: <span style="color:red;">' + boundary_name + '</span></div>',
                size: new google.maps.Size(150,50),
                position: e.latLng, map: map
            });
        });
        $scope.data_layer.addListener('mouseover', function (e) {
            $scope.data_layer.overrideStyle(e.feature, {
                strokeWeight: 3,
                strokeColor: '#ff0000'
            });
        });

        $scope.data_layer.addListener('mouseout', function (e) {
            $scope.data_layer.overrideStyle(e.feature, {
                strokeWeight: 1,
                strokeColor: ''
            });
        });
    }


    $scope.clearMap = function()
    {
        countryLayer.forEach(function (feature) {
            //filter...
            countryLayer.remove(feature);
            countryLayer.setMap(null);
        });
        countyLayer.forEach(function (feature) {
            //filter...
            countyLayer.remove(feature);
            countyLayer.setMap(null);
        });
        countryLayer = [];
        countyLayer = [];
    }


    $scope.loadMapData = function () {
        //console.log($scope.statselected);
        if ($scope.countyflag) {
            $scope.loadCountyMapData();

        }
        else if ($scope.countryflag) {
            $scope.loadCountryMapData();
        }

    }

    /**
     * Process each point in a Geometry, regardless of how deep the points may lie.
     * @param {google.maps.Data.Geometry} geometry The structure to process
     * @param {function(google.maps.LatLng)} callback A function to call on each
     *     LatLng point encountered (e.g. Array.push)
     * @param {Object} thisArg The value of 'this' as provided to 'callback' (e.g.
     *     myArray)
     */
    $scope.processPoints = function (geometry, callback, thisArg) {
        if (geometry instanceof google.maps.LatLng) {
            callback.call(thisArg, geometry);
        } else if (geometry instanceof google.maps.Data.Point) {
            callback.call(thisArg, geometry.get());
        } else {
            geometry.getArray().forEach(function (g) {
                $scope.processPoints(g, callback, thisArg);
            });
        }
    }

    $scope.zoom = function (map, currentlayer) {
        var bounds = new google.maps.LatLngBounds();
        currentlayer.forEach(function (feature) {
            $scope.processPoints(feature.getGeometry(), bounds.extend, bounds);
        });
        $scope.map.fitBounds(bounds);
        var currzoom = $scope.map.getZoom();
        $scope.map.setZoom(currzoom + 2);
    }


    //*************************************************************************************************************************
    ////For Loading up ProductLine Data to Dropdown
    //##########################################################################################################################       
    $scope.loadProductline = function () {
        var config = {
            headers: {
                'Content-Type': 'application/json',
            }
        }
        $scope.productlines = [];
        $http.get('' + apiUrl + 'GetProductline', config)
        .success(function (data, status, headers, config) {
            for (var i = 0; i < data.length; i++) {
                $scope.productlines.push(data[i].product_line);
            }
            //console.log($scope.countries);
        })
        .error(function (data, status, header, config) {
            alert('Unable to get Productlines !!!');
        });
    };

    //**************************************************************************************************************************
    $scope.loadProductline();
    //**************************************************************************************************************************


    //*************************************************************************************************************************
    ////For Loading up Producs Data to Dropdown
    //##########################################################################################################################       
    $scope.loadProducts = function (_product) {
        var config = {
            headers: {
                'Content-Type': 'application/json',
            }
        }
        $scope.products = [];
        $http.get('' + apiUrl + 'GetProducts/' + _product, config)
        .success(function (data, status, headers, config) {
            for (var i = 0; i < data.length; i++) {
                $scope.products.push(data[i].products);
            }
            //console.log($scope.countries);
        })
        .error(function (data, status, header, config) {
            alert('Unable to get Products !!!');
        });
    };


    //*************************************************************************************************************************
    ////For Loading up First Filter to Dropdown
    //##########################################################################################################################  

    $scope.loadFilters = function (selectedproduct) {
        productname = selectedproduct;
        if (selectedproduct == 'Social Places') {
            $scope.loadSecondFilterData(selectedproduct);
        }
            //kunaledit
        else if (selectedproduct == 'PropertyAttributefabric') {
            $scope.loadSecondFilterData(selectedproduct);
        }
        else if (selectedproduct == 'PropertyAttributeParcel') {
            $scope.loadSecondFilterData(selectedproduct);
        }
        else if (selectedproduct == 'PropertyAttributeGEM') {
            $scope.loadSecondFilterData(selectedproduct);
        }
        else {
            $scope.socialplacestd = true;
            $scope.loadFirstFilter(selectedproduct);
        }
    };


    //Loading First Filter data in the drop down
    $scope.loadFirstFilter = function (_product) {
        var config = {
            headers: {
                'Content-Type': 'application/json',
            }
        }
        $scope.productStats = [];
        $http.get('' + apiUrl + 'GetFirstFilter/' + _product, config)
        .success(function (data, status, headers, config) {
            for (var i = 0; i < data.length; i++) {
                $scope.productStats.push(data[i].filter2);
            }
            console.log($scope.productStats);
        })
        .error(function (data, status, header, config) {
            alert('Unable to get First Filter Data !!!');
        });
    };


    //Loading Social Places data in the drop down
    $scope.loadSecondFilterData = function (_product) {

        $scope.socialplacestd = false;
        var config = {
            headers: {
                'Content-Type': 'application/json',
            }
        }
        $scope.socialplaces = [];
        $http.get('' + apiUrl + 'GetSecondFilterData/' + _product, config)
        .success(function (data, status, headers, config) {
            if (_product == "PropertyAttributefabric") {
                $scope.socialplaces.push("USA");
                for (var i = 1; i < data.length; i++) {
                    if (data[i].filter1 != "USA") {
                        $scope.socialplaces.push(data[i].filter1);
                    }
                }
            }
            else if (_product == "PropertyAttributeGEM") {
                $scope.socialplaces.push("USA");
                for (var i = 0; i < data.length; i++) {
                    if (data[i].filter1 != "USA") {
                        $scope.socialplaces.push(data[i].filter1);
                    }
                }

            }
            else if (_product == "PropertyAttributeParcel") {
                $scope.socialplaces.push("USA");
                for (var i = 0; i < data.length; i++) {
                    if (data[i].filter1 != "USA") {
                        $scope.socialplaces.push(data[i].filter1);
                    }
                }
            }
            else {
                for (var i = 0; i < data.length; i++) {
                    if (data[i].filter1 != "USA") {
                        $scope.socialplaces.push(data[i].filter1);
                    }
                }
            }


            //console.log($scope.countries);
        })
        .error(function (data, status, header, config) {
            alert('Unable to load Social Places !!!');
        });
    };


    //Loading Social Places data in the drop down
    $scope.loadTables = function (_subproduct) {
        // _product = 'Social Places';
        var config = {
            headers: {
                'Content-Type': 'application/json',
            }
        }
        $scope.productStats = [];
        /*
        $http.get('' + apiUrl + 'GetSecondFilter/' + _product + '/' + _subproduct, config)
        .success(function (data, status, headers, config) {
            for (var i = 0; i < data.length; i++) {
                $scope.productStats.push(data[i].filter2);
            }
            //console.log($scope.countries);
        })
        .error(function (data, status, header, config) {
            alert('Unable to load Social Places Stats Tables !!!');
        });*/
        _product = productname;
        $scope.productStats = [];
        $http.get('' + apiUrl + 'GetSecondFilter/' + _product + '/' + _subproduct, config)
        .success(function (data, status, headers, config) {
            for (var i = 0; i < data.length; i++) {
                $scope.productStats.push(data[i].filter2);
            }
            //console.log($scope.countries);
        })
        .error(function (data, status, header, config) {
            alert('Unable to load Social Places Stats Tables !!!');
        });
    };

    //**************************************************************************************************************************
    //getting product stats data 
    //##########################################################################################################################
    $scope.loadStats = function (_tblname) {

        $scope.countryflag = false;
        $scope.countyflag = false;

        if ((_tblname == "ppoi_counts_by_category_country") || (_tblname == "ppoic_counts_by_category_country")) {
            //if (_tblname == "ppoi_counts_by_category_country") {
            $scope.countrytd = false;
            $scope.loadCountries();
        }
        else {
            $scope.countrytd = true;
            var config = {
                headers: {
                    'Content-Type': 'application/json',
                }
            }
            $scope.poistatsdata = [];
            $http.get('' + apiUrl + 'GetPoiStats/' + _tblname, config)
            .success(function (data, status, headers, config) {
                $scope.poistatsdata = data;
                if ($scope.poistatsdata.length > 0) {
                    var arrData = Object.getOwnPropertyNames($scope.poistatsdata[0]);

                    for (var i = 0; i < arrData.length; i++) {

                        console.log(arrData[i]);
                        if (arrData[i].toLocaleLowerCase() == 'country') {
                            $scope.countryflag = true;
                            //$scope.countyflag = false;
                        }
                    }

                    if ($scope.countryflag) {
                        $scope.mapdata = [];
                        for (var i = 0; i < data.length; i++) {
                            $scope.mapdata.push(data[i].Country);
                        }
                    }

                    if (_tblname == 'parcel_data_coverage') {
                        $scope.mapdata = [];
                        for (var i = 0; i < data.length; i++) {
                            $scope.mapdata.push(data[i].county);
                        }
                        $scope.countyflag = true;
                    }
                }
            })
            .error(function (data, status, header, config) {
                alert('Unable to fetch poi stats data !!!');
            });
        }

    };
    //***********************************************************************************************************************

    //$scope.loadStats('micode');

    //$scope.gridStatsOptions.columnDefs = [
    //{ displayName: 'TradeDivision', field: 'tradedivision', width: 400 },
    //{ displayName: 'TradeGroup', field: 'tradegroup', width: 400 },
    //{ displayName: 'Class', field: 'class', width: 400 },
    //{ displayName: 'SubClass', field: 'subclass', width: 400 },
    //{ displayName: 'Sic', field: 'sic', width: 150 },
    //{ displayName: 'Sic8', field: 'sic8', width: 150 },
    //{ displayName: 'MicodeSic8', field: 'micodesic8', width: 150 },
    //{ displayName: 'Sic8_Description', field: 'sic8description', width: 400 },
    //{ displayName: 'Search_Description_Engine', field: 'search_description_engine', width: 400 },
    //{ displayName: 'Grouping', field: 'grouping', width: 120 },
    //{ displayName: 'Source', field: 'source', width: 120 },
    //{ displayName: 'AnzCode', field: 'anzcode', width: 120 },
    //{ displayName: 'AnzDescription', field: 'anzdescription', width: 400 },
    //{ displayName: 'ConsumerPoi', field: 'consumerpoi', width: 120 },
    //{ displayName: 'NonBussiness', field: 'nonbussiness', width: 120 },
    //{ displayName: 'OwneriqMicodes', field: 'owneriqmicodes', width: 120 }
    //];

    //*************************************************************************************************************************
    ////For Loading up countrieswise stats data
    //##########################################################################################################################       
    $scope.loadCountries = function () {
        var config = {
            headers: {
                'Content-Type': 'application/json',
            }
        }
        $scope.countries = [];
        $http.get('' + apiUrl + 'GetCountries', config)
        .success(function (data, status, headers, config) {

            for (var i = 0; i < data.length; i++) {
                $scope.countries.push(data[i].Country);
            }


            console.log($scope.countries);

        })
        .error(function (data, status, header, config) {
            alert('Unable to fetch country data !!!');
        });
    };

    //**************************************************************************************************************************
    //getting poi stats for country for POI
    //##########################################################################################################################

    $scope.loadCountryStats = function (_country) {

        var config = {
            headers: {
                'Content-Type': 'application/json',
            }
        }
        $scope.poicountrystatsdata = [];
        $http.get('' + apiUrl + 'GetCountryPoiStats/' + $scope.statselected + '/' + _country, config)
        .success(function (data, status, headers, config) {
            $scope.poistatsdata = data;

            //console.log(data);
            $scope.gridStatsOptions.data = $scope.poistatsdata;
            $scope.gridStatsOptions.height = ($scope.poistatsdata.length * 2) + "px";

            $scope.loadStatsData($scope.statselected, $scope);


            //$scope.gridStatsOptions.columnDefs = [
            //    { displayName: 'Country_Bundles', field: 'countrybundles', width: 300 },
            //    { displayName: 'Countries', field: 'countries', width: 150 },
            //    { displayName: 'ISO3', field: 'iso3', width: 150 },
            //    { displayName: 'Trade_Division', field: 'tradedivision', width: 500 },
            //    { displayName: 'Group', field: 'group', width: 500 },
            //    { displayName: 'Class', field: 'class', width: 500 },
            //    { displayName: 'SubClass', field: 'subclass', width: 500 },
            //    { displayName: 'SIC8', field: 'sic8', width: 150 },
            //    { displayName: 'Micode', field: 'micode', width: 150 },
            //    { displayName: 'Description', field: 'description', width: 500 },
            //    { displayName: 'PoiCounts', field: 'poicounts', width: 160 },
            //    { displayName: 'Version', field: 'version', width: 120 },
            //    { displayName: 'Delta', field: 'delta', width: 120 }
            //];
        })
        .error(function (data, status, header, config) {
            alert('Unable to fetch country poi stats data !!!');
        });

    };

    //**************************************************************************************************************************
    $scope.toTitleCase = function (string) {
        // \u00C0-\u00ff for a happy Latin-1
        return string.toLowerCase().replace(/_/g, ' ').replace(/\b([a-z\u00C0-\u00ff])/g, function (_, initial) {
            return initial.toUpperCase();
        }).replace(/(\s(?:de|a|o|e|da|do|em|ou|[\u00C0-\u00ff]))\b/ig, function (_, match) {
            return match.toLowerCase();
        });
    }
    //*************************************************************************************************************************
    //Retun back to Product View
    $scope.return = function () {
        $location.path('/product');
    };

    $scope.getdemographics = function () {
        $location.path('/demographics');
    };

    $scope.getfootnotes = function () {
        $location.path('/footnotes');
    };
    //**************************************************************************************************************************
    //loading the data to the datagrid for stats page
    //##########################################################################################################################
    $scope.loadStatsData = function (item, $scope) {
        var arrData = [];
        var _content = '', propercasestr = '';

        var arrData = Object.getOwnPropertyNames($scope.poistatsdata[0]);
        var griddata = '';

        $scope.gridStatsOptions.columnDefs = new Array();

        for (var i = 0; i < arrData.length; i++) {

            propercasestr = $scope.toTitleCase(arrData[i]);
            propercasestr = propercasestr.replace("'", '');
            propercasestr = propercasestr.replace("'", '');
            $scope.gridStatsOptions.columnDefs.push({
                field: arrData[i],

                displayName: propercasestr,
                width: 300,
                /*filter: 'text',
				filter: {
				 //kunal edit
					condition: uiGridConstants.filter.CONTAINS,
				}*/
            });
        }

        if (item == "base_demographics_description") {
            $window.open('BaseDemographics.html');
        }
        else if (item == "base_demographics_footnotes") {
            $window.open('Footnotes.html');
        }

    };


});


