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

}());

$(function () {
    if (navigator.appVersion.indexOf("Mac") != -1) {
        $("#shortcut").html("or <kbd>command</kbd> + <kbd>enter</kbd>")
    }
    else {
        $("#shortcut").html("or <kbd>ctrl</kbd> + <kbd>enter</kbd>")
    }

    var refreshDependencies = function (versionRange) {
        var versions = new Versions();
        $("#dependencies div.checkbox").each(function (idx, item) {
            if ($(item).attr('data-range') === 'null' || versions.matchRange($(item).attr('data-range'))(versionRange)) {
                $(item).show();
            } else {
                $(item).hide();
                $("input", item).prop('checked', false);
            }
        });
    };
    var addTag = function (id, name) {
        if ($("#starters div[data-id='" + id + "']").length == 0) {
            $("#starters").append("<div class='tag' data-id='" + id + "'>" + name +
                "<button type='button' class='close' aria-label='Close'><span aria-hidden='true'>&times;</span></button></div>");
        }
    };
    var removeTag = function (id) {
        $("#starters div[data-id='" + id + "']").remove();
    };
    var initializeSearchEngine = function (engine, bootVersion) {
        $.getJSON("/dependencies.json?version=" + bootVersion, function (data) {
            engine.clear();
            engine.add(data);
        });
    };
    refreshDependencies($("#bootVersion").val());
    $("#type").on('change', function () {
        $("#form").attr('action', $(this.options[this.selectedIndex]).attr('data-action'))
    });
    $("#artifactId").on('change', function () {
        $("#baseDir").attr('value', this.value)
    });
    $("#bootVersion").on("change", function (e) {
        refreshDependencies(this.value);
        initializeSearchEngine(starters, this.value);
    });
    var starters = new Bloodhound({
        datumTokenizer: Bloodhound.tokenizers.obj.whitespace('name', 'description'),
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        identify: function (obj) {
            return obj.id;
        },
        sorter: function(a,b) {
            return 0;
        },
        cache: false
    });
    initializeSearchEngine(starters, $("#bootVersion").val());
    $('#autocomplete').typeahead(
        {
            minLength: 2
        }, {
            name: 'starters',
            display: 'name',
            source: starters,
            templates: {
                suggestion: function (data) {
                    return "<div><strong>" + data.name + "</strong><br/><small>" + data.description + "</small></div>";
                }
            }
        });
    $('#autocomplete').bind('typeahead:select', function (ev, suggestion) {
        var versions = new Versions();
        var bootVersion = $("#bootVersion").val();
        if (!suggestion.versionRange || versions.matchRange(suggestion.versionRange)(bootVersion)) {
            addTag(suggestion.id, suggestion.name);
            $("#dependencies input[value='" + suggestion.id + "']").prop('checked', true);
        }
        else {
            $(".message div").removeClass("invisible");
            window.setTimeout(function () {
                $(".message div").addClass("invisible");
            }, 3000);
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
            addTag(results[0].id, results[0].name);
        } else {
            removeTag(value);
        }
    });
    Mousetrap.bind(['command+enter', 'ctrl+enter'], function (e) {
        $("#form").submit();
        return false;
    });
    var autocompleteTrap = new Mousetrap($("#autocomplete").get(0));
    autocompleteTrap.bind(['command+enter', 'ctrl+enter'], function (e) {
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
});