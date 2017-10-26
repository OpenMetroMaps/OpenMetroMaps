function Parser() {

    var self = this;

    this.stations = [];
    this.lines = [];
    this.viewstations = [];

    this.parse = function(data) {
        var $xml = $(data)

        var stations = $xml.find('stations');
        stations.find('station').each(function(){
            self.stations.push($(this).attr('name'));
        });

        var lines = $xml.find('lines');
        lines.find('line').each(function(){
            self.lines.push($(this).attr('name'));
        });

        var view = $xml.find('view');
        view.find('station').each(function(){
            self.viewstations.push($(this).attr('name'));
        });
    }

}
