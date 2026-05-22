<template>
    <!-- 注意，这里必须设置宽高，并且宽高不能都设置为100%，否则显示的是空白 -->
    <div id="china_map" style="width: 100%; height: 450px;">
    
    </div>
</template>;

<script>
import * as echarts from 'echarts';
import china from './china.json'; //中国地图
echarts.registerMap("china", china);
export default {
    props:{
        MapDataList:{
            type:Array,
        }
    },
    data() {
        return {
            //MapDataList:[]
        }
    },
    mounted() {
        this.setMapData();
        this.$nextTick(() => {
            this.setMapData();
        });
        
    },
    created(){
        //  函数
    },
    methods: {
        // 地图
        setMapData() {
            // 初始化echarts实例 #D8E9FD
            this.chinachart = echarts.init(document.getElementById("china_map"));
            // 进行相关配置
            this.chartOption = {
                tooltip: { // 鼠标移到图里面的浮动提示框
                    // formatter详细配置： https://echarts.baidu.com/option.html#tooltip.formatter
                    formatter(params, ticket, callback) {
                        // params.data 就是series配置项中的data数据遍历
                        let localValue, perf, downloadSpeep, usability, point;
                        if (params.data) {
                            localValue = params.data.value;
                            perf = params.data.perf;
                            downloadSpeep = params.data.downloadSpeep;
                            usability = params.data.usability;
                            point = params.data.point;
                        } else {// 为了防止没有定义数据的时候报错写的
                            localValue = 0;
                            perf = 0;
                        }
                        return `<div style='font-size:18px;'> ${params.name}</div>
                                <p style='text-align:left;margin-top:4px;'>数量：${localValue}<br/>占比：${perf}<br/></p>`;
                    },
                    backgroundColor: "#ff7f50", //提示标签背景颜色
                    textStyle: { color: "#fff" }, //提示标签字体颜色
                    padding: [10, 10, 10, 10],  // 设置上下的内边距为 10,左右的内边距为 10
                },
                visualMap: {
                    show: true,
                    //bottom: 20,
                    //left: 0,
                    //top: '30%',
                    text: ["200", "0"],
                    min: 0,
                    itemHeight: 380,  //图形的高度，即长条的高度。
                    color: [
                        '#ee6666',//红色
                        '#fc8452',//橙色
                        '#fac858',//黄色
                        '#9a60b4',//紫色
                        '#ea7ccc',//淡紫
                        '#3ba272',//绿色
                        '#91cc75',//浅绿
                        '#5470c6',//蓝色
                        '#73c0de',//淡蓝
                    ]
                },
                geo: {  // 地理坐标系组件用于地图的绘制// geo配置详解： https://echarts.baidu.com/option.html#geo
                    map: "china", // 表示中国地图
                    // roam: true, // 是否开启鼠标缩放和平移漫游
                    zoom: 1.2, // 当前视角的缩放比例（地图的放大比例）
                    label: {
                        show: true
                    },
                    emphasis: {  // 地图区域的多边形 图形样式。
                        borderColor: "#ffffff",//未选中的状态
                        areaColor: "#D8E9FD", //背景颜色
                        label: {
                            show: true, //显示名称
                        },
                        itemStyle: {  //选中的状态// 高亮状态下的多边形和标签样式
                            shadowBlur: 20,
                            shadowColor: "rgba(0, 0, 0, 0.5)",
                            borderColor: "#fff",
                            areaColor: "#DA3A3A",
                        },
                    },
                },
                series: [
                    {
                        name: "地图", // 浮动框的标题（上面的formatter自定义了提示框数据，所以这里可不写）
                        type: "map",
                        geoIndex: 0,
                        label: {
                            show: true,
                        },
                        // 这是需要配置地图上的某个地区的数据，根据后台的返回的数据进行拼接（下面是我定义的假数据）
                        data: this.MapDataList,
                    },
                ],
            };
            // 使用刚指定的配置项和数据显示地图数据
            this.chinachart.setOption(this.chartOption);
        },
    
        
    }
}
</script>
