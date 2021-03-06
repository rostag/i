angular.module('app').controller('ServiceBuiltInBankIDController', function($state, $stateParams, $scope, FormDataFactory, ActivitiService, oServiceData, BankIDAccount, ActivitiForm, uiUploader) {
    //,$timeout

  $scope.oServiceData = oServiceData;
  $scope.account = BankIDAccount;
  $scope.ActivitiForm = ActivitiForm;

  $scope.data = $scope.data || {};
  $scope.data.formData = new FormDataFactory();
  $scope.data.formData.initialize(ActivitiForm);
  $scope.data.formData.setBankIDAccount(BankIDAccount);

  var currentState = $state.$current;
  $scope.data.region = currentState.data.region;
  $scope.data.city = currentState.data.city;

    //mock markers
    /*$scope.data.formData.params.markers = {
        "validate":{
            "PhoneUA":{
                "aField_ID":["privatePhone","workPhone", "phone1"]
            }
            , "Mail":{
                "aField_ID":["privateMail","email1"]
            }            
        }
    };

    var validateIds = $scope.data.formData.params.markers.validate.PhoneUA.aField_ID;*/

    angular.forEach($scope.ActivitiForm.formProperties, function(value, key) {
        var sField = value.name;
        var s;
        if (sField === null) {
          sField = "";
        }
        var a = sField.split(";");
        s = a[0].trim();
        value.sFieldLabel = s;
        s = null;
        if (a.length > 1) {
          s = a[1].trim();
          if (s === "") {
            s = null;
          }
        }
        value.sFieldNotes = s;
        /*
        if (_.indexOf(validateIds, value.id)!=-1){
            value.sFieldType="tel";
        }*/
  });

  $scope.submit = function(form) {
    $scope.isSending = true;
    form.$setSubmitted();

    //if ($($('input[type=tel]')[0]).intlTelInput("isValidNumber")){
        
        if (form.$valid) {
            ActivitiService
                .submitForm(oServiceData, $scope.data.formData)
                .then(function(result) {
                    $scope.isSending = false;
                    var state = $state.$current;

                    var submitted = $state.get(state.name + '.submitted');
                    submitted.data.id = result.id;

                    $scope.isSending = false;
                    $scope.$root.data = $scope.$root.data || {}
                    $scope.$root.data.formData = $scope.data.formData;
                    return $state.go(submitted, $stateParams);
                })
        } else {
            $scope.isSending = false;
            return false;
        }

    //} else {
    //   $scope.isSending = false;
    //    return false;
    //}

  };

  $scope.cantSubmit = function(form) {
    return $scope.isSending || ($scope.isUploading && !form.$valid);
  };

  $scope.bSending = function(form) {
    return $scope.isSending;
  };

  $scope.isUploading = false;
  $scope.isSending = false;

  var fileKey = function(file) {
    return file.name + file.size;
  };

  $scope.uploadFile = function() {
    uiUploader.startUpload({
      url: ActivitiService.getUploadFileURL(oServiceData),
      concurrency: 2,
      onProgress: function(file) {
        $scope.isUploading = true;
        $scope.$apply();
      },
      onCompleted: function(file, response) {
        $scope.isUploading = false;
        if (response) {
          try {
            JSON.parse(response);
            alert(response);
          } catch (e) {
            ActivitiService.updateFileField(oServiceData,
              $scope.data.formData, $scope.files[fileKey(file)], response);
          }
        }
        $scope.$apply();
      }
    });
  };

  $scope.files = {};
  $scope.addFile = function(propertyId, event) {
    var files = event.target.files;
    if (files && files.length === 1) {
      uiUploader.addFiles(files);
      if (uiUploader.getFiles()[0]) {
        $scope.files[fileKey(event.target.files[0])] = propertyId;
      }
    }
    $scope.$apply();
  };
/*
    $timeout(function () {
        $('input[type=tel]').intlTelInput({
            defaultCountry: "auto",
            autoFormat: true,
            allowExtensions: true,
            preferredCountries: ["ua"],
            autoPlaceholder: false,
            geoIpLookup: function(callback) {
                $.get('http://ipinfo.io', function() {}, "jsonp").always(function(resp) {
                    var countryCode = (resp && resp.country) ? resp.country : "";
                    callback(countryCode);
                });
            }
        });
    });*/
});
