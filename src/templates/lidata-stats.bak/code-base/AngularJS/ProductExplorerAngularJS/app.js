

//var app = angular.module('productApp', ['ngRoute', 'ngAnimate', 'ngSanitize', 'ui.bootstrap', 'chieffancypants.loadingBar', 'ui.grid', 'toastr', 'ui.grid.pagination', 'ui.grid.selection', 'ui.grid.exporter', 'ui.grid.resizeColumns', 'ui.grid.pinning']);
var app = angular.module('productApp', ['ngRoute', 'ngAnimate', 'ngSanitize', 'ui.bootstrap', 'chieffancypants.loadingBar', 'ui.grid', 'toastr', 'ui.grid.pagination', 'ui.grid.selection', 'ui.grid.exporter', 'ui.grid.resizeColumns', 'ui.grid.moveColumns', 'ui.grid.autoResize', 'ui.grid.autoFitColumns']);
app.config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
    $routeProvider
        //.when('/product', {
        //    templateUrl: 'views/productView.html',
        //    controller: 'productController'
        //})
        .when('/stats', {
            templateUrl: 'views/statsView.html',
            controller: 'statsController'
        })
        .when('/docs', {
            templateUrl: 'views/docsView.html',
            controller: 'docsController'
        })
        .when('/map', {
            templateUrl: 'views/mapView.html',
            controller: 'mapController'
        })

        .otherwise({
            redirectTo: '/stats'
        });


}]);

//Directive to adjust UI Grid Dynamically on run time
app.directive('adjustHeight', ['$timeout', '$window', function ($timeout, $window) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var setGridHeight = function () {
                $('#changes-height-stats').height($(element).height() / 4);
            };

            $timeout(setGridHeight);  // resizes the grid but not the render-container
            angular.element($window).bind('resize', setGridHeight);
        }
    };
}]);

//app.directive('back', ['$window', function ($window) {
//    return {
//        restrict: 'A',
//        link: function (scope, elem, attrs) {
//            elem.bind('click', function () {
//               // $window.history.back();
//            });
//        }
//    };
//}]);

app.config(function (cfpLoadingBarProvider) {
    cfpLoadingBarProvider.includeSpinner = true;

});

/// <summary>Filter Service so that filters can be accessed accross pages</summary>
/// <returns type="Object" />
app.service('SharedFilterService', function () {
    //var Filters = {
    //    productlineselected: '',
    //    productselected: '',
    //    statselected: '',
    //    selproductline: '',
    //    selproduct: '',
    //    selsocialplaces: '',
    //    selfirstfilter: '',
    //    selstats: '',
    //    fipsselected: ''
    //};

    var Filters = {
        productlineselected: 'GlobalDataCoverage',
        productselected: 'GlobalDataScorecard',
        statselected: 'productmaster',
        selproductline: '',
        selproduct: '',
        selsocialplaces: '',
        selfirstfilter: '',
        selstats: '',
        demogrphicsdesc: '',
        demogrphicsfootnotes: '',
        demogrphicsdatasource: '',
        demogrphicsmethodology: '',
        pageload : ''
    };
    return Filters;
});


//Stats Controller
app.controller('statsController', function ($scope, $http, $location, $route, toastr, uiGridExporterService, uiGridExporterConstants, $timeout, cfpLoadingBar, uiGridConstants, $window, SharedFilterService) {

    //Service getting the selected filters accross the views
    $scope.Filters = SharedFilterService;
    //console.log("ProductLine ::"+$scope.Filters.productlineselected + " Product ::" + $scope.Filters.productselected + " Table:: " + $scope.Filters.statselected);
    var productname;
    $scope.alertMessage = true;
    //Variables for hiding and unhiding some divs
    $scope.socialplacestd = true;
    $scope.countrytd = true;
    $scope.statestd = true;
    $scope.controlfips = true;
    $scope.enableFiltering = true;
    //$scope.firstpageload = false;

    $scope.productData = [];
    $scope.productStats = [];
    $scope.poistatsdata = [];
    $scope.poicountrystatsdata = [];
    $scope.countries = [];
    $scope.socialplaces = [];
    $scope.productlines = [];
    $scope.fipscodes = [];

    $scope.selectedproduct = null;
    $scope.statselected = null;
    $scope.countryselected = null;
    $scope.firstfilterselected = null;
    $scope.socialplacesselected = null;
    $scope.selectedstate = null;

    $scope.productMap = {};
    $scope.hyperlinksMap = {};
    $scope.coverageMap = {};

    $scope.productlineclickedflag = false;
    $scope.productclickedflag = false;
    $scope.firstfilterclickedflag = false;
    $scope.secondfilterclickedflag = false;

    //$scope.statsclickedflag = false;

    $scope.loadGrid = function()
    {
        $location.path('/stats');
    }

    $scope.loadMap = function () {
         $location.path('/map');
    }




    $scope.toggle = function () {
        //console.log($('.btnClose'));
        if ($scope.hide) {
            $scope.hide = false;
            $('.btnPanel').css('left', '23%');
            $('.rightPanelStats').css('left', '25%');
            $('.rightPanelStats').css('width', '75%');
        }
        else {
            $scope.hide = true;
            $('.btnPanel').css('left', '0px');
            $('.rightPanelStats').css('left', '0px');
            $('.rightPanelStats').css('width', '100%');
        }
        //divleftpanel  pb-ds-fade-in-left  pb-ds-fade-in-right
        if ($('#slidericon').hasClass('nc-icon-outline ui-3_slide-right')) {
            $("#slidericon").removeClass('nc-icon-outline ui-3_slide-right').addClass('nc-icon-outline ui-3_slide-left');
            
        }
        else if ($('#slidericon').hasClass('nc-icon-outline ui-3_slide-left')) {
            $("#slidericon").removeClass('nc-icon-outline ui-3_slide-left').addClass('nc-icon-outline ui-3_slide-right');
        }

        if($scope.hide == true)
        {
            $("#divleftpanel").removeClass('pb-ds-fade-in-left').addClass('pb-ds-fade-out-left');
        }
        else  {
            $("#divleftpanel").removeClass('pb-ds-fade-out-left').addClass('pb-ds-fade-in-left');
        }
    }

    var apiUrl = 'http://152.144.227.176:8080/ProductDevWS/jaxrs/WebService/';
    //var apiUrl = 'http://localhost:8080/ProductWS/jaxrs/WebService/';
    $scope.mapNotes = new Map();
    

    /// <summary>Setting up the initial UI Grid properties</summary>
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

        //Registering listeners on grid cell click
        onRegisterApi: function (gridApi) {
            $scope.gridApi = gridApi;
            gridApi.core.on.renderingComplete($scope, function () {
                $timeout(function () {
                    var gridBodyElem = document.getElementById(gridApi.grid.id + '-grid-container');
                    gridBodyElem.addEventListener('mouseup', $scope.handleGridClick);
                });
            });
        }

    };
    $scope.filteringText = '';
    $scope.filterOptions = {
        filterText: '',
        useExternalFilter: true
    };

//***************************************************************************************************************

    //Reading the hyperlink file for countries for hyperlink to work
    $scope.readFileForDemographics = function () {
        $http.get('country_links.csv').then($scope.processHyperlinks);
    }

    /// <summary>Processing the hyperlink file </summary>
    /// <param name="hyperlink file" type="String">Hyperlinks containing the country and hyperlinks</param>
    /// <returns type="Map of cntry as key and hyperlink as value" />
    $scope.processHyperlinks = function (allText) {
        // split content based on new line
        var allTextLines = allText.data.split(/\r\n|\n/);
        var headers = allTextLines[0].split(',');

        for (var i = 0; i < allTextLines.length; i++) {
            // split content based on comma
            var data = allTextLines[i].split(',');

            for (var j = 0; j < data.length; j++) {
                $scope.hyperlinksMap[data[j]] = data[j + 1];
            }
        }


    };

    $scope.readFileForDemographics();
//**********************************************************************************************************************
    /// <summary>Function to handle cell click of the UI Grid</summary>
    /// <param name="event" type="String">Event Data</param>
    $scope.handleGridClick = function (evt) {

        var targetElem = angular.element(evt.target);
        if ((targetElem[0].innerHTML != "") && (evt.target.tabIndex > -1)) {
            $scope.openPDFLink(targetElem[0].innerHTML);
        }
    }

    $scope.openPDFLink = function (country_current) {
        //console.log(country_current);
        var _hyperlink = '';
        //country_current = entity.Country;
        _hyperlink = $scope.hyperlinksMap[country_current]
        //alert(_hyperlink);
        if (!(angular.isUndefined(_hyperlink)))
            $window.open(_hyperlink);
    }
//*************************************************************************************************************************
    /// <summary>Loading the notes file and displaying the contents as note in toastr</summary>
    /// <param name="_content" type="String">Notes file containing the tanlename and corresponding notes to be displayed</param>
    $scope.loadNotes = function (_content) {
        toastr.info(_content, {
            closeButton: true,
            position: 'toast-bottom-full-width',
            tapToDismiss: false,
            timeOut: 0
        });
    };

    $scope.readNotesCSV = function () {
        // http get request to read CSV file content
        $http.get('Notes.csv').then($scope.processData);
    };

    $scope.processData = function (allText) {
        // split content based on new line
        var allTextLines = allText.data.split(/\r\n|\n/);
        var headers = allTextLines[0].split(',');
        var lines = [];

        for (var i = 0; i < allTextLines.length; i++) {
            // split content based on comma
            var data = allTextLines[i].split(',');
            //if (data.length == headers.length) {
            var tarr = [];
            var _data, _table;
            for (var j = 0; j < data.length; j++) {
                if (j < 1) {
                    _table = data[j];
                    _table = _table.replace('"', '');
                    _table = _table.replace('"', '');
                    lines.push(_table);
                    _data = data[j + 1]
                    _data = _data.replace('"', '');
                    _data = _data.replace('"', '');
                }
                else
                    if (j < 2) {
                        _data = data[j];
                        _data = _data.replace('"', '');
                        _data = _data.replace('"', '');
                    }
                    else {
                        _data = _data + data[j];
                        _data = _data.replace('"', '');
                        _data = _data.replace('"', '');
                    }
            }

            lines.push(_data);
            $scope.mapNotes.set(_table, _data);
            //mapNotes[_table] = _data;
            //}
        }
        $scope.data = lines;
        //console.log($scope.mapNotes);
    };

    $scope.readNotesCSV();

//*************************************************************************************************************************
    /// <summary>Getting the Product Line Data from filters table from database and populating the productline drop down</summary>
    $scope.loadProductline = function () {

        //Check for first page load for custom page load
        $scope.firstpageload = true;

        var config = {
            headers: {
                'Content-Type': 'application/json',
            }
        }
        $scope.productlines = [];
        $http.get('' + apiUrl + 'GetProductline', config)
        .then(function (response, status, headers, config) {
            $scope.productlines.push("GlobalDataCoverage");
            for (var i = 0; i < response.data.length; i++) {
                if (response.data[i].product_line != "GlobalDataCoverage")
                    $scope.productlines.push(response.data[i].product_line);
            }

            var newleftPaneltop = $('#hdr').height();
            console.log(newleftPaneltop);
            $('.leftPanel').css('top', newleftPaneltop + 'px');

            var newPaneltop = $('#hdr').height();
            $('.btnPanel').css('top', newPaneltop);
            //console.log($scope.countries);
        },function (data, status, header, config) {
            alert('Unable to get Productlines !!!');
        });
    };

    $scope.loadProductline();
//**************************************************************************************************************************
    /// <summary>Getting the Products Data from filters table from database and populating the products drop down</summary>
    /// <param name="_product" type="String">getting products for selected productline</param>
    $scope.loadProducts = function (item) {

        if ($scope.Filters.productlineselected != "")
            $scope.firstpageload = true;
        else
            $scope.firstpageload = false;

        if ($scope.productlineselected != item)
            $scope.productlineclickedflag = false;

        $scope.selproductline = item;
        $scope.productlineselected = item;

        //clearing up selected products background color
        $scope.Filters.selproduct = '';
        $scope.Filters.selstats = '';
        $scope.Filters.selsocialplaces = '';

        //Setting up service object
        $scope.Filters.productlineselected = item;
        $scope.Filters.selproductline = item;

        $scope.products = [];
        $scope.socialplaces = [];
        $scope.productStats = [];

        $scope.coverageMap = {};


        //Handling click event to toggel productline
        if ($scope.productlineclickedflag) {
            $scope.productlineclickedflag = false;
        } else {
            $scope.productlineclickedflag = true;   
           
            var config = {
                headers: {
                    'Content-Type': 'application/json',
                }
            }
            //$scope.products = [];
            $http.get('' + apiUrl + 'GetProducts/' + item, config)
                .then(function (response, status, headers, config) {
                    for (var i = 0; i < response.data.length; i++) {
                        $scope.products.push(response.data[i].products);
                    }

                },function (data, status, header, config) {
                    alert('Unable to get Products !!!');
                });
        }
        
    };

    //Reading the Service object exposed and checking if it is case of return from some other page
    if ($scope.Filters.productlineselected != "")
        $scope.loadProducts($scope.Filters.productlineselected);


//************************************************************************************************************************
    /// <summary>Getting the tables/first_filter from filters table from database and populating the stats drop down</summary>
    /// <param name="_product" type="String">getting stats for selected products</param>
    $scope.loadFirstFilter = function (_product) {
        var config = {
            headers: {
                'Content-Type': 'application/json',
            }
        }

        $http.get('' + apiUrl + 'GetFirstFilter/' + _product, config)
        .then(function (response, status, headers, config) {
            for (var i = 0; i < response.data.length; i++) {

                $scope.productStats.push(response.data[i].filter2display);
                $scope.coverageMap[response.data[i].filter2display] = response.data[i].coveragemap;

                if (_product == 'GlobalDataScorecard') {
                    $scope.productStats.push('World Premium POI');
                    $scope.productStats.push('World POI');
                    $scope.productStats.push('World Boundaries Premium');
                    $scope.productStats.push('Postal And Admin Boundaries');
                    $scope.productStats.push('StreetPro Classic');
                    $scope.productStats.push('StreetPro Display');
                    $scope.productStats.push('StreetPro Navigation');
                    $scope.productStats.push('StreetPro Wrld');
                    $scope.productStats.push('Routing J Server Data');
                    $scope.productStats.push('Enterprise Routing Module');
                    $scope.productStats.push('Route Finder Data');
                    $scope.productStats.push('Address Doctor');
                    $scope.productStats.push('Loqate');
                    $scope.productStats.push('Address Now');
                    $scope.productStats.push('ICP');
                    $scope.productStats.push('Cameo');
                    $scope.productStats.push('Detail Demographics');
                    $scope.productStats.push('Base Demographics');
                    $scope.productStats.push('Property Attribute Data');
                    $scope.productStats.push('Addressing Enrichment Data');
                    
                    
                    $scope.coverageMap['World Premium POI'] = 'yes';
                    $scope.coverageMap['World POI'] = 'yes';
                    $scope.coverageMap['World Boundaries Premium'] = 'yes';
                    $scope.coverageMap['Postal And Admin Boundaries'] = 'yes';
                    $scope.coverageMap['StreetPro Classic'] = 'yes';
                    $scope.coverageMap['StreetPro Display'] = 'yes';
                    $scope.coverageMap['StreetPro Navigation'] = 'yes';
                    $scope.coverageMap['StreetPro Wrld'] = 'yes';
                    $scope.coverageMap['Routing J Server Data'] = 'yes';
                    $scope.coverageMap['Enterprise Routing Module'] = 'yes';
                    $scope.coverageMap['Route Finder Data'] = 'yes';
                    $scope.coverageMap['Address Doctor'] = 'yes';
                    $scope.coverageMap['Loqate'] = 'yes';
                    $scope.coverageMap['Address Now'] = 'yes';
                    $scope.coverageMap['ICP'] = 'yes';
                    $scope.coverageMap['Cameo'] = 'yes';
                    $scope.coverageMap['Detail Demographics'] = 'yes';
                    $scope.coverageMap['Base Demographics'] = 'yes';
                    $scope.coverageMap['Property Attribute Data'] = 'yes';
                    $scope.coverageMap['Addressing Enrichment Data'] = 'yes';
                }
            }
            //console.log($scope.productStats);
        },function (data, status, header, config) {
            alert('Unable to get First Filter Data !!!');
        });
    };
//*************************************************************************************************************************
    /// <summary>Setting up the second filter for specefic product selected</summary>
    /// <param name="selectedproduct" type="String">setting second filter for selected products</param>
    $scope.loadFilters = function (item) {
        if ($scope.productselected != item)
            $scope.productclickedflag = false;

        $scope.selproduct = item;
        $scope.productselected = item;
        $scope.Filters.productselected = item;
        $scope.Filters.selproduct = item;

        //clearing up selected stats table background color
        $scope.Filters.selstats = '';
        $scope.socialplaces = [];
        $scope.productStats = [];
        $scope.coverageMap = {};
        $scope.socialplaces = [];
        $scope.fipscodes = [];
        $scope.statestd = true;

        //var selectedproduct = $scope.productselected;
        //productname = selectedproduct;
        if ($scope.productclickedflag) {
            $scope.productclickedflag = false;
        } else {
            $scope.productclickedflag = true;        

            if ((item.toLowerCase() == 'social places') || (item.toLowerCase() == 'propertyattributefabric') || (item.toLowerCase() == 'propertyattributegem') || (item.toLowerCase() == 'propertyattributeparcelclassic')
                || (item.toLowerCase() == 'propertyattributeparcelplus') || (item.toLowerCase() == 'propertyattributeparcelpremium')) 
            {
                $scope.loadSecondFilterData(item);
               
            }
            else {
                $scope.socialplacestd = true;
                $scope.statestd = true;
                $scope.loadFirstFilter(item);
            }
        }
    };

    //Reading the Service object exposed and checking if it is case of return from some other page
    if ($scope.Filters.productlineselected != "")
        $scope.loadFilters($scope.Filters.productselected);

//***************************************************************************************************************************
    /// <summary>Getting the tables/second_filter from filters table from database and populating the second filter drop down</summary>
    /// <param name="_product" type="String">getting second filetr for selected products</param>
    $scope.loadSecondFilterData = function (item) {

        if ($scope.firstfilterselected != item)
            $scope.firstfilterclickedflag = false;

        $scope.firstfilterselected = item;
        $scope.Filters.selfirstfilter = item;

        $scope.socialplacestd = false;
        $scope.Filters.selsocialplaces = '';
    

        $scope.productStats = [];
        $scope.coverageMap = {};

        //Handling click event to toggel social places
        if ($scope.firstfilterclickedflag) {
            $scope.firstfilterclickedflag = false;
        } else {
            $scope.firstfilterclickedflag = true;

            var config = {
                headers: {
                    'Content-Type': 'application/json',
                }
            }
           
            $http.get('' + apiUrl + 'GetSecondFilterData/' + item, config)
            .then(function (response, status, headers, config) {
                if ((item.toLowerCase() == 'propertyattributefabric') || (item.toLowerCase() == 'propertyattributegem') || (item.toLowerCase() == 'propertyattributeparcelclassic')
                    || (item.toLowerCase() == 'propertyattributeparcelplus') || (item.toLowerCase() == 'propertyattributeparcelpremium'))
                {
                    $scope.statestd = false;
                    $scope.socialplacestd = true;
                    $scope.socialplaces.push("USA");
                    for (var i = 1; i < response.data.length; i++) {
                        if (response.data[i].filter1 != "USA") {
                            $scope.socialplaces.push(response.data[i].filter1);
                        }
                    }
                }
                else {
                    $scope.statestd = true;
                    $scope.socialplacestd = false;
                    for (var i = 0; i < response.data.length; i++) {
                        if (response.data[i].filter1 != "USA") {
                            $scope.socialplaces.push(response.data[i].filter1);
                        }
                    }
                }

            },function (data, status, header, config) {
                alert('Unable to load Social Places !!!');
            });
        }
    };
//*******************************************************************************************************************
    /// <summary>Getting the second_filter data from database and populating the UI GRid</summary>
    /// <param name="_subproduct" type="String">getting second_filter data </param>
    $scope.loadTables = function (item, index) {

        if ($scope.selsocialplaces != item)
            $scope.secondfilterclickedflag = false;

        $scope.selsocialplaces = item;
        $scope.selectedstate = item;
        $scope.Filters.selsocialplaces = item;
        var _subproduct = item;
        $scope.socialplacesselected = _subproduct;

        $scope.productStats = [];
        $scope.coverageMap = {};

        //Handling click event to toggel social places
        if ($scope.secondfilterclickedflag) {
            $scope.secondfilterclickedflag = false;
        } else {
            $scope.secondfilterclickedflag = true;

            var config = {
                headers: {
                    'Content-Type': 'application/json',
                }
            }

            _product = $scope.productselected;
            $scope.productStats = [];
            $scope.fipscodes = [];

            //if ($scope.Filters.productselected.toLowerCase() == '')
            if (($scope.Filters.productselected.toLowerCase() == 'propertyattributefabric') || ($scope.Filters.productselected.toLowerCase() == 'propertyattributegem') || ($scope.Filters.productselected.toLowerCase() == 'propertyattributeparcelclassic')
                || ($scope.Filters.productselected.toLowerCase() == 'propertyattributeparcelplus') || ($scope.Filters.productselected.toLowerCase() == 'propertyattributeparcelpremium')) {
                    
                if (item.toLowerCase() != 'usa') {
                    $scope.controlfips = false;
                    $http.get('' + apiUrl + 'GetFips/' + _product + '/' + _subproduct, config)
                    .then(function (response, status, headers, config) {
                        $scope.fipscodes.push("All");
                        for (var i = 0; i < response.data.length; i++) {
                            if (response.data[i].fips != "All") {
                                $scope.fipscodes.push(response.data[i].fips);
                            }
                        }
                    }, function (data, status, header, config) {
                        alert('Unable to load Fips codes !!!');
                    });
                }
                else {
                    $scope.controlfips = true;
                    var config = {
                        headers: {
                            'Content-Type': 'application/json',
                        }
                    }
                    $scope.poistatsdata = [];
                    $http.get('' + apiUrl + 'GetAEDStats/' + _product + '/' + _subproduct + '/' + '""', config)
                    .success(function (data, status, headers, config) {
                        $scope.poistatsdata = data;

                        $scope.gridStatsOptions.data = $scope.poistatsdata;
                        $scope.gridStatsOptions.height = ($scope.poistatsdata.length * 2) + "px";
                        $scope.loadStatsData(_product, $scope);
                    })
                    .error(function (data, status, header, config) {
                        alert('Unable to fetch AED Usa stats data !!!');
                    });
                }
            }
            else {

                $http.get('' + apiUrl + 'GetSecondFilter/' + _product + '/' + _subproduct, config)
                .then(function (response, status, headers, config) {
                    for (var i = 0; i < response.data.length; i++) {
                        $scope.productStats.push(response.data[i].filter2);
                        $scope.coverageMap[response.data[i].filter2] = response.data[i].coveragemap;
                    }
                }, function (data, status, header, config) {
                    alert('Unable to load Social Places Stats Tables !!!');
                });
            }
        }
    };
//**************************************************************************************************************************
    /// <summary>Getting data from database for selected table and populating the UI Grid</summary>
    /// <param name="_tblname" type="String">Stats table</param>
    $scope.loadStats = function (index) {

        //clearing up selected products background color
        $scope.Filters.selstats = '';
        $scope.selstats = index;
        $scope.Filters.selstats = index;
        $scope.Filters.statselected = index;
        $scope.Filters.fipsselected = index;
        var _tblname = '';
        var productname = $scope.productselected;
        //if (index == 'parcel_data_coverage')
        if (index.length >= 3)
            _tblname = index;
        else
            _tblname = $scope.productStats[index];

        
        if ((_tblname == "ppoi_counts_by_category_country") || (_tblname == "ppoic_counts_by_category_country")) {
            //if (_tblname == "ppoi_counts_by_category_country") {
            $scope.countrytd = false;
            $scope.loadCountries();
        }
        else if ((productname.toLowerCase() == 'propertyattributeparcelplus') || (productname.toLowerCase() == 'propertyattributeparcelclassic') ||
            (productname.toLowerCase() == 'propertyattributeparcelpremium') || (productname.toLowerCase() == 'propertyattributefabric') || (productname.toLowerCase() == 'propertyattributegem'))
        {
            var fips = _tblname;
            var state = $scope.socialplacesselected;

            var config = {
                headers: {
                    'Content-Type': 'application/json',
                }
            }
            $scope.poistatsdata = [];
            $http.get('' + apiUrl + 'GetAEDStats/' + _product + '/' + state + '/' + fips, config)
            .success(function (data, status, headers, config) {
                if (data.length > 0) {
                    $scope.alertMessage = true;
                    $scope.poistatsdata = data;

                    $scope.gridStatsOptions.data = $scope.poistatsdata;
                    $scope.gridStatsOptions.height = ($scope.poistatsdata.length * 2) + "px";
                    $scope.loadStatsData(_product, $scope);
                }
                else {
                    $scope.alertMessage = false;
                    $timeout(function () { $scope.alertMessage = true; }, 3000);
                }
            })
            .error(function (data, status, header, config) {
                alert('Unable to fetch AED State/Fips stats data !!!');
            });

        }
        else {
            $scope.countrytd = true;
            var config = {
                headers: {
                    'Content-Type': 'application/json',
                }
            }
            $scope.poistatsdata = [];
            var _currentproduct = $scope.productselected;
            $http.get('' + apiUrl + 'GetPoiStats/' + _currentproduct+"/"+_tblname, config)
            .then(function (response, status, headers, config) {
                if (response.data.length > 0) {
                    $scope.alertMessage = true;
                    $scope.poistatsdata = response.data;
                    $scope.gridStatsOptions.data = $scope.poistatsdata;

                    var newHeight = $('body').height() - 55 + 'px';
                    $('#gridStats').css('height', newHeight);
                    var newPaneltop = $('#hdr').height();
                    $('#btnPanel').css('top', newPaneltop);
                    var newleftPaneltop = $('#hdr').height();
                    $('#leftPanel').css('top', newleftPaneltop);

                    //calling of the dynamic UI Grid creation function
                    $scope.loadStatsData(_tblname, $scope);
                }
                else {
                    $scope.gridStatsOptions.data = $scope.poistatsdata;
                    $scope.alertMessage = false;
                    $timeout(function () { $scope.alertMessage = true; }, 3000);
                }
            },function (data, status, header, config) {
                alert('Unable to fetch poi stats data !!!');
            });
        }

    };

    //Reading the Service object exposed and checking if it is case of return from some other page
    if ($scope.Filters.statselected != "")
        $scope.loadStats($scope.Filters.statselected);
//************************************************************************************************************************
    /// <summary>Loading Countries for products having huge data so that we can show them countrywise</summary>
    $scope.loadCountries = function () {
        var config = {
            headers: {
                'Content-Type': 'application/json',
            }
        }
        $scope.countries = [];
        $http.get('' + apiUrl + 'GetCountries', config)
        .then(function (response, status, headers, config) {

            for (var i = 0; i < response.data.length; i++) {
                $scope.countries.push(response.data[i].Country);
            }

            //calling of the dynamic UI Grid creation function
            console.log($scope.countries);

        },function (data, status, header, config) {
            alert('Unable to fetch country data !!!');
        });
    };

    /// <summary>Loading countrywise data for some specefic products and populating the UI Grid</summary>
    /// <param name="_country" type="String">country for which data is to be fetched</param>
    $scope.loadCountryStats = function (_country) {

        var config = {
            headers: {
                'Content-Type': 'application/json',
            }
        }
        $scope.poicountrystatsdata = [];
        $http.get('' + apiUrl + 'GetCountryPoiStats/' + $scope.statselected + '/' + _country, config)
        .then(function (response, status, headers, config) {
            $scope.poistatsdata = response.data;
            $scope.gridStatsOptions.data = $scope.poistatsdata;

            var newHeight = document.body.offsetHeight - 30;
            //console.log(newHeight);
          
            angular.element(document.getElementsByClassName('grid')[0]).css('height', newHeight + 'px');
            //$scope.gridStatsOptions.height = ($scope.poistatsdata.length * 2) + "px";


            $scope.loadStatsData($scope.statselected, $scope);
        },function (data, status, header, config) {
            alert('Unable to fetch country poi stats data !!!');
        });

    };

//**************************************************************************************************************************
    /// <summary>Populating the UI grid dynamically </summary>
    /// <param name="_tablename" type="String">data for populating the UI Grid</param>
    /// <param name="scope" type="String">scope</param>
    $scope.loadStatsData = function (_tablename) {
        var arrData = [];
        var _content = '', propercasestr = '';
        var totalcellwidth = 0;

        //Routing to docs page
        
        if (_tablename == "description") {
            if ($scope.Filters.demogrphicsdesc == '') {
                $scope.Filters.demogrphicsdesc = 'description';
                $scope.Filters.demogrphicsfootnotes = '';
                $scope.Filters.demogrphicsdatasource = '';
                $scope.Filters.demogrphicsmethodology = '';
                $location.path('/docs');
            }
        }
        else if (_tablename == "footnotes") {
            if ($scope.Filters.demogrphicsfootnotes == '') {
                $scope.Filters.demogrphicsfootnotes = 'footnotes';
                $scope.Filters.demogrphicsdesc = '';
                $scope.Filters.demogrphicsdatasource = '';
                $scope.Filters.demogrphicsmethodology = '';
                $location.path('/docs');
            }
        }
        else if (_tablename == "datasource_details") {
            if ($scope.Filters.demogrphicsdatasource == '') {
                $scope.Filters.demogrphicsfootnotes = '';
                $scope.Filters.demogrphicsdesc = '';
                $scope.Filters.demogrphicsdatasource = 'datasource';
                $scope.Filters.demogrphicsmethodology = '';
                $location.path('/docs');
            }
        }
        else if (_tablename == "data_methodology") {
            if ($scope.Filters.demogrphicsmethodology == '') {
                $scope.Filters.demogrphicsfootnotes = '';
                $scope.Filters.demogrphicsdesc = '';
                $scope.Filters.demogrphicsdatasource = '';
                $scope.Filters.demogrphicsmethodology = 'methodolgy';
                $location.path('/docs');
            }
        }
            //else if (_tablename == "footnotes") {
            //    $location.path('/docs');
            //}
        else {
            var arrData = Object.getOwnPropertyNames($scope.poistatsdata[0]);
            var griddata = '', colnamewidth = '', coldatawidth = '', currcolwidth = '';
            var j = 0;

            $scope.gridStatsOptions.columnDefs = new Array();

            for (var i = 0; i < arrData.length; i++) {
                var _cellTempelate = '', _cellwidth = '';
                if (arrData[i].toLocaleLowerCase() == 'cameo table link') {
                    _cellTempelate = '<div style="cursor: pointer"><a ng-click = "$scope.gridApi.handleGridClick()">{{COL_FIELD}}</a></div>';
                }

                //$scope.colnamewidth = arrData[i + 1];

                propercasestr = $scope.toTitleCase(arrData[i]);
                propercasestr = propercasestr.replace("'", '');
                propercasestr = propercasestr.replace("'", '');
                //_cellwidth = propercasestr.length + 150;

                for (j ; j < arrData.length ; j++) {
                    if ($scope.poistatsdata[0][arrData[j]] != '') {
                        coldatawidth = $scope.poistatsdata[0][arrData[j]].length;
                    }
                    j++;
                    break;
                }
                //console.log(coldatawidth);

                //Handling horizontal scroll bar issue with data more than 75 columns
                if (arrData.length < 75) {
                    currcolwidth = (100 / arrData.length);
                    _cellwidth = currcolwidth + coldatawidth + '%';
                }
                else {
                    _cellwidth = propercasestr.length + 150;
                }

                //console.log(_cellwidth);

                $scope.gridStatsOptions.columnDefs.push({
                    field: arrData[i],
                    displayName: propercasestr,
                    width: _cellwidth,
                    maxWidth: '600',
                    cellTemplate: _cellTempelate
                });

            }


            //For collapsed view of productmaster on first page load
            if ($scope.Filters.pageload == '') {
                $scope.products = [];
                $scope.Filters.selproduct = '';
                $scope.Filters.selsocialplaces = '';
                $scope.Filters.selfirstfilter = '';
                $scope.Filters.selstats = '';
                $scope.Filters.pageload = 'loaded';
            }


            //Loading Notes
            //var _keyNotes = arrData[i].toLowerCase();
            //_keyNotes = _keyNotes.substr(1, _keyNotes.length - 2);
            var _content = $scope.mapNotes.get(_tablename);
            if (!(angular.isUndefined(_content)))
                $scope.loadNotes(_content);
        }



    };
    //********************************************************************************************************************************
    /// <summary>Converting string to Title case</summary>
    /// <param name="string" type="String">String to be formatted</param>
    $scope.toTitleCase = function (string) {
        // \u00C0-\u00ff for a happy Latin-1
        return string.toLowerCase().replace(/_/g, ' ').replace(/\b([a-z\u00C0-\u00ff])/g, function (_, initial) {
            return initial.toUpperCase();
        }).replace(/(\s(?:de|a|o|e|da|do|em|ou|[\u00C0-\u00ff]))\b/ig, function (_, match) {
            return match.toLowerCase();
        });
    }

//*************************************************************************************************************************
    /// <summary>Setting routes</summary>


    $scope.getCoverageMap = function () {
        $location.path('/map');
    };


});
//Map Controller
app.controller('mapController', function ($scope, $http, $location, $route, uiGridExporterService, uiGridExporterConstants, $timeout, cfpLoadingBar, uiGridConstants, $window, SharedFilterService) {
    
    var productname;
    var apiUrl = 'http://152.144.227.176:8080/ProductDevWS/jaxrs/WebService/'

    $scope.Filters = SharedFilterService;
    $scope.alertMessage = true;
    $scope.infoMessage = true;
    //$scope.Filters = '';

    var map, mapZoom;
    var latCenter = 53.42135;
    var longCenter = -1.28498;
    var color = "#000000";
    var mapdata = [];
    var countyLayer = new google.maps.Data();
    var countryLayer = new google.maps.Data();
    var stateLayer = new google.maps.Data();
    var addedLayers = [];
    var infoWindow = new google.maps.InfoWindow({
        content: ""
    });

    var socialplacestd = true;
    var countrytd = true;
    var countryflag = false, countyflag = false,usastateflag = false;

    $scope.productlines = [];
    $scope.products = [];
    $scope.poistatsdata = [];
    $scope.hide = false;
    $scope.coverageMap = {};
    $scope.countryListMap = {};
    $scope.usastateMap = {};
    $scope.jsondataMap = {};
    $scope.productstateMap = {};

    $scope.productlineclickedflag = false;
    $scope.productclickedflag = false;
    $scope.firstfilterclickedflag = false;
    $scope.secondfilterclickedflag = false;



//Setting Routes according  to the icon clicked
    $scope.loadGrid = function () {
        $location.path('/stats');
    }

    $scope.loadMap = function () {
        $location.path('/map');
    }

    $scope.toggle = function () {
        //console.log($('.btnClose'));
        if ($scope.hide) {
            $scope.hide = false;
            $('.btnPanel').css('left', '23%');
            $('.rightPanelStats').css('left', '25%');
            $('.rightPanelStats').css('width', '75%');
        }
        else {
            $scope.hide = true;
            $('.btnPanel').css('left', '0px');
            $('.rightPanelStats').css('left', '0px');
            $('.rightPanelStats').css('width', '100%');
        }

        if ($('#slidericon').hasClass('nc-icon-outline ui-3_slide-right'))
            $("#slidericon").removeClass('nc-icon-outline ui-3_slide-right').addClass('nc-icon-outline ui-3_slide-left');
        else if ($('#slidericon').hasClass('nc-icon-outline ui-3_slide-left'))
            $("#slidericon").removeClass('nc-icon-outline ui-3_slide-left').addClass('nc-icon-outline ui-3_slide-right');


        if ($scope.hide == true) {
            $("#divleftpanel").removeClass('pb-ds-fade-in-left').addClass('pb-ds-fade-out-left');
        }
        else {
            $("#divleftpanel").removeClass('pb-ds-fade-out-left').addClass('pb-ds-fade-in-left');
        }

        //var newHeight = $('body').height();
        //var newWidth = $('body').width();
        //$('#map').css('height', newHeight);
        //$('#map').css('width', newWidth);
    }
//*************************************************************************************************************
//Setting up the left panel to hide so that we can have the full view of map
    $scope.hide = true;
    $('.btnPanel').css('left', '0px');
    $('.rightPanelStats').css('left', '0px');
    $('.rightPanelStats').css('width', '100%');

  //Setting up map with initial properties
    $scope.map = new google.maps.Map(document.getElementById('map'), {
        zoom: 3,
        center: new google.maps.LatLng(latCenter, longCenter),
        fullscreenControl: false,
        mapTypeControl: true,
        mapTypeControlOptions: {
            style: google.maps.MapTypeControlStyle.HORIZONTAL_BAR,
            position: google.maps.ControlPosition.TOP_RIGHT
        },
        mapTypeId: google.maps.MapTypeId.ROADMAP
    });

    //Restricting the zoom levels for the user
    var opt = { minZoom: 1, maxZoom: 16 };
    $scope.map.setOptions(opt);

    var newHeight = $('body').height() - 41 + 'px';
    //var newHeight = $('body').height() ;
    $('#map').css('height', newHeight);
    //$('#map').css('width', newWidth);

    //Setting up icons for Map Legend and creation of custum legend for Map
    var icons = {
        available: {
            name: 'Available',
            icon: 'images/green.png'
        },
        notavailable: {
            name: 'Not Available',
            icon: 'images/red.png'
        }
    };


    //Creating Custom Control Legend
    var legend = document.getElementById('legend');
    for (var key in icons) {
        var type = icons[key];
        var name = type.name;
        var icon = type.icon;
       
        var controlUI = document.createElement('div');
        controlUI.style.backgroundColor = '#fff';
        controlUI.style.border = '2px solid #fff';
        controlUI.style.borderRadius = '3px';
        controlUI.style.boxShadow = '0 2px 7px rgba(0,0,0,.3)';
        controlUI.style.marginBottom = '10px';
        controlUI.style.textAlign = 'left';
        //controlUI.title = 'Click to open up Legend';
        legend.appendChild(controlUI);

        // Set CSS for the control interior.
        var controlText = document.createElement('div');
        controlText.style.color = 'rgb(25,25,25)';
        controlText.style.fontFamily = 'Roboto,Arial,sans-serif';
        controlText.style.fontSize = '12px';
        controlText.style.lineHeight = '22px';
        controlText.style.paddingLeft = '5px';
        controlText.style.paddingRight = '5px';
        controlText.innerHTML = '<img src="' + icon + '"> ' + name;
        //controlText.innerHTML = 'Legend';
        controlUI.appendChild(controlText);

        
        //legend.appendChild(div);
    }
    legend.index = 1;
    //$scope.map.controls[google.maps.ControlPosition.TOP_CENTER].push(legend);
    $('#legend').css('top', '34px');
    $('#legend').css('position', 'absolute');
    $('#legend').css('right', '0%');


//*************************************************************************************************************************************
//*************************************************************************************************************************
    /// <summary>Loading the notes file and displaying the contents as note in toastr</summary>
    /// <param name="_content" type="String">Notes file containing the tanlename and corresponding notes to be displayed</param>

    $scope.loadUSAStateMappings = function () {
        // http get request to read CSV file content
        $http.get('usastate_mappings.csv').then($scope.processCSV);
    };

    $scope.processCSV = function (content) {

        $scope.usastateMap = {};
        var text = content.data.split(/\r\n|\n/);

        for (var i = 0; i < text.length; i++) {
            var data = text[i].split(',');
            $scope.usastateMap[data[0]] = data[1];
        }

    };

    $scope.loadUSAStateMappings();
//******************************************************************************************************************
    /// <summary>Getting the country list of geojson file and different products</summary>
    $scope.getCountryList = function ()
    {
        var config = {
            headers: {
                'Content-Type': 'application/json',
            }
        }
        //$scope.productlines = [];
        
        $http.get('' + apiUrl + 'GetCountryList', config)
        .then(function (response, status, headers, config) {
            for (var i = 0; i < response.data.length; i++) {
                $scope.countryListMap[response.data[i].key] = response.data[i].value;
              }
        },function (data, status, header, config) {
            alert('Unable to get Productlines !!!');
        });
    }

    $scope.getCountryList();
//****************************************************************************************************************
 /// <summary>Rendering Country Data on map along with attribute data as main component for the thematic/coverage</summary>
    $scope.loadCountryMapData = function () {
        var wrldlong = -3.74922, wrldlat = 40.46366, countryname='';
        $scope.clearMap();

        //$http.get("world.countries.geo.json")
        $http.get("world_countries.geojson")
           .then(function (response) {
               countryLayer = new google.maps.Data();
               countryLayer.addGeoJson(response.data);
               countryLayer.setStyle({});
               countryLayer.setStyle(function (feature) {

                   if (($scope.Filters.productselected.toLowerCase() == 'premium poi') || ($scope.Filters.productselected.toLowerCase() == 'premium poi csmr')) {
                       countryname = feature.getProperty('iso_a3');
                   }
                   else {
                       countryname = feature.getProperty('admin');
                   }

                   
                   color = $scope.geographychk(countryname);

                   if (color == null)
                       color = 'red';
                   return {
                       fillColor: color,
                       strokeWeight: .5
                   }
               });

               countryLayer.setMap($scope.map);

               countryLayer.addListener('click', function (event) {
                   //show an infowindow on click   
                   infoWindow.setContent('<div style="line-height:1.35;overflow:hidden;white-space:nowrap;">' +
                       'Country  : ' + event.feature.getProperty("admin") + "</div>");
                   var anchor = new google.maps.MVCObject();
                   anchor.set("position", event.latLng);
                   infoWindow.open($scope.map, anchor);
               });

           });

        $scope.map.setCenter(new google.maps.LatLng(wrldlat, wrldlong))
        $scope.map.setZoom(3);
    }

    //************************************************************************************************************************
    /// <summary>Rendering County Data on map along with attribute data as main component for the thematic/coverage</summary>
    $scope.loadCountyMapData = function () {
        $scope.clearMap();
        $http.get("usa_counties.geo.json")
           .then(function (res) {
               countyLayer = new google.maps.Data();
               countyLayer.addGeoJson(res.data);
               countyLayer.setStyle({});
               countyLayer.setStyle(function (feature) {
                   var countyname = feature.getProperty('NAME');

                   //calling the countrychk function for coverage information
                   $scope.color = $scope.geographychk(countyname);
                   if ($scope.color == null)
                       $scope.color = 'red';

                   return {
                       fillColor: $scope.color,
                       strokeWeight: .5
                   }
               });
               countyLayer.setMap($scope.map);

               //Adding event listmer to the polygon created for Info window
               countyLayer.addListener('mouseover', function (event) {
                   //show an infowindow on click   
                   infoWindow.setContent('<div style="line-height:1.35;overflow:hidden;white-space:nowrap;">' +
                       'County  = ' + event.feature.getProperty("NAME") + "</div>");
                   var anchor = new google.maps.MVCObject();
                   anchor.set("position", event.latLng);
                   infoWindow.open($scope.map, anchor);
               });
           });

        $scope.map.setCenter(new google.maps.LatLng(39.0119, -98.4842))
        $scope.map.setZoom(4);
    }
    //***************************************************************************************************************************
    /// <summary>Rendering State Data on map along with attribute data as main component for the thematic/coverage</summary>
    $scope.loadStateMapData = function () {
        var productstate = ''; state = '',statename ='';
        $scope.clearMap();

        $http.get("usa_states.geojson")
           .then(function (res) {
               $scope.jsondataMap = {};
               $scope.productstateMap = {};

               var result = res.data.features;
               var arrproductdata = $scope.mapdata;

               for (var i = 0; i < result.length; i++) {
                   $scope.jsondataMap[result[i].properties.LEVEL2] = result[i].properties.LEVEL2;
               }

               for (var i = 0; i < arrproductdata.length; i++) {
                   productstate = arrproductdata[i].toLowerCase();
                   productstate = productstate.toUpperCase().trim();
                   state = $scope.usastateMap[productstate];
                   $scope.productstateMap[state] = state;
               }

               stateLayer = new google.maps.Data();
               stateLayer.addGeoJson(res.data);
               stateLayer.setStyle({});
               stateLayer.setStyle(function (feature) {
                   statename = feature.getProperty('LEVEL2');

                   if ($scope.productstateMap[statename] == null)
                       $scope.color = 'red';
                   else
                       $scope.color = 'green';

                    return {
                        fillColor: $scope.color,
                        strokeWeight: .5
                    }


               });
               stateLayer.setMap($scope.map);


               //Adding event listmer to the polygon created for Info window
               stateLayer.addListener('click', function (event) {
                   //show an infowindow on click   
                   //if ($scope.Filters.selstats.toLowerCase() == 'demographics_statewise') {
                       infoWindow.setContent('<div style="line-height:1.35;overflow:hidden;white-space:nowrap;">' +
                          'State  = ' + event.feature.getProperty("LEVEL2") + "</div>");
                   //}

                   var anchor = new google.maps.MVCObject();
                   anchor.set("position", event.latLng);
                   infoWindow.open($scope.map, anchor);
               });
           });

        $scope.map.setCenter(new google.maps.LatLng(39.0119, -98.4842))
        $scope.map.setZoom(4);
    }
    //***************************************************************************************************************************
    $scope.stateCheck = function(_jsonstate)
    {
        var arrproductdata = $scope.mapdata;
        var productstate = '', state = '';


        for (var i = 0; i < arrproductdata.length; i++) {

            productstate = arrproductdata[i].toLowerCase();
            productstate = productstate.toUpperCase().trim();
            state = $scope.usastateMap[productstate];
        
            //console.log("State : " + productstate + " ISO: " + state);
            if (state != null) {
                if (state.toLowerCase() == _jsonstate.toLowerCase()) {
                    color = 'green';
                    return color;
                    break;
                }
            }
        }
    }

    //***************************************************************************************************************************
    /// <summary>Checks for the country name in the stats table country/county field and returns the color accordingly</summary>
    /// <param name="_jsoncntry" type="String">country/county name</param>
    /// <returns type="String" />
    $scope.geographychk = function (_jsoncntry) {

        var arrproductdata = $scope.mapdata;
        var productcntry = '', cntrylstvalue,currprdtcntry = '';
        var arrcntrylstvalue = [];

        for (var i = 0; i < arrproductdata.length; i++) {

            productcntry = arrproductdata[i].toLowerCase();

            if (($scope.Filters.productselected.toLowerCase() == 'premium poi') || ($scope.Filters.productselected.toLowerCase() == 'premium poi csmr')) {
                if (productcntry.toLowerCase() == 'usa') 
                    productcntry = 'USA'
            }
            else {
                if ((productcntry.toLowerCase() == 'usa') || (productcntry.toLowerCase() == 'united states')) {
                    productcntry = 'united states of america'
                }
            }

            if ((productcntry.toLowerCase() == 'democratic republic of congo') || (productcntry.toLowerCase() == 'democratic republic of the congo') || (productcntry.toLowerCase() == 'congo, the democratic republic of the')) {
                productcntry = 'democratic republic of the congo';
            }
            else if (productcntry.toLowerCase() == 'congo') {
                productcntry = 'republic of congo';
            }
            else if (productcntry.toLowerCase() == 'viet nam') {
                productcntry = 'vietnam';
            }
            else if (productcntry.toLowerCase() == 'russian federation') {
                productcntry = 'russia';
            }
            else if (productcntry.toLowerCase() == 'iran, islamic republic of') {
                productcntry = 'iran';
            }
            else if ((productcntry.toLowerCase() == 'tanzania') || (productcntry.toLowerCase() == 'tanzania, united republic of')) {
                productcntry = 'united republic of tanzania';
            }
            else if ((productcntry.toLowerCase() == 'syrian arab republic') || (productcntry.toLowerCase() == 'republic of syria')) {
                productcntry = 'syria';
            }
            else if ((productcntry.toLowerCase() == 'serbia')) {
                productcntry = 'republic of serbia';
            }
            else if ((productcntry.toLowerCase() == 'cote d\'ivoire')) {
                productcntry = 'ivory coast';
            }
            else if ((productcntry.toLowerCase() == 'bosnia & herzegovina')) {
                productcntry = 'bosnia and herzegovina';
            }

            
            if (productcntry.toLowerCase() == _jsoncntry.toLowerCase()) {
                    color = 'green';
                    return color;
                    break;
            }
            else {
                //console.log(productcntry.toLowerCase() + "::" + _jsoncntry.toLowerCase())
            }
            
        }
    };
//********************************************************************************************************************
    /// <summary>Clears all the layers from the Map</summary>
    $scope.clearMap = function()
    {
        countryLayer.forEach(function (feature) {
            countryLayer.remove(feature);
            countryLayer.setMap(null);
        });
        stateLayer.forEach(function (feature) {
            stateLayer.remove(feature);
            stateLayer.setMap(null);
        });
        countyLayer.forEach(function (feature) {
            countyLayer.remove(feature);
            countyLayer.setMap(null);
        });
        countryLayer = [];
        countyLayer = [];
        stateLayer = [];
    }
//************************************************************************************************************************
    /// <summary>Sets up for County/Country to be loaded on the map</summary>
    $scope.loadMapData = function () {
        if ($scope.countyflag) {
            $scope.infoMessage = true;
            $scope.alertMessage = true;
            $scope.loadCountyMapData();
        }
        else if ($scope.usastateflag) {
            $scope.infoMessage = true;
            $scope.alertMessage = true;
            $scope.loadStateMapData();
        }
        else if ($scope.countryflag) {
            $scope.infoMessage = true;
            $scope.alertMessage = true;
            $scope.loadCountryMapData();
        }
        else {
            $scope.infoMessage = false;
            $scope.alertMessage = true;
            $timeout(function () { $scope.infoMessage = true; }, 3000);
        }

    }
//***********************************************************************************************************************
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
   /// <summary>Getting the Product Line Data from filters table from database and populating the productline drop down</summary>
    $scope.loadProductline = function () {
        var config = {
            headers: {
                'Content-Type': 'application/json',
            }
        }
        $scope.productlines = [];
        $http.get('' + apiUrl + 'GetProductline', config)
        .then(function (response, status, headers, config) {
            $scope.productlines.push("GlobalDataCoverage");
            for (var i = 0; i < response.data.length; i++) {
                if (response.data[i].product_line != "GlobalDataCoverage")
                    $scope.productlines.push(response.data[i].product_line);
            }

            var newleftPaneltop = $('#hdr').height();
            console.log(newleftPaneltop);
            $('.leftPanel').css('top', newleftPaneltop + 'px');

            var newPaneltop = $('#hdr').height();
            $('.btnPanel').css('top', newPaneltop);
            //console.log($scope.countries);
        }, function (data, status, header, config) {
            alert('Unable to get Productlines !!!');
        });
    };

    $scope.loadProductline();
//**************************************************************************************************************************
    /// <summary>Getting the Products Data from filters table from database and populating the products drop down</summary>
    /// <param name="_product" type="String">getting products for selected productline</param>      
        $scope.loadProducts = function (item) {

            if ($scope.productlineselected != item)
                $scope.productlineclickedflag = false;

            $scope.selproductline = item;
            $scope.productlineselected = item;

            //clearing up selected products background color
            $scope.Filters.selproduct = '';
            $scope.Filters.selstats = '';
            $scope.Filters.selsocialplaces = '';

            //Setting up service object
            $scope.Filters.productlineselected = item;
            $scope.Filters.selproductline = item;

            $scope.products = [];
            $scope.socialplaces = [];
            $scope.productStats = [];

            $scope.coverageMap = {};


            //Handling click event to toggel productline
            if ($scope.productlineclickedflag) {
                $scope.productlineclickedflag = false;
            } else {
                $scope.productlineclickedflag = true;

                var config = {
                    headers: {
                        'Content-Type': 'application/json',
                    }
                }
                //$scope.products = [];
                $http.get('' + apiUrl + 'GetProducts/' + item, config)
                    .then(function (response, status, headers, config) {
                        for (var i = 0; i < response.data.length; i++) {
                            $scope.products.push(response.data[i].products);
                        }

                    }, function (data, status, header, config) {
                        alert('Unable to get Products !!!');
                    });
            }

        };

        //Reading the Service object exposed and checking if it is case of return from some other page
        if ($scope.Filters.productlineselected != "")
            $scope.loadProducts($scope.Filters.productlineselected);


        //************************************************************************************************************************
        /// <summary>Getting the tables/first_filter from filters table from database and populating the stats drop down</summary>
        /// <param name="_product" type="String">getting stats for selected products</param>
        $scope.loadFirstFilter = function (_product) {
            var config = {
                headers: {
                    'Content-Type': 'application/json',
                }
            }

            $http.get('' + apiUrl + 'GetFirstFilter/' + _product, config)
            .then(function (response, status, headers, config) {
                for (var i = 0; i < response.data.length; i++) {

                    $scope.productStats.push(response.data[i].filter2display);
                    $scope.coverageMap[response.data[i].filter2display] = response.data[i].coveragemap;

                    if (_product == 'GlobalDataScorecard') {
                        $scope.productStats.push('World Premium POI');
                        $scope.productStats.push('World POI');
                        $scope.productStats.push('World Boundaries Premium');
                        $scope.productStats.push('Postal And Admin Boundaries');
                        $scope.productStats.push('StreetPro Classic');
                        $scope.productStats.push('StreetPro Display');
                        $scope.productStats.push('StreetPro Navigation');
                        $scope.productStats.push('StreetPro Wrld');
                        $scope.productStats.push('Routing J Server Data');
                        $scope.productStats.push('Enterprise Routing Module');
                        $scope.productStats.push('Route Finder Data');
                        $scope.productStats.push('Address Doctor');
                        $scope.productStats.push('Loqate');
                        $scope.productStats.push('Address Now');
                        $scope.productStats.push('ICP');
                        $scope.productStats.push('Cameo');
                        $scope.productStats.push('Detail Demographics');
                        $scope.productStats.push('Base Demographics');
                        $scope.productStats.push('Property Attribute Data');
                        $scope.productStats.push('Addressing Enrichment Data');


                        $scope.coverageMap['World Premium POI'] = 'yes';
                        $scope.coverageMap['World POI'] = 'yes';
                        $scope.coverageMap['World Boundaries Premium'] = 'yes';
                        $scope.coverageMap['Postal And Admin Boundaries'] = 'yes';
                        $scope.coverageMap['StreetPro Classic'] = 'yes';
                        $scope.coverageMap['StreetPro Display'] = 'yes';
                        $scope.coverageMap['StreetPro Navigation'] = 'yes';
                        $scope.coverageMap['StreetPro Wrld'] = 'yes';
                        $scope.coverageMap['Routing J Server Data'] = 'yes';
                        $scope.coverageMap['Enterprise Routing Module'] = 'yes';
                        $scope.coverageMap['Route Finder Data'] = 'yes';
                        $scope.coverageMap['Address Doctor'] = 'yes';
                        $scope.coverageMap['Loqate'] = 'yes';
                        $scope.coverageMap['Address Now'] = 'yes';
                        $scope.coverageMap['ICP'] = 'yes';
                        $scope.coverageMap['Cameo'] = 'yes';
                        $scope.coverageMap['Detail Demographics'] = 'yes';
                        $scope.coverageMap['Base Demographics'] = 'yes';
                        $scope.coverageMap['Property Attribute Data'] = 'yes';
                        $scope.coverageMap['Addressing Enrichment Data'] = 'yes';
                    }
                }
                console.log($scope.productStats);
            }, function (data, status, header, config) {
                alert('Unable to get First Filter Data !!!');
            });
        };
        //*************************************************************************************************************************
        /// <summary>Setting up the second filter for specefic product selected</summary>
        /// <param name="selectedproduct" type="String">setting second filter for selected products</param>
        $scope.loadFilters = function (item) {
            if ($scope.productselected != item)
                $scope.productclickedflag = false;

            $scope.selproduct = item;
            $scope.productselected = item;
            $scope.Filters.productselected = item;
            $scope.Filters.selproduct = item;

            //clearing up selected stats table background color
            $scope.Filters.selstats = '';
            $scope.socialplaces = [];
            $scope.productStats = [];
            $scope.coverageMap = {};
            $scope.socialplaces = [];
            $scope.fipscodes = [];
            $scope.statestd = true;

            //var selectedproduct = $scope.productselected;
            //productname = selectedproduct;
            if ($scope.productclickedflag) {
                $scope.productclickedflag = false;
            } else {
                $scope.productclickedflag = true;

                if ((item.toLowerCase() == 'social places') || (item.toLowerCase() == 'propertyattributefabric') || (item.toLowerCase() == 'propertyattributegem') || (item.toLowerCase() == 'propertyattributeparcelclassic')
                    || (item.toLowerCase() == 'propertyattributeparcelplus') || (item.toLowerCase() == 'propertyattributeparcelpremium')) {
                    $scope.loadSecondFilterData(item);

                }
                else {
                    $scope.socialplacestd = true;
                    $scope.statestd = true;
                    $scope.loadFirstFilter(item);
                }
            }
        };

        //Reading the Service object exposed and checking if it is case of return from some other page
        if ($scope.Filters.productlineselected != "")
            $scope.loadFilters($scope.Filters.productselected);

        //***************************************************************************************************************************
        /// <summary>Getting the tables/second_filter from filters table from database and populating the second filter drop down</summary>
        /// <param name="_product" type="String">getting second filetr for selected products</param>
        $scope.loadSecondFilterData = function (item) {

            if ($scope.firstfilterselected != item)
                $scope.firstfilterclickedflag = false;

            $scope.firstfilterselected = item;
            $scope.Filters.selfirstfilter = item;

            $scope.socialplacestd = false;
            $scope.Filters.selsocialplaces = '';


            $scope.productStats = [];
            $scope.coverageMap = {};

            //Handling click event to toggel social places
            if ($scope.firstfilterclickedflag) {
                $scope.firstfilterclickedflag = false;
            } else {
                $scope.firstfilterclickedflag = true;

                var config = {
                    headers: {
                        'Content-Type': 'application/json',
                    }
                }

                $http.get('' + apiUrl + 'GetSecondFilterData/' + item, config)
                .then(function (response, status, headers, config) {
                    if ((item.toLowerCase() == 'propertyattributefabric') || (item.toLowerCase() == 'propertyattributegem') || (item.toLowerCase() == 'propertyattributeparcelclassic')
                        || (item.toLowerCase() == 'propertyattributeparcelplus') || (item.toLowerCase() == 'propertyattributeparcelpremium')) {
                        $scope.statestd = false;
                        $scope.socialplacestd = true;
                        $scope.socialplaces.push("USA");
                        for (var i = 1; i < response.data.length; i++) {
                            if (response.data[i].filter1 != "USA") {
                                $scope.socialplaces.push(response.data[i].filter1);
                            }
                        }
                    }
                    else {
                        $scope.statestd = true;
                        $scope.socialplacestd = false;
                        for (var i = 0; i < response.data.length; i++) {
                            if (response.data[i].filter1 != "USA") {
                                $scope.socialplaces.push(response.data[i].filter1);
                            }
                        }
                    }

                }, function (data, status, header, config) {
                    alert('Unable to load Social Places !!!');
                });
            }
        };
        //*******************************************************************************************************************
        /// <summary>Getting the second_filter data from database and populating the UI GRid</summary>
        /// <param name="_subproduct" type="String">getting second_filter data </param>
        $scope.loadTables = function (item, index) {

            if ($scope.selsocialplaces != item)
                $scope.secondfilterclickedflag = false;

            $scope.selsocialplaces = item;
            $scope.selectedstate = item;
            $scope.Filters.selsocialplaces = item;
            var _subproduct = item;
            $scope.socialplacesselected = _subproduct;

            $scope.productStats = [];
            $scope.coverageMap = {};

            //Handling click event to toggel social places
            if ($scope.secondfilterclickedflag) {
                $scope.secondfilterclickedflag = false;
            } else {
                $scope.secondfilterclickedflag = true;

                var config = {
                    headers: {
                        'Content-Type': 'application/json',
                    }
                }

                _product = $scope.productselected;
                $scope.productStats = [];
                $scope.fipscodes = [];

                //if ($scope.Filters.productselected.toLowerCase() == '')
                if (($scope.Filters.productselected.toLowerCase() == 'propertyattributefabric') || ($scope.Filters.productselected.toLowerCase() == 'propertyattributegem') || ($scope.Filters.productselected.toLowerCase() == 'propertyattributeparcelclassic')
                    || ($scope.Filters.productselected.toLowerCase() == 'propertyattributeparcelplus') || ($scope.Filters.productselected.toLowerCase() == 'propertyattributeparcelpremium')) {

                    if (item.toLowerCase() != 'usa') {
                        $scope.controlfips = false;
                        $http.get('' + apiUrl + 'GetFips/' + _product + '/' + _subproduct, config)
                        .then(function (response, status, headers, config) {
                            $scope.fipscodes.push("All");
                            for (var i = 0; i < response.data.length; i++) {
                                if (response.data[i].fips != "All") {
                                    $scope.fipscodes.push(response.data[i].fips);
                                }
                            }
                        }, function (data, status, header, config) {
                            alert('Unable to load Fips codes !!!');
                        });
                    }
                    else {
                        $scope.controlfips = true;
                        var config = {
                            headers: {
                                'Content-Type': 'application/json',
                            }
                        }
                        $scope.poistatsdata = [];
                        $http.get('' + apiUrl + 'GetAEDStats/' + _product + '/' + _subproduct + '/' + '""', config)
                        .success(function (data, status, headers, config) {
                            if (data.length > 0) {
                                $scope.alertMessage = true;
                                $scope.poistatsdata = data;

                                $scope.gridStatsOptions.data = $scope.poistatsdata;
                                $scope.gridStatsOptions.height = ($scope.poistatsdata.length * 2) + "px";
                                $scope.loadStatsData(_product, $scope);
                            }
                            else
                            {
                                $scope.alertMessage = false;
                                $scope.infoMessage = true;
                                $timeout(function () { $scope.alertMessage = false; }, 3000);
                            }
                        })
                        .error(function (data, status, header, config) {
                            alert('Unable to fetch AED Usa stats data !!!');
                        });
                    }
                }
                else {

                    $http.get('' + apiUrl + 'GetSecondFilter/' + _product + '/' + _subproduct, config)
                    .then(function (response, status, headers, config) {
                        for (var i = 0; i < response.data.length; i++) {
                            $scope.productStats.push(response.data[i].filter2);
                            $scope.coverageMap[response.data[i].filter2] = response.data[i].coveragemap;
                        }
                    }, function (data, status, header, config) {
                        alert('Unable to load Social Places Stats Tables !!!');
                    });
                }
            }
        };
        //**************************************************************************************************************************
        /// <summary>Getting data from database for selected table and populating the UI Grid</summary>
        /// <param name="_tblname" type="String">Stats table</param>
        $scope.loadStats = function (index) {
            //clearing up selected products background color
            $scope.Filters.selstats = '';
            $scope.selstats = index;
            $scope.Filters.selstats = index;
            $scope.Filters.statselected = index;
            $scope.Filters.fipsselected = index;
            var _tblname = '';
            var productname = $scope.productselected;
            //if (index == 'parcel_data_coverage')
            if (index.length >= 3)
                _tblname = index;
            else
                _tblname = $scope.productStats[index];

            //Routing to docs page

            if (index == "description") {
                if ($scope.Filters.demogrphicsdesc == '') {
                    $scope.Filters.demogrphicsdesc = 'description';
                    $scope.Filters.demogrphicsfootnotes = '';
                    $scope.Filters.demogrphicsdatasource = '';
                    $scope.Filters.demogrphicsmethodology = '';
                    $location.path('/docs');
                }
            }
            else if (index == "footnotes") {
                if ($scope.Filters.demogrphicsfootnotes == '') {
                    $scope.Filters.demogrphicsfootnotes = 'footnotes';
                    $scope.Filters.demogrphicsdesc = '';
                    $scope.Filters.demogrphicsdatasource = '';
                    $scope.Filters.demogrphicsmethodology = '';
                    $location.path('/docs');
                }
            }
            else if (index == "datasource_details") {
                if ($scope.Filters.demogrphicsdatasource == '') {
                    $scope.Filters.demogrphicsfootnotes = '';
                    $scope.Filters.demogrphicsdesc = '';
                    $scope.Filters.demogrphicsdatasource = 'datasource';
                    $scope.Filters.demogrphicsmethodology = '';
                    $location.path('/docs');
                }
            }
            else if (index == "data_methodology") {
                if ($scope.Filters.demogrphicsmethodology == '') {
                    $scope.Filters.demogrphicsfootnotes = '';
                    $scope.Filters.demogrphicsdesc = '';
                    $scope.Filters.demogrphicsdatasource = '';
                    $scope.Filters.demogrphicsmethodology = 'methodolgy';
                    $location.path('/docs');
                }
            }


            if ((_tblname == "ppoi_counts_by_category_country") || (_tblname == "ppoic_counts_by_category_country")) {
                //if (_tblname == "ppoi_counts_by_category_country") {
                $scope.countrytd = false;
                $scope.loadCountries();
            }
            else if ((productname.toLowerCase() == 'propertyattributeparcelplus') || (productname.toLowerCase() == 'propertyattributeparcelclassic') ||
                (productname.toLowerCase() == 'propertyattributeparcelpremium') || (productname.toLowerCase() == 'propertyattributefabric') || (productname.toLowerCase() == 'propertyattributegem')) {
                var fips = _tblname;
                var state = $scope.socialplacesselected;

                var config = {
                    headers: {
                        'Content-Type': 'application/json',
                    }
                }
                $scope.poistatsdata = [];
                $http.get('' + apiUrl + 'GetAEDStats/' + _product + '/' + state + '/' + fips, config)
                .success(function (data, status, headers, config) {
                    if (data.length > 0) {
                        $scope.alertMessage = true;
                        $scope.infoMessage = true;
                        $scope.poistatsdata = data;

                        $scope.gridStatsOptions.data = $scope.poistatsdata;
                        $scope.gridStatsOptions.height = ($scope.poistatsdata.length * 2) + "px";
                        $scope.loadStatsData(_product, $scope);
                    }
                    else {
                        $scope.alertMessage = false;
                        $scope.infoMessage = true;
                        $timeout(function () { $scope.alertMessage = false; }, 3000);
                    }
                })
                .error(function (data, status, header, config) {
                    alert('Unable to fetch AED State/Fips stats data !!!');
                });

            }
            else {
                $scope.countrytd = true;
                var config = {
                    headers: {
                        'Content-Type': 'application/json',
                    }
                }
                $scope.poistatsdata = [];
                var _currentproduct = $scope.productselected;
                $http.get('' + apiUrl + 'GetPoiStats/' + _currentproduct + "/" + _tblname, config)
                .then(function (response, status, headers, config) {
                    if (response.data.length > 0) {
                        $scope.alertMessage = true;
                        $scope.infoMessage = true;
                        $scope.poistatsdata = response.data;
                        if ($scope.poistatsdata.length > 0) {
                            var arrData = Object.getOwnPropertyNames($scope.poistatsdata[0]);
                            $scope.countryflag = false;
                            $scope.usastateflag = false;
                            $scope.countyflag = false;

                            for (var i = 0; i < arrData.length; i++) {

                                //console.log(arrData[i]);
                                if ((arrData[i].toLowerCase() == 'country') || (arrData[i].toLowerCase() == 'iso3')) {
                                    $scope.countryflag = true;
                                }
                                else if ((arrData[i].toLowerCase() == 'state') || (arrData[i].toLowerCase() == 'stabb')) {
                                    $scope.usastateflag = true;
                                }
                            }

                            if ($scope.countryflag) {
                                $scope.mapdata = [];
                                for (var i = 0; i < response.data.length; i++) {
                                    if ((_tblname == 'vendorvintage_master') || (_tblname == 'base_demographics_statistics') || (_tblname == 'routing') || (_tblname == 'streets'))
                                        $scope.mapdata.push(response.data[i].COUNTRY);
                                    else if ((_tblname == 'brandnames_counts') || (_tblname == 'brands_by_country') || (_tblname == 'brand_iso3') || (_tblname == 'country_category')
                                         || (_tblname == 'country') || (_tblname == 'geocoding_confidence_') || (_tblname == 'geocoding_precision') || (_tblname == 'tt_geo_improvement')) {
                                        $scope.mapdata.push(response.data[i].ISO3);
                                        $scope.unique($scope.mapdata);
                                    }
                                    else {
                                        $scope.mapdata.push(response.data[i].Country);
                                        $scope.unique($scope.mapdata);
                                    }
                                }
                            }
                            else if ($scope.usastateflag) {
                                $scope.mapdata = [];
                                for (var i = 0; i < response.data.length; i++) {
                                    if ((_tblname == 'paph_metrics') || (_tblname == 'risk_earthquake') || (_tblname == 'risk_firestations') || (_tblname == 'risk_floodriskpro')
                                         || (_tblname == 'telco_ahjpro') || (_tblname == 'telco_psappro_statecount')) {
                                        
                                        $scope.mapdata.push(response.data[i].Stabb);
                                        $scope.unique($scope.mapdata);
                                    }
                                    else {
                                        $scope.mapdata.push(response.data[i].State);
                                        $scope.unique($scope.mapdata);
                                    }
                                }
                            }

                            if (_tblname == 'parcel_data_coverage') {
                                $scope.mapdata = [];
                                for (var i = 0; i < response.data.length; i++) {
                                    $scope.mapdata.push(response.data[i].county);
                                    $scope.unique($scope.mapdata);
                                }
                                $scope.countyflag = true;
                            }

                            if (($scope.Filters.productlineselected != "") && ($scope.mapdata.length > 0))
                                $scope.loadMapData();
                            else {
                                $scope.clearMap();
                                //alert('No Coverage Map for selected Filters !!!')
                            }
                        }
                    }
                    else {
                        $scope.alertMessage = false;
                        $scope.infoMessage = true;
                        $timeout(function () { $scope.alertMessage = true; }, 3000);
                    }
                }, function (response, status, header, config) {
                    alert('Unable to fetch poi stats data !!!');
                });
            }


        };

        //Reading the Service object exposed and checking if it is case of return from some other page
        if ($scope.Filters.statselected != "")
            $scope.loadStats($scope.Filters.statselected);
 //************************************************************************************************************************
    /// <summary>Removing duplicates from the given Array</summary>    
        $scope.unique = function(list) {
            var result = [];
            $.each(list, function (i, e) {
                if ($.inArray(e, result) == -1) result.push(e);
            });
            return result;
        }
    
//************************************************************************************************************************
    /// <summary>Loading Countries for products having huge data so that we can show them countrywise</summary>      
    $scope.loadCountries = function () {
        var config = {
            headers: {
                'Content-Type': 'application/json',
            }
        }
        $scope.countries = [];
        $http.get('' + apiUrl + 'GetCountries', config)
        .then(function (response, status, headers, config) {

            for (var i = 0; i < response.data.length; i++) {
                $scope.countries.push(response.data[i].Country);
            }


            console.log($scope.countries);

        },function (response, status, header, config) {
            alert('Unable to fetch country data !!!');
        });
    };
//**********************************************************************************************************************************
    /// <summary>Loading countrywise data for some specefic products and populating the UI Grid</summary>
    /// <param name="_country" type="String">country for which data is to be fetched</param>
    $scope.loadCountryStats = function (_country) {

        var config = {
            headers: {
                'Content-Type': 'application/json',
            }
        }
        $scope.poicountrystatsdata = [];
        $http.get('' + apiUrl + 'GetCountryPoiStats/' + $scope.statselected + '/' + _country, config)
        .then(function (response, status, headers, config) {
            $scope.poistatsdata = response.data;
            $scope.gridStatsOptions.data = $scope.poistatsdata;
            $scope.gridStatsOptions.height = ($scope.poistatsdata.length * 2) + "px";

            $scope.loadStatsData($scope.statselected, $scope);

        },function (data, status, header, config) {
            alert('Unable to fetch country poi stats data !!!');
        });

    };
//********************************************************************************************************************************
    /// <summary>Converting string to Title case</summary>
    /// <param name="string" type="String">String to be formatted</param>
    $scope.toTitleCase = function (string) {
        // \u00C0-\u00ff for a happy Latin-1
        return string.toLowerCase().replace(/_/g, ' ').replace(/\b([a-z\u00C0-\u00ff])/g, function (_, initial) {
            return initial.toUpperCase();
        }).replace(/(\s(?:de|a|o|e|da|do|em|ou|[\u00C0-\u00ff]))\b/ig, function (_, match) {
            return match.toLowerCase();
        });
    }
//*************************************************************************************************************************
    /// <summary>Setting routes</summary>
    //$scope.return = function () {
    //    $location.path('/stats');
    //};

    //$scope.getdemographics = function () {
    //    $location.path('/demographics');
    //};

    //$scope.getfootnotes = function () {
    //    $location.path('/footnotes');
    //};
//**************************************************************************************************************************


});
//Docs Controller
app.controller('docsController', function ($scope, $http, $location, $route, $timeout, cfpLoadingBar, uiGridConstants, $window, SharedFilterService) {
    $scope.Filters = SharedFilterService;

    //Setting Style to the selected option in the left panel
    if ($scope.Filters.demogrphicsdesc != "") {
        document.getElementById('libasedemographics').className = "active";
        document.getElementById('lifootnotes').className = "";
        document.getElementById('lidatasource').className = "";
        document.getElementById('lidatamethodology').className = "";

        document.getElementById('footnotediv').style.display = 'none';
        document.getElementById('datasourcediv').style.display = 'none';
        document.getElementById('datamethodologydiv').style.display = 'none';
        document.getElementById('demographicsdiv').style.display = 'block';
    }       
    else if ($scope.Filters.demogrphicsfootnotes != "") {
        document.getElementById('libasedemographics').className = "";
        document.getElementById('lifootnotes').className = "active";
        document.getElementById('lidatasource').className = "";
        document.getElementById('lidatamethodology').className = "";

        document.getElementById('demographicsdiv').style.display = 'none';
        document.getElementById('datasourcediv').style.display = 'none';
        document.getElementById('datamethodologydiv').style.display = 'none';
        document.getElementById('footnotediv').style.display = 'block';
    }
    else if ($scope.Filters.demogrphicsdatasource != "") {
        document.getElementById('libasedemographics').className = "";
        document.getElementById('lifootnotes').className = "";
        document.getElementById('lidatasource').className = "active";
        document.getElementById('lidatamethodology').className = "";

        document.getElementById('demographicsdiv').style.display = 'none';
        document.getElementById('datasourcediv').style.display = 'block';
        document.getElementById('datamethodologydiv').style.display = 'none';
        document.getElementById('footnotediv').style.display = 'none';
    }
    else if ($scope.Filters.demogrphicsmethodology != "") {
        document.getElementById('libasedemographics').className = "";
        document.getElementById('lifootnotes').className = "";
        document.getElementById('lidatasource').className = "";
        document.getElementById('lidatamethodology').className = "active";

        document.getElementById('demographicsdiv').style.display = 'none';
        document.getElementById('datasourcediv').style.display = 'none';
        document.getElementById('datamethodologydiv').style.display = 'block';
        document.getElementById('footnotediv').style.display = 'none';
    }
    


    $scope.getEventTarget = function(e) {
        e = e || window.event;
        return e.target || e.srcElement;
    }

    var ul = document.getElementById('docul');
    ul.onclick = function (event) {
        var target = $scope.getEventTarget(event);
        //alert(target.innerHTML);
        var currdiv = target.innerHTML;
        
        if (currdiv.toLowerCase() == 'base demographics description')
        {
            document.getElementById('libasedemographics').className = "active"; 
            document.getElementById('lifootnotes').className = "";
            document.getElementById('lidatasource').className = ""; 
            document.getElementById('lidatamethodology').className = "";

            document.getElementById('footnotediv').style.display = 'none';
            document.getElementById('datasourcediv').style.display = 'none';
            document.getElementById('datamethodologydiv').style.display = 'none';
            document.getElementById('demographicsdiv').style.display = 'block';
        }
        else if (currdiv.toLowerCase() == 'base demographics foot notes')
        {
            document.getElementById('lifootnotes').className = "active";
            document.getElementById('libasedemographics').className = "";
            document.getElementById('lidatasource').className = "";
            document.getElementById('lidatamethodology').className = "";

            document.getElementById('demographicsdiv').style.display = 'none';
            document.getElementById('datasourcediv').style.display = 'none';
            document.getElementById('datamethodologydiv').style.display = 'none';
            document.getElementById('footnotediv').style.display = 'block';
        }
        else if (currdiv.toLowerCase() == 'data source details') {
            document.getElementById('lidatamethodology').className = "";
            document.getElementById('lifootnotes').className = "";
            document.getElementById('libasedemographics').className = "";
            document.getElementById('lidatasource').className = "active";

            document.getElementById('demographicsdiv').style.display = 'none';
            document.getElementById('datasourcediv').style.display = 'block';
            document.getElementById('datamethodologydiv').style.display = 'none';
            document.getElementById('footnotediv').style.display = 'none';
        }
        else if (currdiv.toLowerCase() == 'data methodology') {
            document.getElementById('lidatamethodology').className = "active";
            document.getElementById('lifootnotes').className = "";
            document.getElementById('libasedemographics').className = "";
            document.getElementById('lidatasource').className = "";

            document.getElementById('demographicsdiv').style.display = 'none';
            document.getElementById('datasourcediv').style.display = 'none';
            document.getElementById('datamethodologydiv').style.display = 'block';
            document.getElementById('footnotediv').style.display = 'none';
        }
        //else if (currdiv.toLowerCase() == 'return') {
        //    $scope.Filters.back = 'back';
        //    $location.path('/stats'); 
        //}
    };

    //$scope.return = function () {
    //    $location.path('/stats');
    //};

});


