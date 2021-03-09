<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" %>
<html>

<head>
    <meta http-equiv="X-UA-Compatible" content="IE=8">
    <!--meta http-equiv="refresh" content="10;url="/-->
    <!-- 使用IE8以上的渲染 -->
    <title>Graph Stream</title>
    <!-- <meta name="renderer" content="webkit|ie-comp|ie-stand"> -->
    <!-- 三个参数分别代表极速、兼容、标准模式，适用于360浏览器 -->
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <!--script type="text/javascript" charset="utf-8"></script-->
    <script type="text/javascript" src="./js/jquery-3.4.1.min.js"></script>
    <script type="text/javascript" src="./js/echarts.min.js"></script>
    <script type="text/javascript" src="./js/select2.min.js"></script>
    <link rel="stylesheet" href="./css/select2.min.css">
    <style>
        * {
            margin: 0;
            padding: 0;
        }

        .flex {
            display: flex;
            align-items: center;
            justify-content: space-between;
        }

        html,
        body,
        #left-side,
        #right-side {
            height: 100%;
        }

        #header {
            height: 50px;
            line-height: 50px;
            padding: 0 10px;
            background-color: #99CC99;
        }

        #main {
            height: calc(100% - 100px);
        }

        #left-side {
            width: 300px;
            background-color: #CCCC99;
            padding-left: 10px;
            text-align: center;
        }

        #right-side {
            width: calc(100% - 300px);
            background-color: aliceblue;
            padding-right: 10px;
            text-align: center;
        }

        footer {
            height: 50px;
            line-height: 50px;
            text-align: center;
        }

        .select-wrapper {
            padding: 0 20px;
        }

        .select-out-wrapper + .select-out-wrapper {
            margin-top: 20px;
        }

        .select-wrapper select, .select-wrapper input {
            width: 120px;
        }

        .part {
            margin-top: 10px;
        }

        .part-title {
            height: 40px;
            line-height: 40px;
            border-bottom: 1px solid #fff;
        }

        .part + .part {
            margin-top: 30px;
            border-top: 1px solid #fff;
        }

        .checkbox-wrapper {
            height: 30px;
            line-height: 30px;
            text-align-last: left;
            padding-left: 20px;
        }

        .checkbox-wrapper input + label {
            margin-left: 8px;
        }

        .checkbox-wrapper input {
            vertical-align: middle;
        }

        #submit-btn {
            box-sizing: content-box;
            margin-top: 15px;
            width: 80px;
            height: 30px;
            line-height: 30px;
            cursor: pointer;
        }
    </style>
</head>

<body>
<div id="container"></div>

<div id="header">
    <h1 style="margin-bottom:0;">Graph Stream</h1>
</div>
<section id="main" class="flex">
    <section id="left-side">
        <div class="part">
            <div class="part-title">Config</div>
            <div class="select-out-wrapper">
                <p>window size</p>
                <p class="select-wrapper flex">
                    <input type="number" id="window-input" value="30">
                    <select name="" id="">
                        <option value=".milliseconds">Milliseconds</option>
                        <option value=".seconds" selected="selected">Seconds</option>
                        <option value=".minutes">Minutes</option>
                        <option value=".hours">Hours</option>
                    </select>
                </p>
            </div>
            <div class="select-out-wrapper">
                <p>slider size</p>
                <p class="select-wrapper flex">
                    <input type="number" id="slider-input" value="10">
                    <select name="" id="">
                        <option value=".milliseconds">Milliseconds</option>
                        <option value=".seconds" selected="selected">Seconds</option>
                        <option value=".minutes">Minutes</option>
                        <option value=".hours">Hours</option>
                    </select>
                </p>
            </div>
        </div>
        <div class="part">
            <div class="part-title">Filter</div>
            <div class="select-out-wrapper">
                <p>Vertex</p>
                <p class="select-wrapper" style="text-align: left">
                    <select class="" name="" id="vertex-select" multiple="multiple" style="width: 100%"> </select>
                </p>
            </div>
            <div class="select-out-wrapper">
                <p>Edge</p>
                <p class="select-wrapper" style="text-align: left">
                    <select name="" id="edge-select" multiple="multiple"
                            style="width: 100%;"></select>
                </p>
            </div>
        </div>
        <div class="part">
            <div class="part-title">Display</div>
            <div class="select-out-wrapper">
                <p class="checkbox-wrapper">
                    <input type="checkbox" name="Vertex" id="c-Vertex"><label for="c-Vertex">Show Vertex Numbers</label>
                </p>
                <p class="checkbox-wrapper">
                    <input type="checkbox" name="Edge" id="c-Edge"><label for="c-Edge">Show Edge Numbers</label>
                </p>
                <p class="checkbox-wrapper">
                    <input type="checkbox" name="Informations" id="c-Informations"><label for="c-Informations">Show
                    Informations</label>
                </p>
                <p class="checkbox-wrapper">
                    <input type="checkbox" name="label" id="c-Label"><label for="c-Label">grouping by label</label>
                </p>
            </div>
            <div>
                <button id="submit-btn">submit</button>
            </div>
        </div>
        </div>


    </section>
    <section id="right-side">

    </section>
</section>
<footer>www.uni-leipzig.de</footer>
<script src="./js/index.js"></script>
</body>
</html>