//var initController = function($scope, $http, $filter, $cookies){
var initController = function($scope, $http, $filter){
	console.log("initController");
	$scope.param = parameters;
	console.log($scope.param);
	$scope.inputType = "text";
	$scope.isRoleRuh = false;
	console.log($scope.inputType);
//	initDocCookie($scope);
	
	var isRole = function(role){
		angular.forEach($scope.moveTodayPatients.principal.authorities, function(authoritie, key) {
			console.log(authoritie);
			console.log(authoritie.authority);
			if(authoritie.authority.indexOf(role) >= 0){
				$scope.isRoleRuh = true;
			}
		});
	}
	initDateVariables = function(){
		var today = new Date($scope.moveTodayPatients.today);
//		$cookies.put('year', today.getFullYear());
		$scope.excelFileName = "pyx-" +today.getFullYear() + "-v.2.xls";
	}
	
	$scope.readMoveTodayPatients = function(){
		var url = "/readMoveTodayPatients"
		if(parameters.date){
			var url = "/readMove-"+parameters.date+"-Patients"
		}
		console.log(url);
		$http({ method : 'GET', url : url
		}).success(function(data, status, headers, config) {
			$scope.moveTodayPatients = data;
			console.log(data);
			initDateVariables();
			isRole("ruh");
			console.log($scope.isRoleRuh);
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});
	}

	$scope.formatDateyyyyMMdd = function(d){
		return $filter('date')( d, "yyyy-MM-dd")
	}

	$scope.isMyDate = function(d){
		return parameters.date == $scope.formatDateyyyyMMdd(d);
	}

	$scope.summ = function(fieldName){
		var summ = 0;
		if($scope.moveTodayPatients)
			angular.forEach($scope.moveTodayPatients.moveTodayPatientsList, function(department, key) {
				summ += department[fieldName]/1;
			});
		return summ;
	}

	$scope.summMinusDR = function(fieldName){
		var summ = 0;
		if($scope.moveTodayPatients)
			angular.forEach($scope.moveTodayPatients.moveTodayPatientsList, function(department, key) {
				if(department.DEPARTMENT_ID != 5
				&& department.DEPARTMENT_ID != 22
				){
					summ += department[fieldName];
				}
			});
		return summ;
	}

	$scope.today = new Date();
	$scope.last7day = [$scope.today];
	for (var i = 0; i < 7; i++) {
		$scope.last7day.push(new Date($scope.last7day[i].getTime() - (24*60*60*1000)));
	}
	$scope.last7day.reverse();
	

	if(parameters.date){
		$scope.paramDate = parameters.date;
	}else{
		$scope.paramDate = $filter('date')(new Date(), "yyyy-MM-dd");
	}
	console.log("------------------");
	$scope.paramDateDate = new Date(Date.parse($scope.paramDate));
	console.log($scope.paramDateDate);
	$scope.addDay = function(date, day){
		var dateOffset = (24*60*60*1000) * day; //5 days
		var myDate = new Date();
		myDate.setTime(date.getTime() + dateOffset);
		return myDate;
	}
}
// ------------OperationCodeCtrl
hol2eih3App.controller('OperationCodeCtrl', ['$scope', '$http', '$filter', '$sce'
		, function ($scope, $http, $filter, $sce) {
	console.log("OperationCodeCtrl");
	$scope.param = parameters;
	console.log($scope.param);
	$scope.model = {};
	$scope.error = [];
	
	$scope.calcOperationDurationHHmm = function(operation){
		initDurationOp(operation);
		console.log(operation.durationOpMin);
		var free_min = Math.ceil(operation.durationOpMin%60);
		var h = Math.floor((operation.durationOpMin)/60);
		operation.durationOpHHmm = "";
		if(h>0)
			operation.durationOpHHmm = h+" год. ";
		operation.durationOpHHmm += free_min+" хв.";
		console.log(operation.durationOpHHmm);
		return operation.durationOpHHmm;
	}

	var initDurationOp = function(operation){
		if(!operation.durationOpMin){
			operation.durationOpMin =
				(operation.operation_history_end - operation.operation_history_start)/1000/60;
		}
	}
	$scope.loginToThisPage = function(){
		return "/throughlogin/gotopage/h/operation-code.html"+window.location.search;
	};

	if($scope.param.ix){
		$http({ method : 'GET', url : "/v/ix/"+$scope.param.ix+window.location.search
		}).success(function(model, status, headers, config) {
			$scope.ix = model;
			console.log($scope.ix);
		}).error(function(model, status, headers, config) {
			$scope.error.push(model);
		});
	}

	$http({ method : 'GET', url : "/v/readAuthorityUser"
	}).success(function(model, status, headers, config) {
		$scope.model = model;
		console.log($scope.model);
		console.log($scope.model.departmentId);
		if($scope.model.departmentId)
			$http({ method : 'GET', url : "/v/department-patient/"+$scope.model.departmentId
			}).success(function(model, status, headers, config) {
				$scope.model.departmentPatient = model;
				console.log($scope.model);
			}).error(function(model, status, headers, config) {
				$scope.error.push(model);
			});
	}).error(function(model, status, headers, config) {
		$scope.error.push(model);
	});
}]);
// ------------OperationCodeCtrl END
hol2eih3App.controller('HomeCtrl', ['$scope', '$http', '$filter', '$sce'
		, function ($scope, $http, $filter, $sce) {
	console.log("HomeCtrl");
	$http({ method : 'GET', url : "/v/readHome"+window.location.search
	}).success(function(model, status, headers, config) {
		$scope.model = model;
		console.log($scope.model);
	}).error(function(model, status, headers, config) {
		$scope.error = model;
	});
}]);
//  1  Запис надходжень/виписки хворих за сьогодні – saveMovePatients.html.
//hol2eih3App.controller('SaveCopeTodayPatientsCtrl', ['$cookies', '$cookieStore', '$scope', '$http', '$filter', '$sce'
//, function ($cookies, $cookieStore, $scope, $http, $filter, $sce) {
//initController($scope, $http, $filter, $cookies);
hol2eih3App.controller('SaveCopeTodayPatientsCtrl', ['$scope', '$http', '$filter', '$sce'
		, function ($scope, $http, $filter, $sce) {
	initController($scope, $http, $filter);
	//  1.1  Зчитування надходження/виписки хворих на сьогодні – readTodayMovePatients
	$scope.readMoveTodayPatients();
	
	// 1.2   Запис надходження/виписки хворих на сьогодні – saveMoveTodayPatients
	$scope.saveMoveTodayPatients = function(){
		var url = "/save-"+$scope.paramDate+"-Patients";
		var today = new Date();
		console.log(today.getFullYear()+"-"+today.getMonth()+"-"+today.getDate());
		if(!$scope.paramDate){
			url = "/save-"+today.getFullYear()+"-"+today.getMonth()+"-"+today.getDate()+"-Patients";
		}
		console.log(url);
		console.log($scope.moveTodayPatients);
		$http({ method : 'POST', data : $scope.moveTodayPatients, url : url
		}).success(function(data, status, headers, config){
			console.log(data);
			$scope.moveTodayPatients = data;
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});
	}
	$scope.calc = function(departmentHol){
		console.log(departmentHol);
		var sum = 0;
		if(departmentHol.MOVEDEPARTMENTPATIENT_PATIENT1DAY)
			sum = departmentHol.MOVEDEPARTMENTPATIENT_PATIENT1DAY/1;
		if(departmentHol.MOVEDEPARTMENTPATIENT_IN)
			sum += departmentHol.MOVEDEPARTMENTPATIENT_IN/1;
		if(departmentHol.MOVEDEPARTMENTPATIENT_INDEPARTMENT)
			sum += departmentHol.MOVEDEPARTMENTPATIENT_INDEPARTMENT/1;
		if(departmentHol.MOVEDEPARTMENTPATIENT_OUTDEPARTMENT)
			sum -= departmentHol.MOVEDEPARTMENTPATIENT_OUTDEPARTMENT/1;
		if(departmentHol.MOVEDEPARTMENTPATIENT_OUT)
			sum -= departmentHol.MOVEDEPARTMENTPATIENT_OUT/1;
		if(departmentHol.MOVEDEPARTMENTPATIENT_DEAD)
			sum -= departmentHol.MOVEDEPARTMENTPATIENT_DEAD/1;
		departmentHol.MOVEDEPARTMENTPATIENT_PATIENT2DAY = sum;
	}

}]);
var fileUploadApp = angular.module('fileUploadApp', ['ngFileUpload']);
fileUploadApp.controller('fileUploadCtrl', ['$scope', 'Upload', '$timeout', '$http', function ($scope, Upload, $timeout, $http) {
	console.log("UploadExcelCtrl");

	$scope.setFile = function(e){
		if(e.picFile){
			console.log("---------------");
			console.log(e.picFile.name);
			console.log("---------------");
		}
	}

	$scope.uploadPic = function(file) {
		console.log("---------------");
		console.log(file);
		console.log(file.name);
		console.log("---------------");
		file.upload = Upload.upload({
//			url: 'https://angular-file-upload-cors-srv.appspot.com/upload',
			url: '/upload',
//			url: '/upload2',
			data: {file: file, fileName: file.name},
		});

		file.upload.then(function (response) {
			console.log("file.upload.then 1 param");
			console.log(response);
			$timeout(function () {
				file.result = response.data;
			});
		}, function (response) {
			console.log("file.upload.then 2 param");
			console.log(response);
			if (response.status > 0)
				$scope.errorMsg = response.status + ': ' + response.data;
		}, function (evt) {
			console.log("file.upload.then 3 param");
			console.log(evt);
			console.log("------------------------");
//			Math.min is to fix IE which reports 200% sometimes
			file.progress = Math.min(100, parseInt(100.0 * evt.loaded / evt.total));
		});
	}

}]);

//hol2eih3App.controller('DepartmentMonthMovementH2Ctrl', ['$cookies', '$cookieStore', '$scope', '$http', '$filter', '$sce'
//                                                         , function ($cookies, $cookieStore, $scope, $http, $filter, $sce) {
hol2eih3App.controller('DepartmentMonthMovementH2Ctrl', ['$scope', '$http', '$filter', '$sce'
                                                       , function ( $scope, $http, $filter, $sce) {
	console.log("DepartmentMonthMovementH2Ctrl");
	var url = "/r/readBedDayH2-2-2";
	$http({ method : 'GET', url : url
	}).success(function(data, status, headers, config) {
		$scope.bedDay = data;
		console.log($scope.bedDay);
//			initDateVariables();
	}).error(function(data, status, headers, config) {
		$scope.error = data;
	});
}]);

var initQuartal = function($scope, $http){
	$scope.eqMonth = function(){
		console.log(" - "+$scope.minMonth+" - "+$scope.maxMonth);
		var url = "?m1="+$scope.minMonth+"&m2="+$scope.maxMonth+"&department_id=" + $scope.param.department_id+"&type="+$scope.eqMonthType;
		window.location.href = url;
	}
eqMonth = function(){
	var url1 = $scope.url1;
	var url = url1 + "-" + $scope.minMonth + "-" + $scope.maxMonth + "-" + $scope.param.department_id;
	console.log(url);
	console.log($scope.showExpandIcd10);
//	alert(url)
	$http({ method : 'GET', url : url
	}).success(function(data, status, headers, config) {
		$scope.bedDay = data;
		console.log($scope.bedDay);
		if($scope.param.viddilennja){
			$scope.bedDayOfMonthMySql 
			= $scope.bedDay.bedDayOfMonthMySql[$scope.param.viddilennja]
			console.log($scope.bedDayOfMonthMySql);
			mmArray();
		}
//		initDateVariables();
	}).error(function(data, status, headers, config) {
		$scope.error = data;
	});
}
}

var initReport = function($scope){
	$scope.param = parameters;
	console.log($scope.param);
	$scope.eqMonthType = "month";
	if(parameters.type){
		$scope.eqMonthType = parameters.type;
	}
	var today = new Date();
	var mm = today.getMonth();
	$scope.yyyy = today.getFullYear();
	if(mm==0) mm=1;
	$scope.minMonth = mm;
	$scope.maxMonth = mm;
	if(parameters.m1){
		$scope.minMonth = parameters.m1;
		if($scope.eqMonthType == "month"){
			$scope.maxMonth = parameters.m1;
		}else if(parameters.m2){
			$scope.maxMonth = parameters.m2;
		}
	}
	
	$scope.error = [];
	errorHandle = function(response, $scope){
		$scope.error.push(response);
		console.log($scope.error);
	}
	
	mmArray = function(){
		if($scope.param.department_id > 0){
			$scope.mmArray =[];
			for (var i = $scope.minMonth*1; i <= $scope.maxMonth; i++) {
				$scope.mmArray.push(i);
			}
			console.log($scope.mmArray);
			if($scope.bedDay){
				$scope.mmNewArray =[];
				if($scope.bedDay.bedDayDepartmentMySql){
					if($scope.mmArray.length > $scope.bedDay.bedDayDepartmentMySql.length){
						console.log($scope.bedDay.bedDayDepartmentMySql.length);
						var mmNew = $scope.mmArray.length - $scope.bedDay.bedDayDepartmentMySql.length;
						for (var i = 1; i <= mmNew; i++) {
							$scope.mmNewArray.push(i);
						}
						console.log($scope.mmNewArray);
					}
				}
			}
		}
	}
	
	$scope.isWaleMonth = function(month){
		return month >= $scope.minMonth && month <= $scope.maxMonth;
	}
	$scope.setMonth = function(month){
		console.log($scope.eqMonthType);
		if($scope.eqMonthType == "month"){
			$scope.maxMonth = month;
			$scope.minMonth = month;
		}else{
			if($scope.minMonth == null){
				$scope.minMonth = month;
			}
			if($scope.maxMonth == null){
				$scope.maxMonth = month;
			}
			if(month < $scope.minMonth){
				$scope.minMonth = month;
			}else if(month > $scope.maxMonth){
				$scope.maxMonth = month;
			}else if((month - $scope.minMonth) < ($scope.maxMonth - month)){
				$scope.minMonth = month;
			}else if((month - $scope.minMonth) > ($scope.maxMonth - month)){
				$scope.maxMonth = month;
			}else{
				$scope.maxMonth = month;
				$scope.minMonth = month;
			}
		}
		console.log(month+" - "+$scope.minMonth+" - "+$scope.maxMonth);
		mmArray();
	}
	
	$scope.eqMonth = function(){
		console.log(" - "+$scope.minMonth+" - "+$scope.maxMonth);
//	eqMonth();
		var url = "?department_id="+parameters.department_id + "&m1="+$scope.minMonth+"&m2="+$scope.maxMonth+"&type="+$scope.eqMonthType;
		if(!(parameters.department_id > 0))
			url = "?m1="+$scope.minMonth+"&m2="+$scope.maxMonth+"&type="+$scope.eqMonthType;
//		window.open();
		window.location.href = url;
	}
	
	$scope.months = {
		'nominative': '_січень_лютий_березень_квітень_травень_червень_липень_серпень_вересень_жовтень_листопад_грудень'.split('_'),
		'accusative': '_січня_лютого_березня_квітня_травня_червня_липня_серпня_вересня_жовтня_листопада_грудня'.split('_')
	};
}

//hol2eih3App.controller('K1Icd10Ctrl', ['$cookies', '$cookieStore', '$scope', '$http', '$filter', '$sce'
//                                       , function ($cookies, $cookieStore, $scope, $http, $filter, $sce) {
hol2eih3App.controller('K1Icd10Ctrl', [ '$scope', '$http', '$filter', '$sce'
	, function ( $scope, $http, $filter, $sce) {
	console.log("K1Icd10Ctrl");
	initReport($scope);
	$scope.icd10Head = [
		{"title":"","name":"МКХ-10","key":"ICD_CODE"}
		,{"title":"","name":"Діагноз","key":"ICD_NAME"}
		,{"title":"","name":"Кількість","key":"CNT"}
	];

	eqMonth = function(){
				var url1 = "/r/readIcd10K1-";
		var url = url1 + $scope.minMonth + "-" + $scope.maxMonth;
		console.log(url);
		$http({ method : 'GET', url : url
		}).success(function(data, status, headers, config) {
			$scope.k1 = data;
			console.log($scope.k1);
		}).error(function(data, status, headers, config) {
			$scope.error.push(data);
			console.log($scope.error);
		});
	}

	eqMonth();
	
}]);

//hol2eih3App.controller('DepartmentMotionCtrl', ['$cookies', '$cookieStore', '$scope', '$http', '$filter', '$sce'
//                                                , function ($cookies, $cookieStore, $scope, $http, $filter, $sce) {
hol2eih3App.controller('DepartmentMotionCtrl', [ '$scope', '$http', '$filter', '$sce'
		, function ( $scope, $http, $filter, $sce) {
	console.log("DepartmentMotionCtrl");
	$scope.url1 = "/r/readDepartmentMotion";
	initReport($scope);
	initQuartal($scope, $http);

	eqMonth();

}]);

//hol2eih3App.controller('DepartmentAdressCtrl', ['$cookies', '$cookieStore', '$scope', '$http', '$filter', '$sce'
//                                                , function ($cookies, $cookieStore, $scope, $http, $filter, $sce) {
hol2eih3App.controller('DepartmentAdressCtrl', ['$scope', '$http', '$filter', '$sce'
		, function ($scope, $http, $filter, $sce) {
	console.log("DepartmentAdressCtrl");
	$scope.url1 = "/r/readDepartmentAdress";
	initReport($scope);
	initQuartal($scope, $http);
	$scope.bedDayHead = [
 {"title":"","name":"Райони","key":""}
 ,{"title":"Поступило з направленням","name":"з напр.","key":"with"}
 ,{"title":"Поступило без направлення","name":"без напр.","key":"without"}
 ,{"title":"Переведений з іншого відділення","name":"пер. з","key":"from_dep"}
 ,{"title":"Переведений в інше відділення","name":"пер. в","key":"to_dep"}
 ,{"title":"Виписаний","name":"вип.","key":"remove"}
 ,{"title":"Померлі","name":"пом.","key":"dead"}
 ];

eqMonth();

}]);

//hol2eih3App.controller('DepartmentIcd10Ctrl', ['$cookies', '$cookieStore', '$scope', '$http', '$filter', '$sce'
//                                               , function ($cookies, $cookieStore, $scope, $http, $filter, $sce) {
hol2eih3App.controller('DepartmentIcd10Ctrl', [ '$scope', '$http', '$filter', '$sce'
		, function ($scope, $http, $filter, $sce) {
	console.log("DepartmentIcd10Ctrl");
	initReport($scope);
	console.log($scope.param);
	console.log($scope.param.showExpandIcd10==1);
	$scope.url1 = $scope.param.showExpandIcd10==1?"/r/readDepartmentIcd10":"/r/readDepartmentIcd10Group";
	initQuartal($scope, $http);

$scope.bedDayHead = [
	{"title":"","name":"МКХ10","key":""}
	,{"title":"","name":"Місто","key":"misto"}
	,{"title":"","name":"Село","key":"selo"}
	,{"title":"Поступило з направленням","name":"з напр.","key":"referral_has"}
	,{"title":"Поступило без направлення","name":"без напр.","key":"referral_self"}
	,{"title":"Переведений з іншого відділення","name":"пер. з","key":"referral_hol"}
	,{"title":"Переведений в інше відділення","name":"пер. в","key":"remove_hol"}
	,{"title":"Виписаний","name":"вип.","key":"remove_true"}
	,{"title":"Померлі","name":"пом.","key":"remove_dead"}
];

$scope.clickShowAllIcd10 = function(){
	console.log("showAllIcd10 "+$scope.showAllIcd10+"; showExpandIcd10 "+$scope.showExpandIcd10);
}

$scope.showIcd10 = function(bedDayOfMonthMySql){
	if($scope.showAllIcd10)
		return true;
	var showIcd10 = bedDayOfMonthMySql.misto_cnt + bedDayOfMonthMySql.selo_cnt > 0;
	return showIcd10;
}

eqMonth();

}]);

//hol2eih3App.controller('DepartmentMonthMovementMySqlCtrl', ['$cookies', '$cookieStore', '$scope', '$http', '$filter', '$sce'
//                                                            , function ($cookies, $cookieStore, $scope, $http, $filter, $sce) {
hol2eih3App.controller('DepartmentMonthMovementMySqlCtrl', [ '$scope', '$http', '$filter', '$sce'
	, function ( $scope, $http, $filter, $sce) {
	console.log("DepartmentMonthMovementCtrl");
	initReport($scope);
	
	$scope.bedDayHead = [
		{"title":"","name":"Відділення","key":""}
		,{"title":" (ліжка)","name":"Ліжок на штаті","key":"department_bed"}
		,{"title":"Поступило (люди)","name":"Поступило","key":"in_clinic"}
		,{"title":"Виписано (люди)","name":"Виписано","key":"out_clinic"}
		,{"title":"Переведено в інші відділення (люди)"
			,"name":"П. в інші від.","key":"in_dep"}
		,{"title":"Переведено з інші відділення (люди)"
			,"name":"П. з інші від.","key":"out_dep"}
		,{"title":"Померло (люди)","name":"Померло","key":"dead"}
		,{"title":"Лікарняна летальність (%)","name":"Л-на летальність","key":"mortality"}
		,{"title":"Лікувалось хворих (люди)","name":"Лік. хворих","key":"TREAT"}
		,{"title":"Проведено ліжкоднів (дні)","name":"Пр-но ліжкоднів","key":"bed_day"}
		,{"title":"План ліжкоднів (дні)","name":"Пл-н ліжкоднів","key":"bed_day_plan"}
		,{"title":"Процент виконання плана ліжкоднів (%)","name":"% вик. плана","key":"bed_day_fulfil"}
		,{"title":"Зайнятість ліжка (днів)","name":"З-ть ліжка","key":"bed_occupancy"}
		,{"title":"Оборот ліжка (раз)","name":"Оборот ліжка","key":"bed_turnover"}
		,{"title":"Оборот ліжка (раз)","name":"Оборот ліжка (альтернатів)","key":"bed_turnover2"}
		,{"title":"Ср. трив. лікуання (днів)","name":"Ср. трив. лікуання","key":"treat_avg"}
	];

	eqMonth = function(){
		var url1 = "/r/readBedDayMySql-";
		if($scope.param.department_id)
			url1 = "/r/readBedDayDepartmentMySql-";
		var url = url1 + $scope.minMonth + "-" + $scope.maxMonth;
		console.log(url);
		$http({ method : 'GET', url : url
		}).success(function(data, status, headers, config) {
			$scope.bedDay = data;
			console.log($scope.bedDay);
			if($scope.param.department_id){
				for (var i = 0; i < $scope.bedDay.bedDayOfMonthMySql.length; i++) {
					if($scope.bedDay.bedDayOfMonthMySql[i].dp_id == $scope.param.department_id){
						$scope.bedDayOfMonthMySql = $scope.bedDay.bedDayOfMonthMySql[i];
						if($scope.bedDay.bedDayDepartmentMySql){
							$scope.bedDayOfMonthMySql.bedDayDepartmentMySql = [];
							for (var j = 0; j < $scope.bedDay.bedDayDepartmentMySql.length; j++) {
								var x = $scope.bedDay.bedDayDepartmentMySql[j][i];
								$scope.bedDayOfMonthMySql.bedDayDepartmentMySql.push(x);
							}
						}
						break;
					}
				}
				console.log($scope.bedDayOfMonthMySql);
				mmArray();
			}
//			initDateVariables();
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});
	}

	eqMonth();


	mmArray();
}]);

//hol2eih3App.controller('MvPatientInWeekDayCtrl', ['$cookies', '$cookieStore', '$scope', '$http', '$filter', '$sce'
//                                                  , function ($cookies, $cookieStore, $scope, $http, $filter, $sce) {
//	console.log("/readMove-day-Patients");
//	initController($scope, $http, $filter, $cookies);
hol2eih3App.controller('MvPatientInWeekDayCtrl', ['$scope', '$http', '$filter', '$sce'
		, function ( $scope, $http, $filter, $sce) {
	console.log("/readMove-day-Patients");
	initController($scope, $http, $filter);
	// 1.1  Зчитування надходження/виписки хворих на сьогодні – readTodayMovePatients
	$scope.readMoveTodayPatients();
	$scope.isParamDate = function(month, day){
		if($scope.paramDateDate.getDate() == day){
			if(month == $scope.paramDateDate.getMonth() + 1){
				return true;
			}
		}
		return false;
	}
	$scope.monthDayDate = function(month, day){
//		var d2 = new Date($cookies.get('year'), month - 1, day);
		var d2 = new Date(2016, month - 1, day);
		return d2;
//		return new Date(new Date().getFullYear(), month - 1, day);
	}
	$scope.passedDays = function(m,d){
		return new Date() > $scope.monthDayDate(m,d);
	}
	$scope.satSanDay = function(m,d){
		return $scope.monthDayDate(m,d).getDay() == 6 || $scope.monthDayDate(m,d).getDay() == 0;
	}
	$scope.monthDay = function(m,d){
		var dt = new Date(new Date().getFullYear(), m, 0).getDate();
		if(d>dt){
			return;
		}
		return d;
	}
}]);

// 2  Показ кількості надходжень/виписки хворих за останні 7 днів – movePatients.html.
hol2eih3App.controller('MovePatientsCtrl', [ '$scope', '$http', '$filter', '$sce', function ($scope, $http, $filter, $sce) {
	console.log("/readMovePatients");
	$scope.today = new Date();
	$scope.last7day = [$scope.today];
	for (var i = 0; i < 7; i++) {
		$scope.last7day.push(new Date($scope.last7day[i].getTime() - (24*60*60*1000)));
	}
	$scope.last7day.reverse();

	$scope.yyyyMMdd = function(d){
		return moment(d).format("YYYY-MM-DD");
	}

	// 2.1  Зчитування руху хворих за останні 7 днів – readMovePatients
	$scope.readMovePatients = function(){
		$http({ method : 'GET', url : "/readMovePatients"
		}).success(function(data, status, headers, config) {
			$scope.movePatients = data;
			console.log($scope.movePatients);
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});
	}
	$scope.readMovePatients();
	
}]);
// 3  План операцій – operationsPlan.html
hol2eih3App.controller('OperationsPlanCtrl', [ '$scope', '$http', '$filter', '$sce', function ($scope, $http, $filter, $sce) {
	// 3.2  Зчитування плану операцій на сьогодні – readTodayOperationsPlan
	$scope.readTodayOperationsPlan = function(){
		$http({ method : 'GET', url : "/readTodayOperationsPlan"
		}).success(function(data, status, headers, config) {
			$scope.movePatients = data;
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});
	}
	$scope.readTodayOperationsPlan();
	// 3.1  Запис плану операцій на завтра – saveTomorrowOperationsPlan
	$scope.saveTomorrowOperationsPlan = function(){
		$http({ method : 'POST', data : $scope.moveTodayPatients, url : "/saveTomorrowOperationsPlan"
		}).success(function(data, status, headers, config){
			console.log(data);
			$scope.tomorrowOperationsPlan = data;
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});
	}

}]);
//4  Список операцій відділення по датах –  departmentOperationsList.html
hol2eih3App.controller('DepartmentOperationsListCtrl', [ '$scope', '$http', '$filter', '$sce', function ($scope, $http, $filter, $sce) {
	// 4.1  Зчитування списку операцій за останні 7 днів –  readListOperationsPlan
	$scope.readListOperationsPlan = function(){
		$http({ method : 'GET', url : "/readListOperationsPlan"
		}).success(function(data, status, headers, config) {
			$scope.movePatients = data;
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});
	}
	$scope.readListOperationsPlan();
}]);
