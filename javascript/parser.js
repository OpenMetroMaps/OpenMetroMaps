function Parser() {

    var self = this;

    this.stations = [];

    this.parse = function(data) {
        var $xml = $(data)
        this.station = $xml.find('station');
        $xml.find('station').each(function(){
            self.stations.push($(this).attr('name'));
        });
    }

}
