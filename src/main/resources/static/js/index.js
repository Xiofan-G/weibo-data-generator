(function () {

    var userId = Date.now() + '';

    /**
     * Get Labels
     */
    function initLabels() {
        $.ajax({
            url: './labels',
            dataType: 'json',
            success: function (res) {
                var vertex = $('#vertex-select')
                vertex.append(res.vertexLabels.map(function (item) {
                    return '<option value="' + item + '">' + item + '</option>';
                }).join(''));
                vertex.select2({
                    width: 'resolve' // need to override the changed default
                });

                var edge = $('#edge-select')
                edge.append(res.edgeLabels.map(function (item) {
                    return '<option value="' + item + '">' + item + '</option>';
                }).join(''));
                edge.select2({
                    width: 'resolve' // need to override the changed default
                });
            }
        })
    }

    /**
     * Initialization of FilterEvent
     * submit-->websocket(JSON-->ControlMessage-->toString)-->kafka
     * @param {Websocket} cws control websocket实例
     * @param {echarts} graph
     */
    function initFilterEvent(cws, graph) {
        var selects = $('select');
        var checks = $('input[type=checkbox]');
        var numInputs = $('input[type=number]');
        var btn = $('#submit-btn');
        btn.on('click', function () {
            var vertext = $("#vertex-select").select2("data");
            var edge = $("#edge-select").select2("data");

            var vertextValue = '';
            vertext.forEach(function (v) {
                vertextValue += v.id + ",";
            })
            var edgeValue = '';
            edge.forEach(function (v) {
                edgeValue += v.id + ",";
            })
            var condition = {
                userId: userId,
                windowSize: numInputs[0].value + selects[0].value,
                slideSize: numInputs[1].value + selects[1].value,
                vertexLabel: vertextValue,
                edgeLabel: edgeValue,
                withGrouping: checks[3].checked
            };
            cws.send(JSON.stringify(condition));
            graph.showLoading();
            btn.attr('disabled', 'disabled');
        })
    }


    /**
     * Initialization of control websocket发送控制的
     */
    function initControlWebsocket() {
        var btn = $('#submit-btn');
        btn.attr('disabled', 'disabled');
        var cws = new WebSocket('ws://' + location.host + '/control/' + userId);
        cws.addEventListener('open', function (evt) {
            btn.attr('disabled', false);
            console.log('cws connection open ...');
        });
        return cws;
    }

    /**
     * Initialization of data websocket接受数据的
     */
    function initDataWebsocket() {
        var dws = new WebSocket('ws://localhost:8080/graphData');
        dws.addEventListener('open', function (evt) {
            console.log('dws connection open ...');
        });
        return dws;
    }

    /**
     * @param {Websocket} dws Initialization of Graph (eCharts)
     */
    function initGraph(dws) {
        var rightDom = document.getElementById('right-side');
        var canvasDom = document.createElement('div');
        canvasDom.style.cssText = 'height:100%;';
        rightDom.appendChild(canvasDom);
        var myChart = echarts.init(canvasDom);
        myChart.showLoading();
        //图例
        var legend = [
            {
                name: 'User',
                icon: 'circle',
                color: '#CD5C5C'
            },
            {
                name: 'Tag',
                icon: 'circle',
                color: '#666699'
            }, {
                name: 'Weibo',
                icon: 'circle',
                color: '#B0E0E6'
            },
            {
                name: 'Comment',
                icon: 'circle',
                color: '#DAA520'
            }
        ];

        var edgeLegend = [
            {
                name: 'Author',
            },
            {
                name: 'Fans',
            },
            {
                name: 'At',
            },
            {
                name: 'Mentioned',
            },
            {
                name: 'ReplyOf'
            },
            {
                name: 'At | Author',
            },
        ];

        var graphData = [];
        var graphLink = [];
        /**
         *Record the time of the last update
         */
        var lastUpdateAt = (new Date()).valueOf();

        /**
         *Configuration items for the diagram
         */
        var option = {
            legend: [
                {
                    data: legend
                }
            ],
            animationDuration: 500,
            animationEasingUpdate: 'quinticInOut',
            tooltip: {
                triggerOn: "mousemove|click",
                trigger: "item",
                backgroundColor: "#fff",
                borderWidth: 1,
                borderRadius: 4,
                textStyle: {
                    color: "#666",
                    fontSize: 14
                },
                shadowBlur: 10,
                shadowColor: "rgba(0,0,0,0.2)",
                shadowOffsetX: 1,
                shadowOffsetY: 2,
                enterable: true,
                formatter: function (params) {
                    var checks = $('input[type=checkbox]');
                    var showInfo = checks[2].checked;
                    var result = ''
                    if (showInfo && params.data) {
                        var data = params.data
                        result = '<div style="text-align: left; max-width: 400px; padding-left: 12px; padding-right: 12px;">'
                        if (data.count)
                            result += '<div><span style="font-weight: bold">' + 'count: ' + '</span>' + data.count + '</div>';

                        if (data.properties) {
                            result += Object.keys(data.properties).map(i => {
                                return '<div><span style="font-weight: bold;">' + i + ': ' + '</span>' + '<span style="max-width: 350px; word-wrap:break-word;overflow: hidden;white-space: normal;">' + data.properties[i] + '</span' + '</div>';
                            }).join('')
                        }
                    }
                    return result + '</div>';
                }

            },
            series: [
                {
                    name: 'Weibo Data Display',
                    type: 'graph',
                    layout: 'force',
                    edgeSymbol: ['none', 'arrow'],
                    edgeLabel: {
                        fontSize: 20
                    },
                    data: graphData,
                    links: graphLink,
                    categories: [
                        {
                            name: 'User'
                        },
                        {
                            name: 'Tag'
                        },
                        {
                            name: 'Weibo'
                        },
                        {
                            name: 'Comment'
                        }
                    ],
                    roam: true,
                    label: {
                        show: true,
                    },
                    draggable: true,
                    lineStyle: {
                        color: 'source',
                        curveness: 0
                    },
                    force: {
                        edgeLength: 200,
                        repulsion: 200,
                        gravity: 0.05
                    },
                    emphasis: {
                        focus: 'adjacency',
                        lineStyle: {
                            width: 10
                        }
                    }
                }
            ]
        };
        /**
         *Use the specified configuration items and data to display the graph
         */
        myChart.hideLoading();
        myChart.setOption(option);

        dws.onmessage = function (data) {
            try {
                myChart.showLoading();

                var checks = $('input[type=checkbox]');
                var showVertexNum = checks[0].checked;
                var showEdgeNum = checks[1].checked;

                var graph = data.data ? JSON.parse(data.data) : null;
                var vertices = graph.vertices ? graph.vertices : null;
                var edges = graph.edges ? graph.edges : null;
                var styles = graph.styles ? graph.styles : null;
                /**
                 *  推数据的时候时间戳
                 */
                var timestamp = graph.timestamp;


                var newGraphData = vertices ? vertices.map(i => {
                    i.category = legend.map(i => i.name).indexOf(i.label);
                    i.name = i.label;
                    i.symbolSize = 40;
                    i.itemStyle = styles ? styles.find(j => j.name === i.label).style : "#666666";
                    i.symbol = legend.find(j => j.name === i.label).icon;
                    return i;
                }) : [];

                var newGraphLink = edges ? edges.map(i => {
                    i.label = {
                        show: true,
                        align: 'right',
                        fontSize: '12',
                        formatter: i.label
                    };
                    return i;
                }) : [];

                var selects = $('select');
                var numInputs = $('input[type=number]');

                var slideSize = numInputs[1].value;
                var slideUnit = selects[1].value;

                var maxTimeSize = lastUpdateAt + slideSize * mapToMills(slideUnit);
                if (timestamp > maxTimeSize) {
                    graphData.length = 0
                    graphLink.length = 0
                }
                /**
                 *如果推新数据的时间戳小于，则这个数据属于这个窗口内，则会和前端已有的数据做合并
                 */
                newGraphData.forEach(i => {
                    var oldItem = graphData.find(j => i.id === j.id)
                    if (oldItem) {
                        oldItem.count += i.count;
                    } else {
                        graphData.push(i);
                    }
                })
                /**
                 *边的逻辑和上面相同
                 */
                newGraphLink.forEach(i => {
                    var oldItem = graphLink.find(j => (i.id + i.source + i.target) === (j.id + j.source + j.target))
                    if (oldItem) {
                        oldItem.count += i.count;
                    } else {
                        graphLink.push(i);
                    }
                })
                lastUpdateAt = (new Date()).valueOf();

                option.series[0].data = graphData
                option.series[0].links = graphLink
               //  if (timestamp > maxTimeSize) {
               //     myChart.setOption(option, true);
               // } else {
               //     myChart.setOption(option);
               // }
                myChart.setOption(option);
                myChart.hideLoading();


                var btn = $('#submit-btn');
                btn.attr('disabled', false);
                var infoWrapper = $('<div></div>')
                infoWrapper.css({
                    position: 'absolute',
                    left: '320px',
                    top: '80px',
                    'pointer-events': 'none',
                    'text-align': 'left',
                    'max-width': '200px',
                    color: '#666',
                    'font-size': '16px'
                });
                var total = 0
                if (showVertexNum) {
                    total = 0;
                    graphData.forEach(i => {
                        total += i.count;
                    })
                    infoWrapper.append('<div class="information" style="margin-bottom: 20px;"><span style="font-weight: bold">Vertex numbers: </span>'
                        + total
                        + '<br />'
                        + legend.map(i => {
                            var subtotal = 0;
                            graphData.filter(j => j.label === i.name).forEach(vertex => {
                                subtotal += vertex.count;
                            });
                            return '<span style="font-weight: bold">' + i.name + ': </span>' + percent(total, subtotal);
                        }).join('<br />')
                        + ' </div></div>')
                }
                if (showEdgeNum) {
                    total = 0;
                    graphLink.forEach(i => {
                        total += i.count;
                    })
                    infoWrapper.append(
                        '<div class="information"><span style="font-weight: bold">Edge numbers: </span>'
                        + total
                        + '<br />'
                        + edgeLegend.map(i => {
                            var subtotal = 0;
                            graphLink.filter(j => j.label.formatter === i.name).forEach(edge => {
                                subtotal += edge.count;
                            });
                            return '<span style="font-weight: bold">' + i.name + ': </span>' + percent(total, subtotal);
                        }).join('<br />')
                        + ' </div></div>')
                }
                $(".information").remove()
                $(rightDom).append(infoWrapper)
            } catch (error) {
                console.error(error)
            }
        }
        return myChart;
    }

    function mapToMills(unit) {
        if (unit === '.milliseconds')
            return 1;
        if (unit === '.seconds')
            return 1000;
        if (unit === '.minutes')
            return 60000;
        if (unit === '.hours')
            return 3600000;
        return 0;
    }


    function percent(denominator, numerator) {
        if (denominator === 0) {
            return "0.00%";
        }
        var point = numerator / denominator;
        var str = Number(point * 100).toFixed(2);
        str += "%";
        return str;
    }

    function init() {
        initLabels();
        var cws = initControlWebsocket();
        var dws = initDataWebsocket();
        var graph = initGraph(dws);
        initFilterEvent(cws, graph);
    }

    init();

})();