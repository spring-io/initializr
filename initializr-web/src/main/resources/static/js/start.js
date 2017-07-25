(function () {

    Versions = function () {
    };

    var strict_range = /\[(.*),(.*)\]/;
    var halfopen_right_range = /\[(.*),(.*)\)/;
    var halfopen_left_range = /\((.*),(.*)\]/;
    var qualifiers = ['M', 'RC', 'BUILD-SNAPSHOT', 'RELEASE'];

    Versions.prototype.matchRange = function (range) {
        var strict_match = range.match(strict_range);
        if (strict_match) {
            return function (version) {
                return compareVersions(strict_match[1], version) <= 0
                    && compareVersions(strict_match[2], version) >= 0;
            }
        }
        var hor_match = range.match(halfopen_right_range);
        if (hor_match) {
            return function (version) {
                return compareVersions(hor_match[1], version) <= 0
                    && compareVersions(hor_match[2], version) > 0;
            }
        }
        var hol_match = range.match(halfopen_left_range);
        if (hol_match) {
            return function (version) {
                return compareVersions(hol_match[1], version) < 0
                    && compareVersions(hol_match[2], version) >= 0;
            }
        }

        return function (version) {
            return compareVersions(range, version) <= 0;
        }
    };

    function parseQualifier(version) {
        var qual = version.replace(/\d+/g, "");
        return qualifiers.indexOf(qual) != -1 ? qual : "RELEASE";
    }

    function compareVersions(a, b) {
        var result;

        var versionA = a.split(".");
        var versionB = b.split(".");
        for (var i = 0; i < 3; i++) {
            result = parseInt(versionA[i], 10) - parseInt(versionB[i], 10);
            if (result != 0) {
                return result;
            }
        }
        var aqual = parseQualifier(versionA[3]);
        var bqual = parseQualifier(versionB[3]);
        result = qualifiers.indexOf(aqual) - qualifiers.indexOf(bqual);
        if (result != 0) {
            return result;
        }
        return versionA[3].localeCompare(versionB[3]);
    }

    /**
     * Parse hash bang parameters from a URL as key value object.
     * For repeated parameters the last parameter is effective.
     * If = syntax is not used the value is set to null.
     * #!x&y=3 -> { x:null, y:3 }
     * @param url URL to parse or null if window.location is used
     * @return Object of key -> value mappings.
     * @source https://gist.github.com/zaus/5201739
     */
    hashbang = function (url, i, hash) {
        url = url || window.location.href;

        var pos = url.indexOf('#!');
        if( pos < 0 ) return [];
        var vars = [], hashes = url.slice(pos + 2).split('&');

        for(i = hashes.length; i--;) {
            hash = hashes[i].split('=');

            vars.push({ name: hash[0], value: hash.length > 1 ? hash[1] : null});
        }

        return vars;
    }

    applyParams = function() {
        var params = hashbang();
        $.each(params, function( index, param ) {
            var value = decodeURIComponent(param.value);
            switch(param.name)  {
                case 'type':
                case 'packaging':
                case 'javaVersion':
                case 'language':
                    $('.' + param.name.toLowerCase() + '-form-group').removeClass("hidden");
                    $('#' + param.name+ ' option[value="' + value + '"]').prop('selected', true);
                    $('#' + param.name).change();
                    break;
                case 'groupId':
                case 'artifactId':
                case 'name':
                case 'description':
                case 'packageName':
                    $('.' + param.name.toLowerCase() + '-form-group').removeClass("hidden");
                    $('#' + param.name).val(value);
                    $('#' + param.name).change();
                    break;
            }
        });
    }

}());

$(function () {

    function _toConsumableArray(arr) {
        if (Array.isArray(arr)) {
            for (var i = 0, arr2 = Array(arr.length); i < arr.length; i++) {
                arr2[i] = arr[i];
            }
            return arr2;
        } else {
            return Array.from(arr);
        }
    }

    if (navigator.appVersion.indexOf("Mac") != -1) {
        $(".btn-primary").append("<kbd>&#8984; + &#9166;</kbd>");
    }
    else {
        $(".btn-primary").append("<kbd>alt + &#9166;</kbd>");
    }

    function changeGitUrl() {
        var name = $("#artifactId").val();
        var githubUrl = 'https://github.com/Grails-Plugin-Consortium/' + name;
        $("#giturl").html('<a target="_blank" href="' + githubUrl + '">' + githubUrl + '</a>');
    }

    var refreshDependencies = function (versionRange) {
        var versions = new Versions();
        $("#dependencies div.checkbox").each(function (idx, item) {
            if (!$(item).attr('data-range') || versions.matchRange($(item).attr('data-range'))(versionRange)) {
                $("input", item).removeAttr("disabled");
                $(item).removeClass("disabled has-error");
            } else {
                $("input", item).prop('checked', false);
                $(item).addClass("disabled has-error");
                $("input", item).attr("disabled", true);
                removeTag($("input", item).val());
            }
        });
    };

    // var addTag = function (id, name) {
    //     if ($("#starters div[data-id='" + id + "']").length == 0) {
    //         $("#starters").append("<div class='tag' data-id='" + id + "'>" + name +
    //             "<button type='button' class='close' aria-label='Close'><span aria-hidden='true'>&times;</span></button></div>");
    //     }
    // };

    var addTag = function(id, name, topic, description) {
        topic = topic || '';
        description = description || '';
        if ($('#starters').find('div[data-id=\'' + id + '\']').length === 0) {
            var div = $('<div title=\"' + description + '\" class=\'tag ' + topic + '\' data-id=\'' + id + '\'>' + name + '<button type=\'button\' class=\'close\' aria-label=\'Close\'><span aria-hidden=\'true\'>&times;</span></button></div>');
            div.tooltip({trigger: 'hover'});
            div.on('click', function(){
                div.tooltip('hide');
            });
            $("#starters").append(div);
        }
    }

    var removeTag = function (id) {
        $("#starters div[data-id='" + id + "']").remove();
    };
    var initializeSearchEngine = function (engine, bootVersion) {
        $.getJSON("/ui/dependencies.json?version=" + bootVersion, function (data) {
            engine.clear();
            $.each(data.dependencies, function(idx, item) {
                if(item.weight === undefined) {
                    item.weight = 0;
                }
            });
            engine.add(data.dependencies);
            if (!initialLoad) {
                $("#archetype").val('MICRO').trigger('change').blur();
                initialLoad = true;
            }
        });
    };
    var generatePackageName = function() {
        var groupId = $("#groupId").val();
        var artifactId = $("#artifactId").val();
        var package = groupId.concat(".").concat(artifactId)
            .replace(/-/g, '');
        $("#packageName").val(package);
    };
    refreshDependencies($("#bootVersion").val());
    $("#type").on('change', function () {
        $("#form").attr('action', $(this.options[this.selectedIndex]).attr('data-action'))
    });
    $("#groupId").on("change", function() {
        generatePackageName();
    });
    $("#name").on("change", function () {
        var $name = $("#name");
        $name.val($name.val().replace(/  +/g, ' ').trim());
        setArtifactIdAndBaseDir(savedPrefix, savedSuffix);
        alignArtifactAndPackageNames();
        changeGitUrl();
    });
    $("#artifactId").on('change', function () {
        $("#name").val($(this).val());
        $("#baseDir").attr('value', this.value)
        generatePackageName();
        changeGitUrl();
    });
    $("#bootVersion").on("change", function (e) {
        refreshDependencies(this.value);
        initializeSearchEngine(starters, this.value);
    });
    $(".tofullversion a").on("click", function() {
        $(".full").removeClass("hidden");
        $(".tofullversion").addClass("hidden");
        $(".tosimpleversion").removeClass("hidden");
        $("body").scrollTop(0);
        return false;
    });
    $(".tosimpleversion a").on("click", function() {
        $(".full").addClass("hidden");
        $(".tofullversion").removeClass("hidden");
        $(".tosimpleversion").addClass("hidden");
        applyParams();
        $("body").scrollTop(0);
        return false;
    });
    var maxSuggestions = 5;
    var starters = new Bloodhound({
        datumTokenizer: Bloodhound.tokenizers.obj.nonword('name', 'description', 'keywords', 'group'),
        queryTokenizer: Bloodhound.tokenizers.nonword,
        identify: function (obj) {
            return obj.id;
        },
        sorter: function(a,b) {
            return b.weight - a.weight;
        },
        limit: maxSuggestions,
        cache: false
    });
    initializeSearchEngine(starters, $("#bootVersion").val());
    $('#autocomplete').typeahead(
        {
            minLength: 2,
            autoSelect: true
        }, {
            name: 'starters',
            display: 'name',
            source: starters,
            templates: {
                suggestion: function (data) {
                    return "<div><strong>" + data.name + "</strong><br/><small>" + data.description + "</small></div>";
                },
                footer: function(search) {
                    if (search.suggestions && search.suggestions.length == maxSuggestions) {
                        return "<div class=\"tt-footer\">More matches, please refine your search</div>";
                    }
                    else {
                        return "";
                    }
                }
            }
        });
    $('#autocomplete').bind('typeahead:select', function (ev, suggestion) {
        var alreadySelected = $("#dependencies input[value='" + suggestion.id + "']").prop('checked');
        if(alreadySelected) {
            removeTag(suggestion.id);
            $("#dependencies input[value='" + suggestion.id + "']").prop('checked', false);
        }
        else {
            addTag(suggestion.id, suggestion.name, suggestion.topic, suggestion.description);
            $("#dependencies input[value='" + suggestion.id + "']").prop('checked', true);
        }
        $('#autocomplete').typeahead('val', '');
    });
    $("#starters").on("click", "button", function () {
        var id = $(this).parent().attr("data-id");
        $("#dependencies input[value='" + id + "']").prop('checked', false);
        removeTag(id);
    });
    $("#dependencies input").bind("change", function () {
        var value = $(this).val()
        if ($(this).prop('checked')) {
            var results = starters.get(value);
            addTag(results[0].id, results[0].name, results[0].topic, results[0].description);
        } else {
            removeTag(value);
        }
    });

    var savedPrefix = '';
    var savedSuffix = '';
    var currentPackageName = '';
    var currentDomainName = '';
    var initialLoad = false;

    function getNameValueParsed() {
        var convertToEmpty = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : true;

        var replacement = convertToEmpty ? '' : '-';
        return $("#name").val().trim().replace(/  +/g, ' ').replace(/ /g, replacement).replace(/_/g, replacement).replace(/-/g, replacement);
    }

    function setArtifactIdAndBaseDir(prefix, suffix) {
        var replaceDash = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : false;
        savedPrefix = prefix;
        savedSuffix = suffix;
        var name = prefix + getNameValueParsed(replaceDash).toLowerCase() + suffix;
        $("#artifactId").val(name);
        $("#baseDir").val(name);
    }

    function alignArtifactAndPackageNames() {
        $("#packageName").val(currentPackageName + '.' + (currentDomainName ? currentDomainName + '.' : '') + getNameValueParsed().toLowerCase());
        $("#groupId").val(currentPackageName);
    }

    function setDefaultPackageName(defaultPkgName) {
        currentPackageName = defaultPkgName;
        alignArtifactAndPackageNames();
    }

    var base = ['springboot', 'spring-test', 'logging', 'spock'];
    var common = [].concat(base);
    var data = ['h2', 'jdbc'];
    var data_jpa = ['data-jpa'];
    var web = ['web', 'actuator', 'payload-client', 'sba-client', 'cloud-hystrix', 'cloud-hystrix-dashboard', 'springfox', 'springfoxui', 'springfoxbean', 'restdocs', 'cloud-starter-zipkin', 'metrics'];
    var all = [].concat(web, _toConsumableArray(common), data, data_jpa);

    $("#archetype").on("change", function () {
        $("#starters div").remove();
        $("#dependencies input").prop('checked', false);
        var results = [];
        var val = $('#archetype').val();
        if (val === 'LIBRARY') {
            setDefaultPackageName('org.grails.conf');
            setArtifactIdAndBaseDir('', '');
            results = starters.get([].concat(common));
        } else if (val === 'MICRO_RABBIT') {
            setDefaultPackageName('org.grails.conf.service');
            setArtifactIdAndBaseDir('rabbit-', '-service');
            results = starters.get(['cloud-stream-binder-rabbit'].concat(_toConsumableArray(all)));
        } else if (val === 'MICRO_KAFKA') {
            setDefaultPackageName('org.grails.conf.service');
            setArtifactIdAndBaseDir('kafka-', '-service');
            results = starters.get(['cloud-stream-binder-kafka'].concat(_toConsumableArray(all)));
        } else if (val === 'MICRO') {
            setDefaultPackageName('org.grails.conf.service');
            setArtifactIdAndBaseDir('', '-service');
            results = starters.get([].concat(_toConsumableArray(all)));
        } else if (val === "APP_WEB_DATA") {
            setDefaultPackageName('org.grails.conf');
            setArtifactIdAndBaseDir('app-', '');
            results = starters.get([].concat(_toConsumableArray(all)));
        } else {
            setDefaultPackageName('org.grails.conf');
            setArtifactIdAndBaseDir('', '', false);
            results = starters.get(['']);
        }

        for (var i = 0; i < results.length; i++) {
            addTag(results[i].id, results[i].name, results[i].topic, results[i].description);
            $('#dependencies input[value=\'' + results[i].id + '\']').prop('checked', true);
        }

        changeGitUrl();
    });

    Mousetrap.bind(['command+enter', 'alt+enter'], function (e) {
        $("#form").submit();
        return false;
    });
    var autocompleteTrap = new Mousetrap($("#autocomplete").get(0));
    autocompleteTrap.bind(['command+enter', 'alt+enter'], function (e) {
        $("#form").submit();
        return false;
    });
    autocompleteTrap.bind("enter", function(e) {
        if (e.preventDefault) {
            e.preventDefault();
        } else {
            e.returnValue = false;
        }
    });
    applyParams();
    if ("onhashchange" in window) {
        window.onhashchange = function() {
            $(".full").addClass("hidden");
            $(".tofullversion").removeClass("hidden");
            $(".tosimpleversion").addClass("hidden");
            applyParams();
        }
    }
});
